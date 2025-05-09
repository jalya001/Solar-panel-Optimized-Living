package no.solcellepanelerApp.ui.reusables


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import no.solcellepanelerApp.R


// burde egt ikke være så vanskelig å implementere "vis kun flrste gang" funksjonaliteten her men man er i en tidsklemme
@Composable
fun SimpleTutorialOverlay(
    onDismiss: () -> Unit,
    message: AnnotatedString,
    bottomMessage: String = stringResource(R.string.overlay_bottom_message),
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable { onDismiss() }
            .zIndex(2f)
            .padding(20.dp)
    ) {
        Text(
            text = message,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )

        Text(
            text = bottomMessage,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.BottomCenter),
            textAlign = TextAlign.Center
        )
    }
}@Preview
@Composable
fun PreviewMapOverlay() {
    val title = stringResource(R.string.map_overlay_title)
    val body = stringResource(R.string.map_overlay)

    val message = buildAnnotatedString {
        withStyle(style = MaterialTheme.typography.titleLarge.toSpanStyle()) {
            append("$title\n\n")
        }
        withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {
            append(body)
        }
    }

    SimpleTutorialOverlay(onDismiss = {}, message = message)
}

@Preview
@Composable
fun PreviewDrawOverlay() {
    val title = stringResource(R.string.draw_overlay_title)
    val body = stringResource(R.string.map_draw_overlay)

    val message = buildAnnotatedString {
        withStyle(style = MaterialTheme.typography.titleLarge.toSpanStyle()) {
            append("$title\n\n")
        }
        withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {
            append(body)
        }
    }

    SimpleTutorialOverlay(onDismiss = {}, message = message)
}

@Preview
@Composable
fun PreviewSavingOverlay() {
    val title = stringResource(R.string.saving_overlay_title)
    val body = stringResource(R.string.saving_overlay)

    val message = buildAnnotatedString {
        withStyle(style = MaterialTheme.typography.titleLarge.toSpanStyle()) {
            append("$title\n\n")
        }
        withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {
            append(body)
        }
    }

    SimpleTutorialOverlay(onDismiss = {}, message = message)
}

@Preview
@Composable
fun PreviewHomeOverlay() {
    val title = stringResource(R.string.home_overlay_title)
    val body = stringResource(R.string.home_overlay)

    val message = buildAnnotatedString {
        withStyle(style = MaterialTheme.typography.titleLarge.toSpanStyle()) {
            append("$title\n\n")
        }
        withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {
            append(
                body.removePrefix("$title\n\n") // in case title is repeated in body
            )
        }
    }

    SimpleTutorialOverlay(onDismiss = {}, message = message)
}
