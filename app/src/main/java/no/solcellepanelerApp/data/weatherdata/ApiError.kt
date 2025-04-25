package no.solcellepanelerApp.data.weatherdata

enum class ApiError {
    TIMEOUT_ERROR,
    AUTHORIZATION_ERROR,
    SERVER_ERROR,
    OVERLOAD_ERROR,
    NETWORK_ERROR,
    UNKNOWN_ERROR
}

class ApiException(val errorCode: ApiError) : Throwable() {
    override fun toString(): String {
        return "$errorCode"
    }
}