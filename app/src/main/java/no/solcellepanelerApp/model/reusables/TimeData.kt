package no.solcellepanelerApp.model.reusables

import java.time.ZonedDateTime

data class TimedData<T>(
    val data: T,
    val timestamp: ZonedDateTime = ZonedDateTime.now()
)

suspend fun <T, R> updateStaleData(
    currentTime: ZonedDateTime,
    dataHolder: () -> TimedData<T>?,
    fetchData: suspend () -> Unit,
    computeValue: (TimedData<T>) -> R?,
    updateTarget: (R?) -> Unit
) {
    val cachedData = dataHolder()
    val isStale = cachedData == null || cachedData.timestamp < currentTime.minusHours(1)

    if (isStale) fetchData()

    val updatedData = dataHolder()
    updateTarget(updatedData?.let { computeValue(it) })
}

