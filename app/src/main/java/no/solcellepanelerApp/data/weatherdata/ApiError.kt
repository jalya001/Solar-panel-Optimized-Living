package no.solcellepanelerApp.data.weatherdata

import androidx.compose.runtime.Composable
import no.solcellepanelerApp.ui.handling.AuthorizationErrorScreen
import no.solcellepanelerApp.ui.handling.NetworkErrorScreen
import no.solcellepanelerApp.ui.handling.OverloadErrorScreen
import no.solcellepanelerApp.ui.handling.RequestErrorScreen
import no.solcellepanelerApp.ui.handling.SeaErrorScreen
import no.solcellepanelerApp.ui.handling.ServerErrorScreen
import no.solcellepanelerApp.ui.handling.TimeoutErrorScreen
import no.solcellepanelerApp.ui.handling.UnknownErrorScreen

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
    fun getErrorScreen(): @Composable () -> Unit {
        return when (errorCode) {
            ApiError.TIMEOUT_ERROR -> { { TimeoutErrorScreen() } }
            ApiError.AUTHORIZATION_ERROR -> { { AuthorizationErrorScreen() } }
            ApiError.SERVER_ERROR -> { { ServerErrorScreen() } }
            ApiError.OVERLOAD_ERROR -> { { OverloadErrorScreen() } }
            ApiError.NETWORK_ERROR -> { { NetworkErrorScreen() } }
            ApiError.UNKNOWN_ERROR -> { { UnknownErrorScreen() } }
            ApiError.REQUEST_ERROR -> { { RequestErrorScreen() } }
            ApiError.SEA_ERROR -> { { SeaErrorScreen() } }
        }
    }
}
