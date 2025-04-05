package no.solcellepaneller.data.weatherdata
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.HttpHeaders
import io.ktor.client.call.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.statement.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlin.math.*
import java.time.ZonedDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class FrostApi {
    val baseUrl = "https://frost-beta.met.no/api/v1/obs/met.no/filter/get"
    val basicAuth = "4868c766-7477-484f-b767-6e5776a60a26:49ee1988-7461-4452-97a3-8ae5cbb133d7" // test auth
    val encodedAuth = java.util.Base64.getEncoder().encodeToString(basicAuth.toByteArray())
    val encode = { json: String -> URLEncoder.encode(json, StandardCharsets.UTF_8.toString()) }
    val radiusIncrement = 15.0

    @Serializable
    data class StationsResponse(
        val data: Data
    )

    @Serializable
    data class Data(
        val tstype: String,
        val tseries: List<TSeries>
    )

    @Serializable
    data class TSeries(
        val header: Header,
        val observations: String? = null
    )

    @Serializable
    data class Header(
        val id: Id,
        val extra: Extra,
        val available: TimeRange? = null
    )

    @Serializable
    data class Id(
        val level: Int,
        val parameterid: Int,
        val sensor: Int,
        val stationid: String
    )

    @Serializable
    data class Extra(
        val element: Element,
        val station: Station,
        val timeseries: TimeSeries
    )

    @Serializable
    data class Element(
        val description: String,
        val id: String,
        val name: String,
        val unit: String
    )

    @Serializable
    data class Station(
        val location: List<Location>,
        val shortname: String? = null
    )

    @Serializable
    data class Location(
        @Serializable(with = ZonedDateTimeSerializer::class) val from: ZonedDateTime,
        @Serializable(with = ZonedDateTimeSerializer::class) val to: ZonedDateTime,
        val value: LocationValue
    )

    @Serializable
    data class LocationValue(
        val latitude: Double? = null,
        val longitude: Double? = null,
        @SerialName("elevation(masl/hs)") val elevation: Double? = null
    )

    @Serializable
    data class TimeSeries(
        val quality: Quality,
        val timeoffset: String,
        val timeresolution: String? = null
    )

    @Serializable
    data class Quality(
        val exposure: List<QualityEntry>,
        val performance: List<QualityEntry>
    )

    @Serializable
    data class QualityEntry(
        val from: String,
        val to: String,
        val value: String
    )

    @Serializable
    data class TimeRange(
        @Serializable(with = ZonedDateTimeSerializer::class) val from: ZonedDateTime? = null,
        @Serializable(with = ZonedDateTimeSerializer::class) val to: ZonedDateTime? = null
    )


    object ZonedDateTimeSerializer : KSerializer<ZonedDateTime> {
        private val formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ZonedDateTime", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: ZonedDateTime) {
            encoder.encodeString(value.format(formatter))
        }
        override fun deserialize(decoder: Decoder): ZonedDateTime {
            return ZonedDateTime.parse(decoder.decodeString(), formatter)
        }
    }


    @Serializable
    data class ObservationsResponse(
        @SerialName("@context") val context: String,
        @SerialName("@type") val type: String,
        val apiVersion: String,
        val license: String,
        val createdAt: String,
        val queryTime: Double,
        val currentItemCount: Int,
        val itemsPerPage: Int,
        val offset: Int,
        val totalItemCount: Int,
        val currentLink: String,
        val data: List<ObservationData>
    )

    @Serializable
    data class ObservationData(
        val sourceId: String,
        @Serializable(with = ZonedDateTimeSerializer::class) val referenceTime: ZonedDateTime,
        val observations: List<Observation>
    )

    @Serializable
    data class Observation(
        val elementId: String,
        val value: Double,
        val unit: String,
        val timeOffset: String,
        val timeResolution: String,
        val timeSeriesId: Int,
        val performanceCategory: String,
        val exposureCategory: String,
        val qualityCode: Int? = null,
        val level: ObservationLevel? = null
    )

    @Serializable
    data class ObservationLevel(
        val levelType: String,
        val unit: String,
        val value: Int
    )

    fun latPlusKm(latitude: Double, deltaKm: Double): Double {
        val kmPerDegreeLat = 111.32
        val newLat = latitude + (deltaKm / kmPerDegreeLat)
        return newLat
    }

    fun lonPlusKm(longitude: Double, latitude: Double, deltaKm: Double): Double {
        val kmPerDegreeLon = 111.32 * cos(Math.toRadians(latitude))
        val newLon = longitude + (deltaKm / kmPerDegreeLon)
        return newLon
    }

    fun formatPolygons(polygons: MutableList<List<Pair<Double, Double>>>): String {
        var bigString = "["
        polygons.forEachIndexed { index1, coordinateList ->
            var smallString = ""
            if (index1 > 0) smallString += ","
            smallString += """{"type":"polygon","pos":["""
            coordinateList.forEachIndexed { index2, coordinates ->
                if (index2 > 0) smallString += ","
                smallString += """{"lat":${String.format("%.5f", coordinates.first).toDouble()},"lon":${String.format("%.5f", coordinates.second).toDouble()}}"""
            }
            smallString += "]}"
            bigString += smallString
        }
        bigString += "]"
        return bigString
    }

    fun determineQuadrant(center: LocationValue, test: LocationValue): Quadrant {
        val centerLat = center.latitude!!.toDouble()
        val centerLon = center.longitude!!.toDouble()
        val testLat = test.latitude!!.toDouble()
        val testLon = test.longitude!!.toDouble()

        return when { // could probably use the pairs to simplify but idk how
            testLat <= centerLat && testLon <= centerLon -> Quadrant.SOUTHWEST
            testLat <= centerLat && testLon >= centerLon -> Quadrant.SOUTHEAST
            testLat >= centerLat && testLon >= centerLon -> Quadrant.NORTHEAST
            testLat >= centerLat && testLon <= centerLon -> Quadrant.NORTHWEST
            else -> Quadrant.NORTHWEST // idk how to avoid
        }
    }

    fun calculateDistance(location1: LocationValue, location2: LocationValue): Double {
        val lat1 = Math.toRadians(location1.latitude!!)
        val lon1 = Math.toRadians(location1.longitude!!)
        val lat2 = Math.toRadians(location2.latitude!!)
        val lon2 = Math.toRadians(location2.longitude!!)

        val dlat = lat2 - lat1
        val dlon = lon2 - lon1
        val a = sin(dlat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dlon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val radius = 6371.0
        return radius * c
    }

    enum class Mode {
        INTERPOLATION, EXTRAPOLATION, GUESSTIMATION
    }

    class StochasticSet : java.util.TreeSet<Int>() {
        private var nextConsecutive = 2 // Begins at 2, because initial circle occupies 1
        override fun add(element: Int): Boolean {
            val added = super.add(element)
            if (added) {
                if (element == nextConsecutive) {
                    while (contains(nextConsecutive)) {
                        nextConsecutive++
                    }
                }
            }
            return added
        }

        fun nextGap(): Int {
            return nextConsecutive
        }
    }

    fun setModes(modes: MutableMap<String, Mode>, intentionalSearches: Map<String, MutableMap<Quadrant, Int>>) {
        intentionalSearches.forEach { (element, quadrants) ->
            var exceeders = 0
            quadrants.forEach { (quadrant, value) ->
                if (value > 4) exceeders++ // if 150km
            }
            /*if (exceeders > 1) { // it should check the angles for this, actually
                modes[element] = Mode.EXTRAPOLATION
                println("SET TO EXTRAPOLATION: "+element)
            } else*/ if (exceeders > 1 /*3*/) {
            modes[element] = Mode.GUESSTIMATION
            //println("SET TO GUESSTIMATION: "+element)
        }
        }
    }

    /*
    fun calculateClockwiseAngle(a: LocationValue, b: LocationValue, c: LocationValue): Double {
        val ax = a.latitude!!
        val ay = a.longitude!!
        val bx = b.latitude!!
        val by = b.longitude!!
        val cx = c.latitude!!
        val cy = c.longitude!!

        val vectorCAx = ax - cx
        val vectorCAy = ay - cy
        val vectorCBx = bx - cx
        val vectorCBy = by - cy

        val dotProduct = vectorCAx * vectorCBx + vectorCAy * vectorCBy
        val crossProduct = vectorCAx * vectorCBy - vectorCAy * vectorCBx
        val magnitudeCA = sqrt(vectorCAx.pow(2) + vectorCAy.pow(2))
        val magnitudeCB = sqrt(vectorCBx.pow(2) + vectorCBy.pow(2))
        if (magnitudeCA == 0.0 || magnitudeCB == 0.0) return 0.0
        val angleRad = acos(dotProduct / (magnitudeCA * magnitudeCB))
        var angleDeg = Math.toDegrees(angleRad)
        if (crossProduct < 0) {
            angleDeg = 360 - angleDeg
        }

        return angleDeg
    }

    fun isClockwise(a: LocationValue, b: LocationValue, c: LocationValue): Boolean {
        val cross = (b.latitude!! - c.latitude!!) * (a.longitude!! - c.longitude!!) - (b.longitude!! - c.longitude!!) * (a.latitude!! - c.latitude!!)
        return cross <= 0
    }
    */


    val quadrantAngles = mapOf(
        Quadrant.NORTHEAST to 0.0,
        Quadrant.SOUTHEAST to 90.0,
        Quadrant.SOUTHWEST to 180.0,
        Quadrant.NORTHWEST to 270.0
    )

    enum class Quadrant(val value: Pair<Int, Int>) { // use for readability. replace every instance of quadrants with this
        SOUTHWEST(-1 to -1),
        SOUTHEAST(-1 to 1),
        NORTHEAST(1 to 1),
        NORTHWEST(1 to -1)
    }

    fun makeAnnularSector(
        center: LocationValue,
        radius: Double,
        quadrant: Quadrant
    ): List<Pair<Double, Double>> {
        val points = 5
        val sectorPoints = mutableListOf<Pair<Double, Double>>()

        val innerRadius = radius - radiusIncrement
        val startAngle = quadrantAngles[quadrant]!!
        val endAngle = startAngle + 90.0

        val startRad = Math.toRadians(startAngle)
        val endRad = Math.toRadians(endAngle)

        for (i in 0..points) {
            val angle = startRad + (i / points.toDouble()) * (endRad - startRad)
            val lat = latPlusKm(center.latitude!!, cos(angle) * radius)
            val lon = lonPlusKm(center.longitude!!, center.latitude!!, sin(angle) * radius)
            sectorPoints.add(Pair(lat, lon))
        }

        for (i in points downTo 0) {
            val angle = startRad + (i / points.toDouble()) * (endRad - startRad)
            val lat = latPlusKm(center.latitude!!, cos(angle) * innerRadius)
            val lon = lonPlusKm(center.longitude!!, center.latitude!!, sin(angle) * innerRadius)
            sectorPoints.add(Pair(lat, lon))
        }

        val readOnlySectorPoints: List<Pair<Double, Double>> = sectorPoints
        return readOnlySectorPoints
    }

    suspend fun getStations(
        client: HttpClient,
        center: LocationValue,
        requestedQuadrants: MutableMap<String, MutableSet<Quadrant>>,
        searchedAreas: Map<String, Map<Quadrant, StochasticSet>>,
        intentionalSearches: Map<String, MutableMap<Quadrant, Int>>,
        elementsConst: List<String>
    ): HttpResponse {
        var elements: String
        var polygons: MutableList<List<Pair<Double, Double>>> = mutableListOf() // if two elements in the same quadrant are at different progress, it makes another polygon. if they are at the same progress, it ignores it if it already made a polygon there. change to LocationValue

        //one polygonmaking function, which simply checks "if progress is at one" to know if to make inner circle or not and combine the polygons into one

        if (requestedQuadrants.isEmpty()) {
            // use inside circle instead of making a polygonal circle?
            val radius = 15
            val points = 20 // 4 quadrants * 10 points
            val circle = MutableList(points) { index ->
                val angle = (index.toDouble() / points) * 2 * Math.PI
                val lat = latPlusKm(center.latitude!!, cos(angle) * radius)
                val lon = lonPlusKm(center.longitude!!, center.latitude!!, sin(angle) * radius)
                Pair(lat, lon)
            }
            polygons.add(circle)
            elements = encode(elementsConst.joinToString(","))
        } else {
            val missingElements: MutableSet<String> = mutableSetOf()
            var currentAreas: Map<Quadrant, MutableSet<Int>> = enumValues<Quadrant>().associateWith { mutableSetOf() }
            requestedQuadrants.forEach { (element, quadrants) ->
                missingElements.add(element)
                val searchElement = searchedAreas[element]!!
                quadrants.forEach { quadrant ->
                    val gap = searchElement[quadrant]!!.nextGap()
                    searchElement[quadrant]!!.add(gap) // should maybe add it after request returns with either missing or success
                    intentionalSearches[element]!![quadrant] = intentionalSearches[element]!![quadrant]!! + 1 // same here
                    if (!currentAreas[quadrant]!!.contains(gap)) {
                        currentAreas[quadrant]!!.add(gap)
                        val radius = gap * radiusIncrement
                        polygons.add(makeAnnularSector(center, radius, quadrant))
                    }
                }
            }
            elements = encode(missingElements.joinToString(","))
        }

        val url = buildString {
            append("$baseUrl?incobs=false&elementids=$elements")
            append("&inside="+encode(formatPolygons(polygons)))
        }
        //println(url)

        val response: HttpResponse = client.get(url) {
            headers {
                append(HttpHeaders.Authorization, "Basic $encodedAuth")
            }
        }

        return response
    }

    fun testWeight(intentionalSearches: Map<String, MutableMap<Quadrant, Int>>, element: String, quadrant: Quadrant): Double {
        var sum = 0.0
        var thisDistance = 0.0
        intentionalSearches[element]!!.forEach { (intentionalQuadrant, distance) ->
            val useDistance = if (quadrant == intentionalQuadrant) {
                thisDistance = distance.toDouble() + 1.0
                distance + 1
            } else {
                distance
            }
            val weight = 1.0 / useDistance.toDouble().pow(2) // We know distance is never 0
            sum += weight
        }
        return (1/thisDistance.toDouble().pow(2)) / sum
        // connect this weighing to determining if a quadrant is defunct, and thus whether to extrapolate, guesstimate, etc.
    }

    fun assignStations(
        center: LocationValue,
        responseBody: StationsResponse?,
        stationLocations: MutableMap<String, LocationValue>,
        stationQueues: Map<String, Map<Quadrant, MutableList<String>>>,
        modes: MutableMap<String, Mode>,
        elementConst: List<String>,
        intentionalSearches: Map<String, MutableMap<Quadrant, Int>>
    ): MutableMap<String, MutableSet<Quadrant>> {
        val appendix: Map<String, Map<Quadrant, MutableList<Pair<String, Double>>>> = elementConst.associateWith { element -> Quadrant.values().associateWith { mutableListOf<Pair<String, Double>>() } }
        val requestedQuadrants: MutableMap<String, MutableSet<Quadrant>> = mutableMapOf()

        if (responseBody != null) {
            val tseries = responseBody.data.tseries
            tseries.forEach { tserie ->
                val header = tserie.header
                val stationid = header.id.stationid
                val firstLocationValue = header.extra.station.location[0].value
                // We assume the first location value is about representative of all
                stationLocations[stationid] = firstLocationValue

                val elementid = tserie.header.extra.element.id

                val quadrant = determineQuadrant(center, firstLocationValue)
                val distance = calculateDistance(center, firstLocationValue)

                appendix[elementid]!![quadrant]!!.add(Pair(stationid, distance))
            }
        }
        stationQueues.forEach { (element, quadrants) ->
            val mode = modes[element]
            quadrants.forEach { (quadrant, queue) ->
                if (responseBody != null) {
                    appendix[element]!![quadrant]!!.sortBy{ it.second }
                    queue.addAll(appendix[element]!![quadrant]!!.map { it.first })
                }
                if (queue.size == 0) {
                    if (mode == Mode.INTERPOLATION) { // tests if it is worth to keep searching in a quadrant
                        if (testWeight(intentionalSearches, element, quadrant) > 0.05) {
                            requestedQuadrants.getOrPut(element) { mutableSetOf() }.add(quadrant)
                        }
                    } else if (mode == Mode.EXTRAPOLATION) {
                        // TBD
                    } else if (mode == Mode.GUESSTIMATION) {
                        // TBD
                    }
                }
            }
        }
        //val readOnlyRequestedQuadrants: Map<String, MutableSet<Quadrant>> = requestedQuadrants
        return requestedQuadrants
    }

    suspend inline fun <reified T> handleResponse(response: HttpResponse): T? {
        val responseStatus = response.status.value
        var rawData: T?
        if (responseStatus == 200) {
            rawData = response.body()
        }
        else if (responseStatus == 404) { // Means Frost didn't have any data for that
            rawData = null // temp
        } else {
            //println(response.status.value) // error handling idk
            rawData = null // temp
        }
        return rawData
    }

    suspend fun getStationData(client: HttpClient, stationids: MutableSet<String>, missingElements: MutableSet<String>): HttpResponse {
        val sources = stationids.joinToString(",") { "SN$it" }
        //val time = timeRange.from.toString() + "/" + timeRange.to.toString()
        val from = ZonedDateTime.of(1800, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
        val to = ZonedDateTime.now(ZoneId.of("UTC"))
        val formatter = DateTimeFormatter.ISO_INSTANT
        val time = "${from.format(formatter)}/${to.format(formatter)}"
        val elements = encode(missingElements.joinToString(","))

        val url = "https://frost.met.no/observations/v0.jsonld?sources=$sources&referencetime=$time&elements=$elements"

        //println(url)

        val response: HttpResponse = client.get(url) {
            headers {
                append(HttpHeaders.Authorization, "Basic $encodedAuth")
            }
        }

        return response
    }

    fun assignData(
        rawData: ObservationsResponse,
        stationMonthData: Map<String, MutableMap<String, MutableMap<Int, Pair<Double, Int>>>>
    ) {
        rawData.data.forEach { observationData ->
            val id = observationData.sourceId.substringBeforeLast(":").removePrefix("SN") // Because Frost non-beta puts :0 at the end of station ids for whatever reason
            val month = observationData.referenceTime.monthValue
            observationData.observations.forEach { observation ->
                val element = observation.elementId
                val value = observation.value
                var useValue = value
                val monthData = stationMonthData[element]!!.getOrPut(id) { mutableMapOf() }
                if (element.contains("snow_coverage_type")) {
                    if (value == -1.0) {
                        useValue = 0.0
                    }
                } else if (element.contains("cloud_area_fraction")) {
                    if (value == -3.0 || value == 9.0) {
                        useValue = 5.0 // "average value" for cloud cover
                    }
                }
                if (monthData.contains(month)) {
                    val (prevValue, prevCount) = stationMonthData[element]!![id]!![month]!!
                    stationMonthData[element]!![id]!![month] = Pair(prevValue+useValue, prevCount+1)
                } else {
                    stationMonthData[element]!![id]!![month] = Pair(useValue, 1)
                }
            }
        }
    }

    fun getNexts(
        requestedQuadrants: MutableMap<String, MutableSet<Quadrant>>,
        usableStations: Map<String, Map<Quadrant, MutableList<String>>>,
        stationQueues: Map<String, Map<Quadrant, MutableList<String>>>,
        queueAdvancements: Map<String, MutableMap<Quadrant, Int>>,
        stationMonthData: Map<String, MutableMap<String, MutableMap<Int, Pair<Double, Int>>>>,
        intentionalSearches: Map<String, MutableMap<Quadrant, Int>>,
        modes: MutableMap<String, Mode>
    ): Pair<MutableSet<String>, MutableSet<String>> { // Returns missingids and missingelements
        val checkQuadrants: MutableMap<String, MutableSet<Quadrant>> = mutableMapOf()
        val missingIds: MutableSet<String> = mutableSetOf()
        val missingElements: MutableSet<String> = mutableSetOf()

        usableStations.forEach { (element, quadrants) ->
            val mode = modes[element]!!
            quadrants.forEach { (quadrant, usableQueue) ->
                val advancement = queueAdvancements[element]!![quadrant]!!
                if (advancement > 0) {
                    // stationqueue is empty when it gives up due to too low weight or due to changing mode
                    if (advancement <= stationQueues[element]!![quadrant]!!.size) {
                        val stationid = stationQueues[element]!![quadrant]!![advancement-1]
                        stationMonthData[element]!!.get(stationid)?.takeIf { it.size == 12 }?.let { // If it has at least one full year
                            usableQueue.add(stationid)
                        }
                        queueAdvancements[element]!![quadrant] = (advancement * -1) // Means it has already tested this
                    }
                }
                if (mode == Mode.INTERPOLATION) {
                    if (usableStations[element]!![quadrant]!!.size == 0) {
                        checkQuadrants.getOrPut(element) { mutableSetOf() }.add(quadrant)
                    }
                } else if (mode == Mode.EXTRAPOLATION) {
                    // TBD
                } else if (mode == Mode.GUESSTIMATION) {
                    // TBD
                }
            }
        }
        checkQuadrants.forEach { (element, quadrants) ->
            quadrants.forEach { quadrant ->
                val nextAdvancement = abs(queueAdvancements[element]!![quadrant]!!) + 1
                if (nextAdvancement <= stationQueues[element]!![quadrant]!!.size) {
                    missingIds.add(stationQueues[element]!![quadrant]!![nextAdvancement-1]!!)
                    missingElements.add(element)
                    queueAdvancements[element]!![quadrant] = nextAdvancement
                } else {
                    val mode = modes[element]
                    if (mode == Mode.INTERPOLATION) {
                        if (testWeight(intentionalSearches, element, quadrant) > 0.05) {
                            requestedQuadrants.getOrPut(element) { mutableSetOf() }.add(quadrant)
                        } else if (mode == Mode.EXTRAPOLATION) {
                            // TBD
                        } else if (mode == Mode.GUESSTIMATION) {
                            // TBD
                        }
                    }
                }
            }
        }
        return Pair(missingIds, missingElements)
    }

    fun formatData(
        center: LocationValue,
        stationLocations: MutableMap<String, LocationValue>,
        stationMonthData: Map<String, MutableMap<String, MutableMap<Int, Pair<Double, Int>>>>,
        usableStations: Map<String, Map<Quadrant, MutableList<String>>>,
        modes: MutableMap<String, Mode>,
        elementsConst: List<String>
    ): Map<String, Array<Double>> { // returns elements to month averages
        val resultsFormatted: MutableMap<String, Array<Double>> = mutableMapOf()
        usableStations.forEach { (element, quadrants) ->
            val mode = modes[element]
            if (mode == Mode.INTERPOLATION) {
                val valuesHolder: MutableMap<String, Array<Double>> = mutableMapOf()
                quadrants.forEach { (quadrant, stations) ->
                    if (stations.size > 0) {
                        val station = stations[0]
                        val monthArray = Array(12) { 0.0 }
                        val monthData = stationMonthData[element]!![station]!!
                        monthData.forEach { (month, pair) ->
                            val (sum, count) = pair
                            monthArray[month-1] = sum / count.toDouble()
                        }
                        valuesHolder[station] = monthArray
                    }
                }
                resultsFormatted[element] = getIDWAverages(center, valuesHolder, stationLocations)
            }
        }
        val readOnlyResultsFormatted: Map<String, Array<Double>> = resultsFormatted
        return readOnlyResultsFormatted
    }

    fun getIDWAverages(center: LocationValue, valuesHolder: MutableMap<String, Array<Double>>, stationLocations: MutableMap<String, LocationValue>): Array<Double> {
        val averagedArray = Array(12) { 0.0 }
        val distances = valuesHolder.map { (id, _) -> id to calculateDistance(center, stationLocations[id]!!) }
        val power = 2
        var rawWeightSum = 0.0
        val rawWeights: Map<String, Double> = distances.map { (id, distance) ->
            val rawWeight = if (distance == 0.0) 1.0 else 1 / distance.pow(power)
            rawWeightSum += rawWeight
            id to rawWeight
        }.toMap()
        valuesHolder.forEach { (id, months) ->
            val weight = rawWeights[id]!! / rawWeightSum
            months.forEachIndexed { index, value ->
                averagedArray[index] = averagedArray[index] + (value * weight)
            }
        }
        return averagedArray
    }

    suspend fun fetchFrostData(
        lat: Double,
        lon: Double,
        elements: List<String>
    ): Map<String, Array<Double>> {

        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
//            install(HttpTimeout){
//                requestTimeoutMillis = 200_000
//                connectTimeoutMillis = 200_000
//                socketTimeoutMillis = 200_000
//            }

        }
        //val center = LocationValue(59.91, 10.75) // middle of oslo
        //val center = LocationValue(60.386163, 8.259478) // middle of the mountains
        val center = LocationValue(lat, lon)
        val elementsConst = elements

        val stationLocations: MutableMap<String, LocationValue> = mutableMapOf() //id to distances and angle from straight up, for easy weight recalculation. if a station is already in, it does not re-add it (e.g. if same station different element)
        val stationMonthData: Map<String, MutableMap<String, MutableMap<Int, Pair<Double, Int>>>> = elementsConst.associateWith { mutableMapOf() }// element to id to month to sum to counter. separated by elements because the same station can have multiple elements.
        val stationQueues: Map<String, Map<Quadrant, MutableList<String>>> = elementsConst.associateWith {
            enumValues<Quadrant>().associateWith { mutableListOf() } } //quadrants for each element. each quadrant points to a queue of stations from the closest to the furthest. where when a station. the sorting of the queues are done before adding, not by adding
        val queueAdvancements: Map<String, MutableMap<Quadrant, Int>> = elementsConst.associateWith {
            enumValues<Quadrant>().associateWith { 1 }.toMutableMap() } // elements to quadrants to a counter of where you last dropped off in the queue. they begin at one, and all indexing require -1 on it
        val usableStations: Map<String, Map<Quadrant, MutableList<String>>> = elementsConst.associateWith {
            enumValues<Quadrant>().associateWith { mutableListOf() } } // stationQueues but only the usable stations // elements to quadrants
        val searchedAreas: Map<String, Map<Quadrant, StochasticSet>> = elementsConst.associateWith {
            enumValues<Quadrant>().associateWith { StochasticSet() } } // elements to quadrant to each increment searched. why the list? well, because we search with multiple elements at once, so we might pick up increment areas stochastically. why do we have this? well, because we say might want to pick up searching in certain areas when switching to extrapolation. that it does not include the areas of the initial circle works fine, but might not be intuitive and should be changed?
        val intentionalSearches: Map<String, MutableMap<Quadrant, Int>> = elementsConst.associateWith {
            enumValues<Quadrant>().associateWith { 1 }.toMutableMap() } // this is the one that modes uses to figure out whether to begin extrapolating? Set to 1 because everything gets searched once for the initial circle
        val modes: MutableMap<String, Mode> = elementsConst.associateWith { Mode.INTERPOLATION }.toMutableMap()// elements to mode

        var requestedQuadrants: MutableMap<String, MutableSet<Quadrant>> = mutableMapOf()
        var first = true
        while (first == true || requestedQuadrants.isNotEmpty()) {
            first = false
            while (true) {
                val response = getStations(client, center, requestedQuadrants, searchedAreas, intentionalSearches, elementsConst) // searches until all quadrants are filled or not worthwhile filling. puts them into the queue by proximity. the queue assumes all stations gotten after each progress is further away from center, which we will do by making the polygons circleoid. also assigns stationlocations. remember to increment intentionalsearches?
                val rawData: StationsResponse? = handleResponse(response)
                setModes(modes, intentionalSearches) // if it has gone 250km in two directions (which means it has not found anything in them) (it can well interpolate with three quadrants, but four is optimal). if the two directions are diagonal to each other, it's also fine however. if it has found nothing in 250km in all directions, it goes to guesstimation
                requestedQuadrants = assignStations(center, rawData, stationLocations, stationQueues, modes, elementsConst, intentionalSearches) // extrapolation makes it stop requesting quadrants it doesn't have
                /*println(requestedQuadrants)
                //println(stationLocations)
                //println(stationMonthData)
                println(stationQueues)
                println(queueAdvancements)
                println(usableStations)
                println(searchedAreas)
                println(intentionalSearches)
                println(modes)*/
                if (requestedQuadrants.isEmpty()) break
            }
            while (true) {
                val (missingIds, missingElements) = getNexts(requestedQuadrants, usableStations, stationQueues, queueAdvancements, stationMonthData, intentionalSearches, modes) // goes through the queue to where we last left off. don't even bother with timerange finding, just get from 1800 to currentdate. if the queue is at max, adds the missing data to requestedQuadrants instead of checkQuadrants
                if (missingIds.isEmpty()) break
                val response = getStationData(client, missingIds, missingElements) //simply requests the data
                val rawData: ObservationsResponse? = handleResponse(response)
                if (rawData != null) {
                    assignData(rawData, stationMonthData) // averages the data into stationMonthData. choosing to put data there depending on the mode it will use later, since calculating all of them will be too expensive. if a data does not have a combined full year available, it requests a new one. if data wasn't sufficient, it advances the queue on it. if the data was found, it adds it to usableStations, which we will use for interpolation. if for extrapolation, we need multiple in usableStations
                }
                /*println(requestedQuadrants)
                //println(stationLocations)
                //println(stationMonthData)
                println(stationQueues)
                println(queueAdvancements)
                println(usableStations)
                println(searchedAreas)
                println(intentionalSearches)
                println(modes)*/
            }
            // if it fails to find data in 150km for both sides, it outright fails, otherwise, the polation is supposed to be fairly strong. if it has for one side, but fails the other, it activates extrapolationmode, which then uses the stationQueues of the side it does have to do so
        }

        /*println(usableStations)
        println("success?")*/
        val dataF = formatData(center, stationLocations, stationMonthData, usableStations, modes, elementsConst) // also calculates the final weights. guesstimation also done here
        //File("testfile123.txt").writeText(Json.encodeToString(dataF))
        /*dataF.forEach { (key, values) ->
            println("$key: ${values.contentToString()}")
        }*/
        return dataF
    }
}
