package `in`.org.dawn.helm.wheels.remote

import android.app.Activity
import android.content.res.Configuration
import android.view.WindowManager
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.org.dawn.helm.boards.Board7
import `in`.org.dawn.helm.boards.Board8
import `in`.org.dawn.helm.comms.Lantern
import `in`.org.dawn.helm.prefs.LanternState
import `in`.org.dawn.helm.prefs.RemoteState
import `in`.org.dawn.helm.shapes.DrawShape
import `in`.org.dawn.helm.ui.settings.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt


fun offsetMapper(
    x: Float, y: Float, maxSize: Float, maxPower: Int, isDifferential: Boolean
): Pair<Float, Float> {
    val nX = (x / maxSize) * maxPower
    val nY = -(y / maxSize) * maxPower
    if (isDifferential) {
        val left = nY + nX
        val right = nY - nX
        val max = maxOf(abs(left), abs(right), 100f)
        return Pair(
            (left / max) * maxPower, (right / max) * maxPower
        )
    }
    return Pair(nX, nY)
}

@Preview
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Composable
fun TVRemote() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val viewModel: SettingsViewModel = hiltViewModel()
    val settingsState by viewModel.uiState.collectAsStateWithLifecycle()

    val lanternState = settingsState.lantern

    LaunchedEffect(lanternState.host) {
        Lantern.connect(lanternState.host, lanternState.secure)
    }

    DisposableEffect(Unit) {
        val window = (context as Activity).window
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        onDispose {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Tray(true, { Board7(Lantern) })
                Tray(true, { Board8(Lantern) })
                Spacer(Modifier.padding(70.dp))
                Controller(lanternState, settingsState.remote, true, 60.dp)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Tray(false, { Board7(Lantern) })
                Controller(lanternState, settingsState.remote, false, 80.dp)
                Tray(false, { Board8(Lantern) })
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
fun Controller(
    lanternState: LanternState, remoteState: RemoteState, isLandscape: Boolean, buttonSize: Dp
) {
    // Support RTL
    val layoutDirection = LocalLayoutDirection.current
    val directionFactor = if (layoutDirection == LayoutDirection.Rtl) -1 else 1

    val scope = rememberCoroutineScope()

    // Swipe size in px
    val buttonSizePx = with(LocalDensity.current) { buttonSize.toPx() }
    val dragSizePx = buttonSizePx * 1.5f

    // Drag offset
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    var isDragging by remember { mutableStateOf(false) }

    var currentPosition by remember { mutableStateOf<Position?>(null) }

    LaunchedEffect(offsetX.value, offsetY.value) {

        val newPosition = getPosition(
            isPrecise = true,
            offset = Offset(offsetX.value, offsetY.value),
            buttonSizePx = buttonSizePx
        )

        currentPosition = newPosition

        val (cX, cY) = offsetMapper(
            offsetX.value, offsetY.value, dragSizePx, lanternState.power, remoteState.isTank
        )
        while (abs(cX) > 0.1f || abs(cY) > 0.1f) {
            Lantern.sendActuation(
                cX, cY, token = lanternState.token
            )
            delay(lanternState.delay)
        }
    }

    Column(
        modifier = if (isLandscape) {
            Modifier.fillMaxHeight()
        } else {
            Modifier.fillMaxWidth()
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {

        Box(
            modifier = Modifier
                .size(buttonSize * 4)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {

            Box(modifier = Modifier
                .offset {
                    IntOffset(
                        x = (offsetX.value).roundToInt(), y = (offsetY.value).roundToInt()
                    )
                }
                .width(buttonSize)
                .height(buttonSize)
                .alpha(0.8f)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
                .pointerInput(Unit) {

                    detectDragGestures(onDragStart = {
                        isDragging = true
                    }, onDragEnd = {
                        scope.launch {
                            offsetX.animateTo(0f)
                        }
                        scope.launch {
                            offsetY.animateTo(0f)
                        }
                        isDragging = false
                    }, onDragCancel = {
                        scope.launch {
                            offsetX.animateTo(0f)
                        }
                        scope.launch {
                            offsetY.animateTo(0f)
                        }
                        isDragging = false
                    }, onDrag = { change, dragAmount ->
                        change.consume()

                        scope.launch {
                            val newOffsetX = offsetX.value + dragAmount.x * directionFactor
                            val newOffsetY = offsetY.value + dragAmount.y

                            if (sqrt(newOffsetX.pow(2) + newOffsetY.pow(2)) < dragSizePx) {
                                offsetX.snapTo(newOffsetX)
                                offsetY.snapTo(newOffsetY)
                            } else if (sqrt(offsetX.value.pow(2) + newOffsetY.pow(2)) < dragSizePx) {
                                offsetY.snapTo(newOffsetY)
                            } else if (sqrt(newOffsetX.pow(2) + offsetY.value.pow(2)) < dragSizePx) {
                                offsetX.snapTo(newOffsetX)
                            }
                        }

                    })
                }

            )

            val buttonAlpha = remember {
                Animatable(0f)
            }

            LaunchedEffect(key1 = isDragging) {
                if (isDragging) {
                    buttonAlpha.animateTo(1f)
                } else {
                    buttonAlpha.animateTo(0f)
                }
            }

            Position.entries.forEach { position ->
                val offset = position.getOffset(buttonSizePx)
                DirectionButton(modifier = Modifier
                    .offset {
                        IntOffset(
                            x = offset.x.roundToInt(), y = offset.y.roundToInt()
                        )
                    }
                    .graphicsLayer {
                        alpha = buttonAlpha.value
                        scaleX = buttonAlpha.value
                        scaleY = buttonAlpha.value
                    }
                    .size(buttonSize)
                    .padding(8.dp),
                    isSelected = (position == currentPosition) || (currentPosition?.contains(
                        position
                    ) == true),
                    position = position)
            }
        }
    }

}

@Composable
fun DirectionButton(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    position: Position,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center
    ) {
        DrawShape(
            position, isSelected = isSelected
        )
    }
}