package `in`.org.dawn.helm.wheels.askew

import android.app.Activity
import android.content.res.Configuration
import android.view.WindowManager
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.org.dawn.helm.R
import `in`.org.dawn.helm.boards.Board7
import `in`.org.dawn.helm.boards.Board8
import `in`.org.dawn.helm.comms.Lantern
import `in`.org.dawn.helm.comms.igniteLantern
import `in`.org.dawn.helm.ui.settings.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.abs

@Composable
fun Earth() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val viewModel: SettingsViewModel = hiltViewModel()
    val settingsState by viewModel.uiState.collectAsStateWithLifecycle()

    val earthState = settingsState.earth
    val lanternState = settingsState.lantern

    DisposableEffect(Unit, lanternState) {
        val window = (context as Activity).window
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (isLandscape) igniteLantern(lanternState.host, lanternState.secure)

        onDispose {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            Lantern.disconnect()
        }
    }

    var acc by remember { mutableFloatStateOf(0f) }
    var dir by remember { mutableFloatStateOf(0f) }

    var decay by remember { mutableStateOf(false) }
    var increment by remember { mutableStateOf(false) }
    var decrement by remember { mutableStateOf(false) }

    LaunchedEffect(
        lanternState, isLandscape
    ) {

        var x = 0f
        var y = 0f
        while (isActive) {
            if (Lantern.ready && (acc != x || dir != y)) {
                if (earthState.isTank) {
                    toDifferential(
                        acc, dir, lanternState.power
                    ).let { (x, y) -> Lantern.sendActuation(x, y, lanternState.token) }
                } else {
                    Lantern.sendActuation(acc, dir, lanternState.token)
                }
                delay(lanternState.delay)
                x = acc
                y = dir
            } else {
                delay(lanternState.delay)
            }
        }
    }

    LaunchedEffect(increment, decrement, decay) {
        while (!decay && (acc != 0f || increment || decrement)) {
            if (increment) {
                if (acc != 100f) acc += 1f
            } else if (decrement) {
                if (acc != -100f) acc -= 1f
            } else {
                if (acc >= 0) acc -= 1f
                else acc += 1f
            }
            delay(earthState.delay)
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
                .padding(20.dp, 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLandscape) {
                Row(
                    Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        LanternStat(Lantern.ready) {
                            igniteLantern(lanternState.host, lanternState.secure)
                        }
                    }

                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        DirectionGauge(acc, dir, 150.dp)
                    }

                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {}
                }
                Spacer(Modifier.weight(1f))
                Row(
                    Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (earthState.invertControls) {
                            Accelerator(50.dp, R.drawable.double_arrow_up_24dp) { isPressed ->
                                increment = isPressed
                            }
                            Accelerator(50.dp, R.drawable.double_arrow_down_24dp) { isPressed ->
                                decrement = isPressed
                            }
                        } else {
                            SwitchButton(50.dp, R.drawable.stop_24dp) { state -> decay = state }
                            ClickButton(50.dp, R.drawable.hand_24dp) { acc = 0f }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Tray { Board7(Lantern) }
                        Tray { Board8(Lantern) }
                    }
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (earthState.invertControls) {
                            SwitchButton(50.dp, R.drawable.stop_24dp) { state -> decay = state }
                            ClickButton(50.dp, R.drawable.hand_24dp) { acc = 0f }
                        } else {
                            Accelerator(50.dp, R.drawable.double_arrow_up_24dp) { isPressed ->
                                increment = isPressed
                            }
                            Accelerator(50.dp, R.drawable.double_arrow_down_24dp) { isPressed ->
                                decrement = isPressed
                            }
                        }
                    }
                }

            } else {
                PortraitWarning()
            }
        }
    }
}

fun toDifferential(x: Float, y: Float, maxPower: Int): Pair<Float, Float> {
    val left = x + y
    val right = y - x
    val max = maxOf(abs(left), abs(right), 100f)
    return Pair(
        (left / max) * maxPower, (right / max) * maxPower
    )
}

@Composable
fun PortraitWarning() {
    Column(
        Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.mobile_rotate_24dp),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "This mode works only in landscape orientation",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun LanternStat(isLit: Boolean, onPressed: () -> Unit) {
    val size = 40.dp

    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(10.dp)
            )
            .border(
                2.dp,
                if (isLit) MaterialTheme.colorScheme.secondary else (MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = { onPressed() })
            .padding(16.dp)
            .size(size, size),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.lantern_24),
                modifier = Modifier.size(size),
                contentDescription = stringResource(R.string.title_lantern),
                tint = if (isLit) MaterialTheme.colorScheme.secondary else (MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Meow",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}


@Composable
fun Tray(
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        content()
    }
}