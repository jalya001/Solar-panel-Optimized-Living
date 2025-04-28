package no.solcellepanelerApp.data.weatherdata

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class FrostApi {
    private val basicAuth = "4868c766-7477-484f-b767-6e5776a60a26:49ee1988-7461-4452-97a3-8ae5cbb133d7"
    private val encodedAuth = java.util.Base64.getEncoder().encodeToString(basicAuth.toByteArray())
    private val encode = { json: String -> URLEncoder.encode(json, StandardCharsets.UTF_8.toString()) }
    private val radiusIncrement = 20.0


    @Serializable
    private data class StationsResponse(
        val data: Data
    )

    @Serializable
    private data class Data(
        val tstype: String,
        val tseries: List<TSeries>
    )

    @Serializable
    private data class TSeries(
        val header: Header,
        val observations: String? = null
    )

    @Serializable
    private data class Header(
        val id: Id,
        val extra: Extra,
        val available: TimeRange? = null
    )
    private object IntAsStringSerializer : KSerializer<String> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("IntAsString", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: String) { encoder.encodeString(value) }
        override fun deserialize(decoder: Decoder): String { return decoder.decodeInt().toString() }
    }

    @Serializable
    private data class Id(
        val level: Int,
        val parameterid: Int,
        val sensor: Int,
        @Serializable(with = IntAsStringSerializer::class)
        val stationid: String
    )

    @Serializable
    private data class Extra(
        val element: Element,
        val station: Station,
        val timeseries: TimeSeries
    )

    @Serializable
    private data class Element(
        val description: String,
        val id: String,
        val name: String,
        val unit: String
    )

    @Serializable
    private data class Station(
        val location: List<Location>,
        val shortname: String? = null
    )

    @Serializable
    private data class Location(
        @Serializable(with = ZonedDateTimeSerializer::class) val from: ZonedDateTime,
        @Serializable(with = ZonedDateTimeSerializer::class) val to: ZonedDateTime,
        val value: LocationValue
    )

    @Serializable
    private data class LocationValue(
        val latitude: Double? = null,
        val longitude: Double? = null,
        @SerialName("elevation(masl/hs)") val elevation: Double? = null
    )

    @Serializable
    private data class TimeSeries(
        val quality: Quality,
        val timeoffset: String,
        val timeresolution: String? = null
    )

    @Serializable
    private data class Quality(
        val exposure: List<QualityEntry>,
        val performance: List<QualityEntry>
    )

    @Serializable
    private data class QualityEntry(
        val from: String,
        val to: String,
        val value: String
    )

    @Serializable
    private data class TimeRange(
        @Serializable(with = ZonedDateTimeSerializer::class) val from: ZonedDateTime? = null,
        @Serializable(with = ZonedDateTimeSerializer::class) val to: ZonedDateTime? = null
    )


    private object ZonedDateTimeSerializer : KSerializer<ZonedDateTime> {
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
    private data class ObservationsResponse(
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
    private data class ObservationData(
        val sourceId: String,
        @Serializable(with = ZonedDateTimeSerializer::class) val referenceTime: ZonedDateTime,
        val observations: List<Observation>
    )

    @Serializable
    private data class Observation(
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
    private data class ObservationLevel(
        val levelType: String,
        val unit: String,
        val value: Int
    )


    private fun latPlusKm(latitude: Double, deltaKm: Double): Double {
        val kmPerDegreeLat = 111.32
        val newLat = latitude + (deltaKm / kmPerDegreeLat)
        return newLat
    }

    private fun lonPlusKm(longitude: Double, latitude: Double, deltaKm: Double): Double {
        val kmPerDegreeLon = 111.32 * cos(Math.toRadians(latitude))
        val newLon = longitude + (deltaKm / kmPerDegreeLon)
        return newLon
    }

    private fun formatPolygons(polygons: MutableList<List<Pair<Double, Double>>>): String {
        var bigString = "["
        polygons.forEachIndexed { index1, coordinateList ->
            var smallString = ""
            if (index1 > 0) smallString += ","
            smallString += """{"type":"polygon","pos":["""
            coordinateList.forEachIndexed { index2, coordinates ->
                if (index2 > 0) smallString += ","
                smallString += """{"lat":${String.format(java.util.Locale.US, "%.4f", coordinates.first).toDouble()},"lon":${String.format(java.util.Locale.US, "%.4f", coordinates.second).toDouble()}}""" // They say on api.met.no ToS 4 decimals is max, so 4 it is
            }
            smallString += "]}"
            bigString += smallString
        }
        bigString += "]"
        return bigString
    }

    private fun determineQuadrant(center: LocationValue, test: LocationValue): Quadrant {
        val centerLat = center.latitude!!.toDouble()
        val centerLon = center.longitude!!.toDouble()
        val testLat = test.latitude!!.toDouble()
        val testLon = test.longitude!!.toDouble()

        return when { // could probably use the pairs to simplify but idk how
            testLat <= centerLat && testLon <= centerLon -> Quadrant.SOUTHWEST
            testLat <= centerLat && testLon >= centerLon -> Quadrant.SOUTHEAST
            testLat >= centerLat && testLon >= centerLon -> Quadrant.NORTHEAST // Gives warning, but it's clearer this way? Doesn't need to change if so.
            testLat >= centerLat && testLon <= centerLon -> Quadrant.NORTHWEST
            else -> Quadrant.NORTHWEST // idk how to avoid
        }
    }

    private fun calculateDistance(location1: LocationValue, location2: LocationValue): Double {
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

    private enum class Mode {
        NEAREST, INTERPOLATION, EXTRAPOLATION, FAIL // Fail means you need to add a guesstimate value in the viewmodel or something
    }

    private fun setModes(modes: MutableMap<String, Mode>, searchAdvancements: Map<String, MutableMap<Quadrant, Int>>) {
        println(modes)
        searchAdvancements.forEach { (element, quadrants) ->
            if (modes[element] != Mode.NEAREST && modes[element] != Mode.FAIL) resetMode(modes, element, quadrants)
        }
        println(modes)
    }

    private fun isDiagonal(q1: Quadrant, q2: Quadrant): Boolean {
        return !((q1.value.first == q2.value.first && abs(q1.value.second - q2.value.second) == 2) || (q1.value.second == q2.value.second && abs(q1.value.first - q2.value.first) == 2))
    }

    private fun resetMode(modes: MutableMap<String, Mode>, element: String, quadrants: MutableMap<Quadrant, Int>) {
        val exceeders = mutableListOf<Quadrant>()
        quadrants.forEach { (quadrant, value) ->
            if (value >= 4) exceeders.add(quadrant)
        }
        println(element)
        println(exceeders)
        if (exceeders.size <= 1 || (exceeders.size == 2 && isDiagonal(exceeders[0], exceeders[1]))) {
            println("SET TO INTERPOLATION: "+element)
            modes[element] = Mode.INTERPOLATION
        } else if (exceeders.size == 4) {
            modes[element] = Mode.FAIL
            println("SET TO FAIL: "+element)
        } else if (modes[element] != Mode.EXTRAPOLATION) {
            modes[element] = Mode.EXTRAPOLATION
            println("SET TO EXTRAPOLATION: "+element)
        }
    }

    private val quadrantAngles = mapOf(
        Quadrant.NORTHEAST to 0.0,
        Quadrant.SOUTHEAST to 90.0,
        Quadrant.SOUTHWEST to 180.0,
        Quadrant.NORTHWEST to 270.0
    )

    private enum class Quadrant(val value: Pair<Int, Int>) { // use for readability. replace every instance of quadrants with this
        SOUTHWEST(-1 to -1),
        SOUTHEAST(-1 to 1),
        NORTHEAST(1 to 1),
        NORTHWEST(1 to -1)
    }

    private fun makeAnnularSector(
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

    private fun formatTime(timeRange: TimeRange): String {
        val formatter = DateTimeFormatter.ISO_INSTANT
        return "${timeRange.from!!.format(formatter)}/${timeRange.to!!.format(formatter)}"
    }

    private fun buildStationsUrl(
        center: LocationValue,
        requestedQuadrants: MutableMap<String, MutableSet<Quadrant>>,
        searchAdvancements: Map<String, MutableMap<Quadrant, Int>>,
        elementsConst: List<String>,
        timeRange: TimeRange,
        modes: MutableMap<String, Mode>
    ): String {
        val elements: String
        val polygons: MutableList<List<Pair<Double, Double>>> = mutableListOf() // if two elements in the same quadrant are at different progress, it makes another polygon. if they are at the same progress, it ignores it if it already made a polygon there. change to LocationValue

        //one polygonmaking function, which simply checks "if progress is at one" to know if to make inner circle or not and combine the polygons into one

        if (requestedQuadrants.isEmpty()) {
            // use inside circle instead of making a polygonal circle?
            val radius = radiusIncrement
            val points = 20 // 4 quadrants * 5 points
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
            elementsConst.forEach { element ->
                if (modes[element] != Mode.NEAREST || modes[element] != Mode.FAIL) {
                    missingElements.add(element)
                }
            }
            val currentAreas: Map<Quadrant, MutableSet<Int>> = enumValues<Quadrant>().associateWith { mutableSetOf() }
            requestedQuadrants.forEach { (element, quadrants) ->
                quadrants.forEach { quadrant ->
                    searchAdvancements[element]!![quadrant] = searchAdvancements[element]!![quadrant]!! + 1
                    val next = searchAdvancements[element]!![quadrant]!!
                    if (!currentAreas[quadrant]!!.contains(next)) {
                        currentAreas[quadrant]!!.add(next)
                        val radius = next * radiusIncrement
                        polygons.add(makeAnnularSector(center, radius, quadrant)) // BUG: sometimes it requests multiple of these in the same quadrant. why?
                    }
                }
            }
            elements = encode(missingElements.joinToString(","))
        }

        val url = buildString {
            append("https://frost-beta.met.no/api/v1/obs/met.no/filter/get?incobs=false&elementids=$elements")
            append("&time="+encode(formatTime(timeRange)))
            append("&inside="+encode(formatPolygons(polygons)))
        }
        println(url)

        return url
    }

    private fun testIDWWeight(searchAdvancements: Map<String, MutableMap<Quadrant, Int>>, element: String, quadrant: Quadrant): Double {
        var sum = 0.0
        var thisDistance = 0.0
        searchAdvancements[element]!!.forEach { (searchedQuadrant, distance) ->
            val useDistance = if (quadrant == searchedQuadrant) {
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

    private fun assignStations(
        center: LocationValue,
        responseBody: StationsResponse?,
        stationLocations: MutableMap<String, LocationValue>,
        stationQueues: Map<String, Map<Quadrant, MutableList<String>>>,
        queueAdvancements: Map<String, MutableMap<Quadrant, Int>>,
        modes: MutableMap<String, Mode>,
        modesData: MutableMap<String, Pair<MutableList<Pair<String, Double>>, Boolean>>,
        elementConst: List<String>,
        searchAdvancements: Map<String, MutableMap<Quadrant, Int>>,
        usableStations: Map<String, Map<Quadrant, MutableList<String>>>
    ): MutableMap<String, MutableSet<Quadrant>> {
        val appendix: Map<String, Map<Quadrant, MutableList<Pair<String, Double>>>> = elementConst.associateWith { element -> Quadrant.values().associateWith { mutableListOf() } }
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

                // MODE ACTIVATION: NEAREST
                if (distance < 5 && modesData[elementid]!!.second == false) {
                    modes[elementid] = Mode.NEAREST // can be set idempotently
                    val index = modesData[elementid]!!.first.binarySearchBy(distance) { it.second }
                    val insertionPoint = if (index < 0) -index - 1 else index
                    modesData[elementid]!!.first.add(insertionPoint, Pair(stationid, distance))
                    println("SET "+elementid+" TO NEAREST")
                }

                appendix[elementid]!![quadrant]!!.add(Pair(stationid, distance))
            }
        }
        stationQueues.forEach { (element, quadrants) ->
            val mode = modes[element]!!

            var queuableOrUsableStationsCount = 0 // Does not need to be muted later
            usableStations[element]!!.forEach { (quadrant, usableQueue) -> // refractor
                if (usableQueue.isNotEmpty() || (queueAdvancements[element]!![quadrant]!! <= stationQueues[element]!![quadrant]!!.size)) {
                    queuableOrUsableStationsCount += 1
                }
            }
            println(element)
            println(stationQueues[element])
            println(queueAdvancements)
            println(queuableOrUsableStationsCount)
            quadrants.forEach { (quadrant, queue) ->
                if (responseBody != null) {
                    appendix[element]!![quadrant]!!.sortBy{ it.second }
                    queue.addAll(appendix[element]!![quadrant]!!.map { it.first })
                }
                if (queue.size == 0) {
                    addToRequestedQuadrants(requestedQuadrants, searchAdvancements, usableStations, queuableOrUsableStationsCount, element, quadrant, mode)
                }
            }
        }
        //val readOnlyRequestedQuadrants: Map<String, MutableSet<Quadrant>> = requestedQuadrants
        return requestedQuadrants
    }

    private fun addToRequestedQuadrants(requestedQuadrants: MutableMap<String, MutableSet<Quadrant>>, searchAdvancements: Map<String, MutableMap<Quadrant, Int>>, usableStations: Map<String, Map<Quadrant, MutableList<String>>>, currentStationsCount: Int, element: String, quadrant: Quadrant, mode: Mode) {
        if (mode == Mode.INTERPOLATION) { // tests if it is worth to keep searching in a quadrant
            if (
                usableStations[element]!![quadrant]!!.size == 0 &&
                (currentStationsCount < 3 || testIDWWeight(searchAdvancements, element, quadrant) > 0.04) // Or save on calculating by simply checking if it has searched 4 times?
                && searchAdvancements[element]!![quadrant]!! <= 3
            ) {
                /*println(usableStations)
                println(usableStations[element]!![quadrant]!!.size)
                println(currentStationsCount)
                println(testIDWWeight(searchAdvancements, element, quadrant))*/
                requestedQuadrants.getOrPut(element) { mutableSetOf() }.add(quadrant)
            }
        } else if (mode == Mode.EXTRAPOLATION) {
            if (usableStations[element]!![quadrant]!!.size < 2 && searchAdvancements[element]!![quadrant]!! <= 3) {
                requestedQuadrants.getOrPut(element) { mutableSetOf() }.add(quadrant)
            }
        }
    }

    private fun buildStationDataUrl(stationids: MutableSet<String>, missingElements: MutableSet<String>, timeRange: TimeRange): String {
        val sources = stationids.joinToString(",") { "SN$it" }
        val time = encode(formatTime(timeRange))
        val elements = encode(missingElements.joinToString(","))

        val url = "https://frost.met.no/observations/v0.jsonld?sources=$sources&referencetime=$time&elements=$elements"

        println(url)

        return url
    }

    private enum class TimeInterval {
        SECOND,
        MINUTE,
        HOUR,
        DAY,
        WEEK,
        MONTH,
        YEAR
    }

    private fun ZonedDateTime.toIntervalBucket(interval: TimeInterval): Int {
        return when (interval) {
            TimeInterval.SECOND -> this.second+1
            TimeInterval.MINUTE -> this.minute+1
            TimeInterval.HOUR -> this.hour+1
            TimeInterval.DAY -> this.dayOfMonth
            TimeInterval.WEEK -> this.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR)
            TimeInterval.MONTH -> this.monthValue
            TimeInterval.YEAR -> this.year
        }
    }

    private fun assignData(
        rawData: ObservationsResponse,
        stationTimeData: Map<String, MutableMap<String, MutableMap<Int, Pair<Double, Int>>>>,
        interval: TimeInterval
    ) {
        rawData.data.forEach { observationData ->
            val id = observationData.sourceId.substringBeforeLast(":").removePrefix("SN") // Because Frost non-beta puts :0 at the end of station ids for whatever reason
            val timeKey = observationData.referenceTime.toIntervalBucket(interval)
            observationData.observations.forEach { observation ->
                val element = observation.elementId
                val value = observation.value
                var useValue = value
                val timeData = stationTimeData[element]!!.getOrPut(id) { mutableMapOf() }
                if (element.contains("snow_coverage_type")) {
                    if (value == -1.0) {
                        useValue = 0.0
                    }
                } else if (element.contains("cloud_area_fraction")) {
                    if (value == -3.0 || value == 9.0) {
                        useValue = 5.0 // "average value" for cloud cover
                    }
                } else if (element.contains("surface_downwelling_shortwave_flux_in_air")) {
                    if (value < 0.0) { // why is it sometimes negative???
                        useValue = 0.0
                    }
                }
                if (timeData.contains(timeKey)) {
                    val (prevValue, prevCount) = stationTimeData[element]!![id]!![timeKey]!!
                    stationTimeData[element]!![id]!![timeKey] = Pair(prevValue+useValue, prevCount+1)
                } else {
                    stationTimeData[element]!![id]!![timeKey] = Pair(useValue, 1)
                }
            }
        }
    }

    private fun getNexts(
        requestedQuadrants: MutableMap<String, MutableSet<Quadrant>>,
        usableStations: Map<String, Map<Quadrant, MutableList<String>>>,
        stationQueues: Map<String, Map<Quadrant, MutableList<String>>>,
        queueAdvancements: Map<String, MutableMap<Quadrant, Int>>,
        stationTimeData: Map<String, MutableMap<String, MutableMap<Int, Pair<Double, Int>>>>,
        searchAdvancements: Map<String, MutableMap<Quadrant, Int>>,
        modes: MutableMap<String, Mode>,
        modesData: MutableMap<String, Pair<MutableList<Pair<String, Double>>, Boolean>>
    ): Pair<MutableSet<String>, MutableSet<String>> { // Returns missingids and missingelements
        val checkQuadrants: MutableMap<String, MutableSet<Quadrant>> = mutableMapOf()
        val missingIds: MutableSet<String> = mutableSetOf()
        val missingElements: MutableSet<String> = mutableSetOf()

        usableStations.forEach { (element, quadrants) ->
            val mode = modes[element]!!
            println("mode of "+element+" is "+mode.toString())
            if (mode == Mode.NEAREST && modesData[element]!!.second == false) {
                println(modesData)
                val stationid = modesData[element]!!.first[0].first
                if (stationid in stationTimeData[element]!! && stationTimeData[element]!![stationid]!!.size == 12) {
                    println("satisfied station "+stationid)
                    modesData[element] = Pair(modesData[element]!!.first, true)
                } else {
                    if (modes[element] == Mode.NEAREST && modesData[element]!!.first[0].first == stationid) {
                        println("popped station "+stationid)
                        modesData[element]!!.first.removeAt(0)
                    }
                }
                if (modesData[element]!!.second == false) {
                    if (modesData[element]!!.first.isNotEmpty()) {
                        println("ELEMENT GOOD "+element)
                        missingElements.add(element)
                        missingIds.add(modesData[element]!!.first[0].first)
                    }
                    else {
                        println("RESETTING "+element)
                        resetMode(modes, element, searchAdvancements[element]!!)
                    }
                }
            }
            if (mode == Mode.INTERPOLATION || mode == Mode.EXTRAPOLATION) {
                quadrants.forEach { (quadrant, usableQueue) ->
                    val advancement = queueAdvancements[element]!![quadrant]!!
                    if (advancement > 0) {
                        if (advancement <= stationQueues[element]!![quadrant]!!.size) {
                            val stationid = stationQueues[element]!![quadrant]!![advancement-1]
                            stationTimeData[element]!![stationid]?.let { data ->
                                if (data.size == 12) {
                                    println(stationid+" is usable")
                                    usableQueue.add(stationid)
                                } else {
                                    println("NON USABLE "+stationid+" SIZE OF "+data.size.toString())
                                }
                            }
                            queueAdvancements[element]!![quadrant] = (advancement * -1) // Means it has already tested this. forgot where this is used
                        }
                    }
                    if (mode == Mode.INTERPOLATION) {
                        if (usableStations[element]!![quadrant]!!.size == 0) {
                            checkQuadrants.getOrPut(element) { mutableSetOf() }.add(quadrant)
                        }
                    } else if (mode == Mode.EXTRAPOLATION) {
                        if (usableStations[element]!![quadrant]!!.size < 3) {
                            checkQuadrants.getOrPut(element) { mutableSetOf() }.add(quadrant)
                        }
                    }
                }
            }
        }
        checkQuadrants.forEach { (element, quadrants) ->
            val mode = modes[element]!!
            if (mode == Mode.INTERPOLATION || mode == Mode.EXTRAPOLATION) { // refractor to only one if of this?
                val usableStationsCount = usableStations[element]!!.count { it.value.isNotEmpty() }
                quadrants.forEach { quadrant ->
                    val nextAdvancement = abs(queueAdvancements[element]!![quadrant]!!)+1
                    if (nextAdvancement <= stationQueues[element]!![quadrant]!!.size) {
                        missingIds.add(stationQueues[element]!![quadrant]!![nextAdvancement-1])
                        missingElements.add(element)
                        queueAdvancements[element]!![quadrant] = nextAdvancement
                    } else {
                        addToRequestedQuadrants(requestedQuadrants, searchAdvancements, usableStations, usableStationsCount, element, quadrant, mode)
                    }
                }
            }
        }
        return Pair(missingIds, missingElements)
    }

    suspend fun fetchFrostData(
        client: CustomHttpClient,
        lat: Double,
        lon: Double,
        elements: List<String>,
        rawTimeRange: Pair<ZonedDateTime, ZonedDateTime> = Pair(
            ZonedDateTime.of(1800, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
            ZonedDateTime.now(ZoneId.of("UTC"))
        )
    ): Result<MutableMap<String, Array<Double>>> {
        val timeRange = TimeRange(from = rawTimeRange.first, to = rawTimeRange.second)
        val center = LocationValue(lat, lon)
        //val center = LocationValue(60.386163, 8.259478) // middle of the mountains
        //val center = LocationValue(60.771442, 4.695727) // westmost region
        val elementsConst = elements

        val stationLocations: MutableMap<String, LocationValue> = mutableMapOf() //id to distances and angle from straight up, for easy weight recalculation. if a station is already in, it does not re-add it (e.g. if same station different element)
        val stationTimeData: Map<String, MutableMap<String, MutableMap<Int, Pair<Double, Int>>>> = elementsConst.associateWith { mutableMapOf() }// element to id to month to sum to counter. separated by elements because the same station can have multiple elements.
        val stationQueues: Map<String, Map<Quadrant, MutableList<String>>> = elementsConst.associateWith {
            enumValues<Quadrant>().associateWith { mutableListOf() } } //quadrants for each element. each quadrant points to a queue of stations from the closest to the furthest. where when a station. the sorting of the queues are done before adding, not by adding
        val queueAdvancements: Map<String, MutableMap<Quadrant, Int>> = elementsConst.associateWith {
            enumValues<Quadrant>().associateWith { 0 }.toMutableMap() } // elements to quadrants to a counter of where you last dropped off in the queue. they begin at one, and all indexing require -1 on it. fine to start at 1 because it begins by incrementing it with 1?? idk i forgor
        val usableStations: Map<String, Map<Quadrant, MutableList<String>>> = elementsConst.associateWith {
            enumValues<Quadrant>().associateWith { mutableListOf() } } // stationQueues but only the usable stations // elements to quadrants
        val searchAdvancements: Map<String, MutableMap<Quadrant, Int>> = elementsConst.associateWith { enumValues<Quadrant>().associateWith { 1 }.toMutableMap() }
        val modes: MutableMap<String, Mode> = elementsConst.associateWith { Mode.INTERPOLATION }.toMutableMap()// elements to mode
        val modesData: MutableMap<String, Pair<MutableList<Pair<String, Double>>, Boolean>> = elementsConst.associateWith { Pair(mutableListOf<Pair<String, Double>>(), false) }.toMutableMap()

        var requestedQuadrants: MutableMap<String, MutableSet<Quadrant>> = mutableMapOf()
        var first = true
        var callCounter = 0

        // use a do while instead of first?
        while (first || requestedQuadrants.isNotEmpty()) {
            first = false
            while (true) {
                setModes(modes, searchAdvancements) // if it has gone 150km in two directions (which means it has not found anything in them) (it can well interpolate with three quadrants, but four is optimal). if the two directions are diagonal to each other, it's also fine however. if it has found nothing in 150km in all directions, it goes to guesstimation
                val url = buildStationsUrl(center, requestedQuadrants, searchAdvancements, elementsConst, timeRange, modes)
                val result: Result<StationsResponse?> = client.httpRequest(url, mapOf("Authorization" to "Basic $encodedAuth"))
                callCounter += 1
                result.onSuccess { body ->
                    requestedQuadrants = assignStations(center, body, stationLocations, stationQueues, queueAdvancements, modes, modesData, elementsConst, searchAdvancements, usableStations) // extrapolation makes it stop requesting quadrants it doesn't have
                }.onFailure { error ->
                    println(error)
                    return Result.failure(error)
                }
                if (requestedQuadrants.isEmpty()) break
            }
            while (true) {
                val (missingIds, missingElements) = getNexts(requestedQuadrants, usableStations, stationQueues, queueAdvancements, stationTimeData, searchAdvancements, modes, modesData) // goes through the queue to where we last left off. don't even bother with timerange finding, just get from 1800 to currentdate. if the queue is at max, adds the missing data to requestedQuadrants instead of checkQuadrants
                if (missingIds.isEmpty()) break
                val url = buildStationDataUrl(missingIds, missingElements, timeRange)
                val result: Result<ObservationsResponse?> = client.httpRequest(url, mapOf("Authorization" to "Basic $encodedAuth"))
                callCounter += 1
                result.onSuccess { body ->
                    if (body != null) {
                        assignData(body, stationTimeData, TimeInterval.MONTH) // averages the data into stationTimeData. choosing to put data there depending on the mode it will use later, since calculating all of them will be too expensive. if a data does not have a combined full year available, it requests a new one. if data wasn't sufficient, it advances the queue on it. if the data was found, it adds it to usableStations, which we will use for interpolation. if for extrapolation, we need multiple in usableStations
                    }
                }.onFailure { error ->
                    println(error)
                    return Result.failure(error)
                }
            }
            // if it fails to find data in 150km for both sides, it outright fails, otherwise, the polation is supposed to be fairly strong. if it has for one side, but fails the other, it activates extrapolationmode, which then uses the stationQueues of the side it does have to do so
        }

        println(usableStations)
        println("success?")
        println(callCounter)

        val dataF = formatData(center, stationLocations, stationTimeData, usableStations, modes, modesData) // also calculates the final weights. guesstimation also done here
        //File("testfile123.txt").writeText(Json.encodeToString(dataF))
        dataF.forEach { (key, values) ->
            println("$key: ${values.contentToString()}")
        }
        println(dataF)
        return Result.success(dataF)
    }

    private fun formatData(
        center: LocationValue,
        stationLocations: MutableMap<String, LocationValue>,
        stationTimeData: Map<String, MutableMap<String, MutableMap<Int, Pair<Double, Int>>>>,
        usableStations: Map<String, Map<Quadrant, MutableList<String>>>,
        modes: MutableMap<String, Mode>,
        modesData: MutableMap<String, Pair<MutableList<Pair<String, Double>>, Boolean>>
    ): MutableMap<String, Array<Double>> { // returns elements to month averages
        val resultsFormatted: MutableMap<String, Array<Double>> = mutableMapOf()
        println(modes)
        println(modesData)
        usableStations.forEach { (element, quadrants) ->
            val mode = modes[element]!!
            if (mode == Mode.NEAREST) {
                val nearestId = modesData[element]!!.first[0].first
                println("NEAREST OF "+element+" THAT IS "+nearestId)
                val monthArray = averageArray(stationTimeData[element]!![nearestId]!!, 12)
                resultsFormatted[element] = monthArray // It isn't set to Mode.NEAREST without there being something, and it always gets set out if it turns out there isn't
            } else if (mode == Mode.INTERPOLATION) {
                val valuesHolder: MutableMap<String, Array<Double>> = mutableMapOf()
                quadrants.forEach { (_, stations) ->
                    if (stations.size > 0) {
                        val station = stations[0]
                        val monthData = stationTimeData[element]!![station]!!
                        val monthArray = averageArray(monthData, 12)
                        valuesHolder[station] = monthArray
                    }
                }
                resultsFormatted[element] = getIDWAverages(center, valuesHolder, stationLocations)
            } else if (mode == Mode.EXTRAPOLATION) {
                println("EXTRAPOLATE "+element)
                val intervalLength = 12 /*TBD: FIX .referenceTime.toIntervalBucket(interval)*/
                val interceptsArray: Array<Pair<Double, Int>> = Array(intervalLength) { Pair(0.0, 0) } // index is month, sum to counter
                usableStations[element]!!.forEach { (_, stations) ->
                    if (stations.size >= 2) {
                        val distanceList: MutableList<Pair<Double, Array<Double>>> = mutableListOf()
                        stations.forEach { stationid ->
                            distanceList.add(Pair(calculateDistance(center, stationLocations[stationid]!!), averageArray(stationTimeData[element]!![stationid]!!, intervalLength)))
                        }
                        multiYLinearRegression(distanceList, interceptsArray)
                    }
                }
                val averagedIntercepts = Array(intervalLength) { 0.0 }
                interceptsArray.forEachIndexed { index, pair ->
                    val (sum, count) = pair
                    averagedIntercepts[index] = sum / count.toDouble()
                }
                resultsFormatted[element] = averagedIntercepts
            }
        }
        //val readOnlyResultsFormatted: Map<String, Array<Double>> = resultsFormatted
        return resultsFormatted
    }

    private fun averageArray(timeData: MutableMap<Int, Pair<Double, Int>>, length: Int): Array<Double> {
        val timeArray = Array(length) { 0.0 }
        timeData.forEach { (time, pair) ->
            val (sum, count) = pair
            timeArray[time-1] = sum / count.toDouble()
        }
        return timeArray
    }

    private fun getIDWAverages(center: LocationValue, valuesHolder: MutableMap<String, Array<Double>>, stationLocations: MutableMap<String, LocationValue>): Array<Double> {
        val averagedArray = Array(12) { 0.0 }
        val distances = valuesHolder.map { (id, _) -> id to calculateDistance(center, stationLocations[id]!!) }
        val power = 2
        var rawWeightSum = 0.0
        val rawWeights: Map<String, Double> = distances.associate { (id, distance) ->
            val rawWeight = if (distance == 0.0) 1.0 else 1 / distance.pow(power)
            rawWeightSum += rawWeight
            id to rawWeight
        }
        valuesHolder.forEach { (id, months) ->
            val weight = rawWeights[id]!! / rawWeightSum
            months.forEachIndexed { index, value ->
                averagedArray[index] = averagedArray[index] + (value * weight)
            }
        }
        return averagedArray
    }

    private fun multiYLinearRegression(data: List<Pair<Double, Array<Double>>>, interceptsArray: Array<Pair<Double, Int>>) {
        val n = data.size
        val meanX = data.sumOf { it.first } / n
        val outputCount = data[0].second.size
        val meanYs = Array<Double>(outputCount) { i -> data.sumOf { it.second[i] } / n }

        for (i in 0 until outputCount) {
            var numerator = 0.0
            var denominator = 0.0

            for ((x, ys) in data) {
                val dx = x - meanX
                val dy = ys[i] - meanYs[i]
                numerator += dx * dy
                denominator += dx * dx
            }

            val slope = numerator / denominator
            val intercept = meanYs[i] - slope * meanX
            interceptsArray[i] = Pair(interceptsArray[i].first + intercept, interceptsArray[i].second + 1)
        }
    }



    @Serializable
    private data class RimStationsResponse(
        val data: List<RimStation>
    )

    @Serializable
    private data class RimStation(
        @SerialName("@type") val type: String,
        val id: String,
        val name: String,
        val shortName: String,
        val country: String,
        val countryCode: String,
        val geometry: RimGeometry,
        val masl: Int,
        val county: String,
        val municipality: String,
        val stationHolders: List<String>
    )

    @Serializable
    private data class RimGeometry( // coords in lon, lat format
        @SerialName("@type") val type: String,
        val coordinates: List<Double>
    )


    suspend fun fetchRimData( // This function is for gimmick use and not implemented well
        client: CustomHttpClient,
        lat: Double,
        lon: Double,
        elementRaw: String,
        rawTimeRange: Pair<ZonedDateTime, ZonedDateTime> = Pair(
            ZonedDateTime.of(2025, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
            ZonedDateTime.now(ZoneId.of("UTC"))
        )
    ): Result<Array<Double>> {
        val center = LocationValue(lat, lon)
        //val elements = encode(elements.joinToString(","))
        val element = encode(elementRaw)
        val timeRange = TimeRange(rawTimeRange.first, rawTimeRange.second) // kotlin bad
        val formatter = DateTimeFormatter.ISO_INSTANT

        val stationsUrl = "https://rim.k8s.met.no/api/v1/stations?weatherElements=$element&from=${encode(rawTimeRange.first.format(formatter))}&to=${encode(rawTimeRange.second.format(formatter))}"
        println(stationsUrl)
        val result: Result<RimStationsResponse?> = client.httpRequest(stationsUrl)
        result.onSuccess { body ->
            if (body == null) return Result.failure(ApiException(ApiError.UNKNOWN_ERROR)) // It should always get a body
            val rimStationsQueue = body.data.toMutableList() // Makes a copy
            rimStationsQueue.sortBy { rimStation ->
                val rimLocation = LocationValue(rimStation.geometry.coordinates[1], rimStation.geometry.coordinates[0])
                calculateDistance(center, rimLocation)
            }
            val stationTimeData: Map<String, MutableMap<String, MutableMap<Int, Pair<Double, Int>>>> = mapOf(elementRaw to mutableMapOf())
            while(true) {
                val stationid = rimStationsQueue[0].id.substringBeforeLast(":").removePrefix("SN")
                val dataUrl = buildStationDataUrl(mutableSetOf(stationid), mutableSetOf(elementRaw), timeRange) // unmutable them later
                val dataResult: Result<ObservationsResponse?> = client.httpRequest(dataUrl, mapOf("Authorization" to "Basic $encodedAuth")) // could use rim's for same effect but avoid using api key
                dataResult.onSuccess { dataBody ->
                    if (dataBody != null) {
                        assignData(dataBody, stationTimeData, TimeInterval.HOUR)
                        val stationData = stationTimeData[elementRaw]!![stationid]!!
                        if (stationData.size == 24) { // Make the minimum months based on how many months in the timerange if we care enough
                            val dataF = averageArray(stationData, 24) // make take a length and then null test for everything
                            println(dataF.contentToString())
                            return Result.success(dataF)
                        }
                    }
                    rimStationsQueue.removeAt(0)
                }.onFailure { error ->
                    println(error)
                    return Result.failure(error)
                }
            }
        }.onFailure { error ->
            println(error)
            return Result.failure(error)
        }
        return Result.failure(ApiException(ApiError.UNKNOWN_ERROR)) // Did I mention I hate kotlin?
    }

}