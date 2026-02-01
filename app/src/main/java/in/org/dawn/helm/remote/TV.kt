package `in`.org.dawn.helm.remote

import android.content.res.Configuration
import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import `in`.org.dawn.helm.DrawShape
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Preview
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Composable
fun TVRemote() {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Tray(true, { Board8() })
                Tray(true, { Board9() })
                Spacer(Modifier.padding(16.dp))
                Controller(true, 60.dp)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Tray(false, { Board8() })
                Controller(false, 80.dp)
                Tray(false, { Board9() })
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
fun Controller(isLandscape: Boolean, buttonSize: Dp) {
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
            offset = Offset(offsetX.value, offsetY.value), buttonSizePx = buttonSizePx
        )

        currentPosition = newPosition
    }

    Column(
        modifier = if (isLandscape) {
            Modifier.fillMaxHeight()
        } else {
            Modifier.fillMaxWidth()
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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

            Position.values().forEach { position ->
                val offset = position.getOffset(buttonSizePx)
                MyButton(modifier = Modifier
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
                    isSelected = position == currentPosition,
                    position = position)
            }
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

@Composable
fun MyButton(
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