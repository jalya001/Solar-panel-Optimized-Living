package no.solcellepanelerApp.data.weatherdata

enum class ApiError {
    TIMEOUT_ERROR,
    AUTHORIZATION_ERROR,
    SERVER_ERROR,
    OVERLOAD_ERROR,
    NETWORK_ERROR,
    UNKNOWN_ERROR,
    REQUEST_ERROR,
    SEA_ERROR
}

class ApiException(val errorCode: ApiError) : Throwable() {
    override fun toString(): String {
        return when (errorCode) {
            ApiError.TIMEOUT_ERROR -> "Request timed out. Please check your internet connection and try again."
            ApiError.AUTHORIZATION_ERROR -> "API authorization failed. Please report to developers."
            ApiError.SERVER_ERROR -> "Server error. Please try again later."
            ApiError.OVERLOAD_ERROR -> "An API key has reached rate-limit. Please wait and retry."
            ApiError.NETWORK_ERROR -> "Could not connect to domain. Please check your internet connection or if the domain is down."
            ApiError.UNKNOWN_ERROR -> "An unknown error occurred. Please report how you achieved this to the developers."
            ApiError.REQUEST_ERROR -> "Bad request error. Please report how you achieved this to the developers."
            ApiError.SEA_ERROR -> "Bad PVGIS request. Your chosen location is most likely over the sea, and we do not have data for that."
        }
    }
}
