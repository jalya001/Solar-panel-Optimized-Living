package no.SOL.model.reusables

import java.time.ZonedDateTime

data class TimedData<T>(
    val data: T,
    val timestamp: ZonedDateTime = ZonedDateTime.now(),
)

suspend fun <T> updateStaleData(
    currentTime: ZonedDateTime,
    getData: () -> TimedData<T>?,
    setData: (TimedData<T>?) -> Unit,
    fetchData: suspend () -> T?,
) {
    val cachedData = getData()
    val isStale = cachedData == null || cachedData.timestamp < currentTime.minusHours(1)
    if (isStale) {
        val fresh = fetchData()
        if (fresh != null) {
            setData(TimedData(fresh, currentTime))
        } else {
            setData(null)
        }
    }
}