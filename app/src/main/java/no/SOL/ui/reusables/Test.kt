package no.SOL.ui.reusables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button


@Composable
fun ReplaceVowelsUI(onProcess: (String) -> String) {
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Enter a string") }
        )


        Button(onClick = {
            result = onProcess(input)
        }) {
            Text("Replace Vowels with 'k'")
        }


        Text("Result: $result")
    }
}
