package `in`.org.dawn.helm.wheels.thrust

import android.app.Activity
import android.content.res.Configuration
import android.view.WindowManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import `in`.org.dawn.helm.boards.Board7
import `in`.org.dawn.helm.boards.Board8
import `in`.org.dawn.helm.comms.Lantern
import `in`.org.dawn.helm.comms.igniteLantern
import `in`.org.dawn.helm.ui.settings.SettingsViewModel
import kotlinx.coroutines.delay

@Composable
@Preview
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
fun BooleanThrust() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val viewModel: SettingsViewModel = hiltViewModel()
    val settingsState by viewModel.uiState.collectAsStateWithLifecycle()

    val thrustState = settingsState.thrust
    val lanternState = settingsState.lantern

    DisposableEffect(Unit, lanternState) {
        val window = (context as Activity).window
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        igniteLantern(lanternState.host, lanternState.secure)

        onDispose {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            Lantern.disconnect()
        }
    }

    var acc by remember { mutableFloatStateOf(0f) }
    var dir by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(acc, dir) {
        if (Lantern.ready) {
            val (finalX, finalY) = when {
                thrustState.invertControls -> dir to acc
                thrustState.invertLR -> acc to -dir
                else -> acc to dir
            }
            Lantern.sendActuation(finalX, finalY, lanternState.token)
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
                    Accelerator(dir, thrustState.stepValue, lanternState.power.toFloat()) {
                        dir = it
                    }
                    Tray(true) {
                        Board7(Lantern)
                    }
                    Tray(true) {
                        Board8(Lantern)
                    }
                    Accelerator(acc, thrustState.stepValue, lanternState.power.toFloat()) {
                        acc = it
                    }
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
                Tray(false) {
                    Board7(Lantern)
                }
                Row(
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Accelerator(dir, thrustState.stepValue, lanternState.power.toFloat()) {
                        dir = it
                    }
                    Accelerator(acc, thrustState.stepValue, lanternState.power.toFloat()) {
                        acc = it
                    }
                }
                Tray(false) {
                    Board8(Lantern)
                }
            }
        }
    }
}

@Composable
fun Accelerator(value: Float, stepValue: Int, maxPower: Float, onValueChanged: (Float) -> Unit) {
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
        .height(50.dp), value = value, onValueChange = {
        onValueChanged(it)
    }, valueRange = -maxPower..maxPower, steps = (maxPower / stepValue).toInt() - 1
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