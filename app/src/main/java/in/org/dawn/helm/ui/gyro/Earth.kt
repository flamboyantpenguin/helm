package `in`.org.dawn.helm.ui.gyro

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import `in`.org.dawn.helm.comms.Lantern
import `in`.org.dawn.helm.ui.settings.SettingsViewModel
import `in`.org.dawn.helm.wheels.thrust.getSignal
import kotlinx.coroutines.delay

fun getSignal(a: Int, d: Int): String = when (a to d) {
    1 to 1 -> "TR"
    1 to -1 -> "TL"
    -1 to 1 -> "BR"
    -1 to -1 -> "BL"
    0 to 1 -> "R0"
    0 to -1 -> "L0"
    1 to 0 -> "T0"
    -1 to 0 -> "B0"
    else -> ""
}

@Composable
@Preview
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
fun Earth() {
    val configuration = LocalConfiguration.current

    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var acc by remember { mutableIntStateOf(0) }
    var dir by remember { mutableIntStateOf(0) }

    val viewModel: SettingsViewModel = hiltViewModel()
    val settingsState by viewModel.uiState.collectAsStateWithLifecycle()
    val lanternState = settingsState.lantern

    LaunchedEffect(lanternState.host) {
        Lantern.connect(lanternState.host, lanternState.secure)
    }

    LaunchedEffect(acc, dir) {
        while (acc != 0 || dir != 0) {
            Lantern.sendCommand(getSignal(acc, dir), token = lanternState.token)
            delay(500)
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
                Tray(false) {
                    Board8()
                    Board9()
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Accelerator(50.dp, text = "🙃") { isPressed ->
                        if (isPressed) acc = -1 else if (acc == -1) acc = 0
                    }
                    Accelerator(50.dp, text = "🙂") { isPressed ->
                        if (isPressed) acc = 1 else if (acc == 1) acc = 0
                    }
                }
            } else {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Accelerator(50.dp, text = "🙃") { isPressed ->
                        if (isPressed) acc = -1 else if (acc == -1) acc = 0
                    }
                    Tray(true) {
                        Board8()
                        Board9()
                    }
                    Accelerator(50.dp, text = "🙂") { isPressed ->
                        if (isPressed) acc = 1 else if (acc == 1) acc = 0
                    }
                }
            }
        }
    }
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
