package `in`.org.dawn.helm.wheels.askew

import android.app.Activity
import android.content.res.Configuration
import android.view.WindowManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.org.dawn.helm.R
import `in`.org.dawn.helm.boards.Board7
import `in`.org.dawn.helm.boards.Board8
import `in`.org.dawn.helm.comms.Lantern
import `in`.org.dawn.helm.ui.settings.SettingsViewModel
import kotlinx.coroutines.delay

@Composable
@Preview
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
fun Earth() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    DisposableEffect(Unit) {
        val window = (context as Activity).window
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        onDispose {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    var acc by remember { mutableFloatStateOf(0f) }
    var dir by remember { mutableFloatStateOf(0f) }

    var decay by remember { mutableStateOf(false) }
    var increment by remember { mutableStateOf(false) }

    val viewModel: SettingsViewModel = hiltViewModel()
    val settingsState by viewModel.uiState.collectAsStateWithLifecycle()
    val lanternState = settingsState.lantern

    LaunchedEffect(lanternState.host) {
        Lantern.connect(lanternState.host, lanternState.secure)
    }

    LaunchedEffect(increment, decay) {
        while (acc != 0f || increment || decay) {
            if (increment) {
                if (acc != 100f) acc += 1f
            }
            else if (decay) {
                if (acc != -100f) acc -= 1f
            }
            else {
                if (acc >= 0) acc -= 1f
                else acc += 1f
            }
            delay(50)
        }
    }

    LaunchedEffect(acc, dir) {
        while (acc != 0f || dir != 0f) {
            Lantern.sendActuation(acc, dir, token = lanternState.token)
            delay(lanternState.delay)
        }
    }

    TiltSensorHandler { tiltValue ->
        dir = tiltValue
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (isLandscape) {
                Row(
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    DirectionGauge(acc, dir, 150.dp)
                }
                Spacer(Modifier.weight(1f))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Break(50.dp, R.drawable.stop_24dp) { acc = 0f }
                        Accelerator(50.dp, R.drawable.double_arrow_down_24dp) { isPressed ->
                            decay = isPressed
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Tray(false) {
                            Board7(Lantern)
                        }
                        Tray(false) {
                            Board8(Lantern)
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Break(50.dp, R.drawable.stop_24dp ) { acc = 0f }
                        Accelerator(50.dp, R.drawable.double_arrow_up_24dp) { isPressed ->
                            increment = isPressed
                        }
                    }
                }
            } else {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Accelerator(50.dp, R.drawable.double_arrow_down_24dp) { isPressed ->
                        if (acc >= -100f) if (isPressed) acc += -1f else acc -= -1f
                    }
                    Accelerator(50.dp, R.drawable.double_arrow_up_24dp) { isPressed ->
                        if (acc <= 100f) if (isPressed) acc += 1f else acc -= 1f
                    }
                }
            }
        }
    }
}


@Composable
fun Tray(
    isLandscape: Boolean = false, content: @Composable () -> Unit
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
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            content()
        }
    }
}