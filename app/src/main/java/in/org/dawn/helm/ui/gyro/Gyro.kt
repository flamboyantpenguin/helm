package `in`.org.dawn.helm.ui.gyro

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.org.dawn.helm.ui.theme.emojiFont


@Composable
fun DirectionGauge(acc: Int, dir: Int, boxSize: Dp) {

    val secondaryColor = MaterialTheme.colorScheme.secondary
    val disabledColor = MaterialTheme.colorScheme.surfaceVariant

    val targetAngle = when (dir) {
        -1 -> -45f
        1 -> 45f
        else -> 0f
    }

    val animatedAngle by animateFloatAsState(
        targetValue = targetAngle,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    Box(
        modifier = Modifier
            .size(boxSize)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height)

            drawArc(
                color = disabledColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )

            rotate(degrees = animatedAngle, pivot = center) {
                drawLine(
                    color = if (acc != 0) secondaryColor else disabledColor,
                    start = center,
                    end = Offset(size.width / 2, 20f),
                    strokeWidth = 12.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        Text(
            text = getSignal(acc, dir),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        )
    }
}



@Composable
fun TiltSensorHandler(onDirectionChanged: (Int) -> Unit) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                // In Landscape, the Y-axis tells us the "steering" tilt
                // 9.8 is full gravity. ~3.0 is a comfortable tilt.
                val yAxis = event.values[1]

                val dir = when {
                    yAxis > 2.0f  -> 1   // Constant Right
                    yAxis < -2.0f -> -1  // Constant Left
                    else          -> 0   // Neutral
                }
                onDirectionChanged(dir)
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }
}

@Composable
fun Accelerator(size: Dp, text: String, onPressStateChanged: (Boolean) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isPressed) {
        onPressStateChanged(isPressed)
    }

    Box(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.tertiary, shape = RectangleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = { })
            .padding(16.dp)
            .size(size, size), contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.onTertiary, style = TextStyle(
            fontFamily = emojiFont,
            fontSize = 24.sp,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            )
        )
        )
    }
}
