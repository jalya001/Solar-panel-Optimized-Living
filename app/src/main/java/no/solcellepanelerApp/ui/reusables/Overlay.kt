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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import no.solcellepanelerApp.R

// burde egt ikke være så vanskelig å implementere "vis kun flrste gang" funksjonaliteten her men man er i en tidsklemme
@Composable
fun SimpleTutorialOverlay(
    onDismiss: () -> Unit,
    message: String = stringResource(R.string.overlay_message),
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
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraLight
        )


        Text(
            text = bottomMessage,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.BottomCenter),
            textAlign = TextAlign.Center
        )
    }
}
//@Composable
//fun SimpleTutorialOverlayWithImage(
//    onDismiss: () -> Unit,
//    message: String = stringResource(R.string.overlay_message),
//    bottomMessage: String = stringResource(R.string.overlay_bottom_message),
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black.copy(alpha = 0.8f))
//            .clickable { onDismiss() }
//            .zIndex(2f)
//            .padding(20.dp)
//    ) {
//        Text(
//            text = message,
//            color = Color.White,
//            style = MaterialTheme.typography.headlineSmall,
//            modifier = Modifier.align(Alignment.Center),
//            textAlign = TextAlign.Center,
//            fontWeight = FontWeight.ExtraLight
//        )
//
//        Image(
//            painter = painterResource(id = R.drawable.house),
//            contentDescription = "House",
//            modifier = Modifier
//                .width(200.dp)
//                .height(300.dp)
//                .align(Alignment.Center)
////                .offset(y = (-10).dp),
//            ,contentScale = ContentScale.Fit
//        )
//
//        Text(
//            text = bottomMessage,
//            color = Color.White,
//            style = MaterialTheme.typography.bodyMedium,
//            modifier = Modifier
//                .align(Alignment.BottomCenter),
//            textAlign = TextAlign.Center
//        )
//    }
//}