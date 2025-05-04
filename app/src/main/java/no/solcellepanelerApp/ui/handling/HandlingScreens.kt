package no.solcellepanelerApp.ui.handling

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
            Text("Laster inn data, vennligst vent...")
        }
    }
}

//Vi må gjøre alle stringa oversettbare

@Composable
fun ErrorScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Noe gikk galt! Prøv igjen senere.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun TimeoutErrorScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Tidsavbrudd. Sjekk internett og prøv igjen.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun AuthorizationErrorScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Autorisering feilet. Kontakt utviklerne.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ServerErrorScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Serverfeil. Prøv igjen senere.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun OverloadErrorScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Tjenesten er overbelastet. Vent litt og prøv igjen.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun NetworkErrorScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Nettverksfeil. Sjekk tilkoblingen din.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun UnknownErrorScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Ukjent feil. Kontakt utviklerne med detaljer.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun RequestErrorScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Feil i forespørselen. Kontakt utviklerne med detaljer.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun SeaErrorScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Ugyldig lokasjon. Det er sannsynligvis i havet.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
