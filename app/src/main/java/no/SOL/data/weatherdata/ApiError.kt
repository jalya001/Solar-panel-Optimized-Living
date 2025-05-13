package no.SOL.data.weatherdata

import androidx.compose.runtime.Composable
import no.SOL.ui.handling.AuthorizationErrorScreen
import no.SOL.ui.handling.NetworkErrorScreen
import no.SOL.ui.handling.OverloadErrorScreen
import no.SOL.ui.handling.RequestErrorScreen
import no.SOL.ui.handling.SeaErrorScreen
import no.SOL.ui.handling.ServerErrorScreen
import no.SOL.ui.handling.TimeoutErrorScreen
import no.SOL.ui.handling.UnknownErrorScreen

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
            ApiError.TIMEOUT_ERROR -> {
                { TimeoutErrorScreen() }
            }

            ApiError.AUTHORIZATION_ERROR -> {
                { AuthorizationErrorScreen() }
            }

            ApiError.SERVER_ERROR -> {
                { ServerErrorScreen() }
            }

            ApiError.OVERLOAD_ERROR -> {
                { OverloadErrorScreen() }
            }

            ApiError.NETWORK_ERROR -> {
                { NetworkErrorScreen() }
            }

            ApiError.UNKNOWN_ERROR -> {
                { UnknownErrorScreen() }
            }

            ApiError.REQUEST_ERROR -> {
                { RequestErrorScreen() }
            }

            ApiError.SEA_ERROR -> {
                { SeaErrorScreen() }
            }
        }
    }
}
