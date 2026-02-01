package `in`.org.dawn.helm.wheels.thrust

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.org.dawn.helm.comms.Lantern
import `in`.org.dawn.helm.ui.settings.SettingsViewModel
import kotlinx.coroutines.delay


fun getSignal(a: Int, d: Int): String = when (a to d) {
    1 to 1   -> "TR"
    1 to -1  -> "TL"
    -1 to 1  -> "BR"
    -1 to -1 -> "BL"
    0 to 1   -> "R0"
    0 to -1  -> "L0"
    1 to 0   -> "T0"
    -1 to 0  -> "B0"
    else     -> ""
}

@Composable
@Preview
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
fun BooleanThrust() {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var acc by remember { mutableFloatStateOf(0f) }
    var dir by remember { mutableFloatStateOf(0f) }

    val viewModel: SettingsViewModel = hiltViewModel()
    val settingsState by viewModel.uiState.collectAsStateWithLifecycle()
    val lanternState = settingsState.lantern

    LaunchedEffect(lanternState.host) {
        Lantern.connect(lanternState.host, lanternState.secure)
    }

    LaunchedEffect(acc, dir) {
        while (acc.toInt() != 0 || dir.toInt() != 0) {
            Lantern.sendCommand(getSignal(acc.toInt(), dir.toInt()), token = lanternState.token)
            delay(500)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        if (isLandscape) {
            Column(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(25.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Director(dir) { dir = it }
                    Tray(true) {
                        Board8()
                        Board9()
                    }
                    Accelerator(acc) { acc = it }
                }
            }
        } else {
            Column(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(25.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Director(dir) {
                        dir = it
                    }
                    Accelerator(acc) { acc = it }
                }
                Tray(false) {
                    Board8()
                    Board9()
                }
            }
        }
    }
}

@Composable
fun Accelerator(acc: Float, onAccChanged: (Float) -> Unit) {
    Slider(
        modifier = Modifier
            .graphicsLayer {
                rotationZ = 270f // Rotates the slider 90 degrees counter-clockwise
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0f)
            }
            .layout { measurable, constraints ->
                // Swap the width and height constraints
                val placeable = measurable.measure(
                    Constraints(
                        minWidth = constraints.minHeight,
                        maxWidth = constraints.maxHeight,
                        minHeight = constraints.minWidth,
                        maxHeight = constraints.maxHeight,
                    )
                )
                // Place the UI element with adjusted position after rotation
                layout(placeable.height, placeable.width) {
                    placeable.place(-placeable.width, 0)
                }
            }
            .width(300.dp) // Set the desired *vertical* height here
        .height(50.dp), value = acc, onValueChange = {
        onAccChanged(it)
        //onTeaPrepared(tea.copy(level = it))
    }, valueRange = -1f..1f, steps = 1
    )
}

@Composable
fun Director(dir: Float, onDirChanged: (Float) -> Unit) {
    Slider(
        modifier = Modifier
            .graphicsLayer {
                rotationZ = 270f // Rotates the slider 90 degrees counter-clockwise
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0f)
            }
            .layout { measurable, constraints ->
                // Swap the width and height constraints
                val placeable = measurable.measure(
                    Constraints(
                        minWidth = constraints.minHeight,
                        maxWidth = constraints.maxHeight,
                        minHeight = constraints.minWidth,
                        maxHeight = constraints.maxHeight,
                    )
                )
                // Place the UI element with adjusted position after rotation
                layout(placeable.height, placeable.width) {
                    placeable.place(-placeable.width, 0)
                }
            }
            .width(300.dp) // Set the desired *vertical* height here
        .height(50.dp), value = dir, onValueChange = {
        onDirChanged(it)
    }, valueRange = -1f..1f, steps = 1
    )

}

@Composable
fun Tray(
    isLandscape: Boolean, content: @Composable () -> Unit
) {
    if (isLandscape) {
        Column(
            modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly
        ) {
            content()
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            content()
        }
    }
}


@Composable
fun Board8() {
    val context = LocalContext.current
    Button(
        onClick = {
            Toast.makeText(context, "α", Toast.LENGTH_SHORT).show()
        },
    ) {
        Text(
            "α", style = MaterialTheme.typography.displaySmall
        )
    }
    Button(
        onClick = {
            Toast.makeText(context, "β", Toast.LENGTH_SHORT).show()
        },
    ) {
        Text(
            "β", style = MaterialTheme.typography.displaySmall
        )
    }
}

@Composable
fun Board9() {
    val context = LocalContext.current
    Button(
        onClick = {
            Toast.makeText(context, "Ɣ", Toast.LENGTH_SHORT).show()
        },
    ) {
        Text(
            "Ɣ", style = MaterialTheme.typography.displaySmall
        )
    }
    Button(
        onClick = {
            Toast.makeText(context, "Δ", Toast.LENGTH_SHORT).show()
        },
    ) {
        Text(
            "Δ", style = MaterialTheme.typography.displaySmall
        )
    }
}