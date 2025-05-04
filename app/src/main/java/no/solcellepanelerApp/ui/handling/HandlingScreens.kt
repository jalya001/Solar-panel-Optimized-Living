package no.solcellepanelerApp.ui.handling

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.solcellepanelerApp.R


@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.tertiary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Laster inn data, vennligst vent...",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
fun ErrorScreenTemplate(
    icon: ImageVector,
    message: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable fun ErrorScreen() =
    ErrorScreenTemplate(Icons.Default.ErrorOutline, stringResource(R.string.generic_error))

@Composable fun TimeoutErrorScreen() =
    ErrorScreenTemplate(Icons.Default.AccessTime, stringResource(R.string.timeout_error))

@Composable fun AuthorizationErrorScreen() =
    ErrorScreenTemplate(Icons.Default.Lock, stringResource(R.string.authorization_error))

@Composable fun ServerErrorScreen() =
    ErrorScreenTemplate(Icons.Default.CloudOff, stringResource(R.string.server_error))

@Composable fun OverloadErrorScreen() =
    ErrorScreenTemplate(Icons.Default.TrendingUp, stringResource(R.string.overload_error))

@Composable fun NetworkErrorScreen() =
    ErrorScreenTemplate(Icons.Default.WifiOff, stringResource(R.string.network_error))

@Composable fun UnknownErrorScreen() =
    ErrorScreenTemplate(Icons.Default.HelpOutline, stringResource(R.string.unknown_error))

@Composable fun RequestErrorScreen() =
    ErrorScreenTemplate(Icons.Default.Send, stringResource(R.string.request_error))

@Composable fun SeaErrorScreen() =
    ErrorScreenTemplate(Icons.Default.Waves, stringResource(R.string.sea_error))

@Composable fun NoDataErrorScreen() =
    ErrorScreenTemplate(Icons.Default.Waves, stringResource(R.string.nodata_error))

@Composable fun PartialDataErrorScreen() =
    ErrorScreenTemplate(Icons.Default.Waves, stringResource(R.string.partialdata_error))

@Composable fun UnexpectedErrorScreen() =
    ErrorScreenTemplate(Icons.Default.Waves, stringResource(R.string.unexpected_error))

@Preview
@Composable
fun PreviewLoadingScreen() {
    LoadingScreen()
}

@Preview
@Composable
fun PreviewErrorScreen() {
    ErrorScreen()
}

@Preview
@Composable
fun PreviewTimeoutErrorScreen() {
    TimeoutErrorScreen()
}

@Preview
@Composable
fun PreviewAuthorizationErrorScreen() {
    AuthorizationErrorScreen()
}

@Preview
@Composable
fun PreviewServerErrorScreen() {
    ServerErrorScreen()
}

@Preview
@Composable
fun PreviewOverloadErrorScreen() {
    OverloadErrorScreen()
}

@Preview
@Composable
fun PreviewNetworkErrorScreen() {
    NetworkErrorScreen()
}

@Preview
@Composable
fun PreviewUnknownErrorScreen() {
    UnknownErrorScreen()
}

@Preview
@Composable
fun PreviewRequestErrorScreen() {
    RequestErrorScreen()
}

@Preview
@Composable
fun PreviewSeaErrorScreen() {
    SeaErrorScreen()
}
