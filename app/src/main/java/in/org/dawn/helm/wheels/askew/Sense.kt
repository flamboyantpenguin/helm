package `in`.org.dawn.helm.wheels.askew

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DirectionGauge(acc: Float, dir: Float, boxSize: Dp) {

    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val disabledColor = MaterialTheme.colorScheme.surfaceVariant

    var targetAngle = (dir / 100) * 90f

    if (acc < 0) targetAngle = when (targetAngle) {
        in 0f..100f -> targetAngle + 180f
        else -> targetAngle - 180f
    }

    val animatedAngle by animateFloatAsState(
        targetValue = targetAngle, animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    Box(
        modifier = Modifier
            .size(boxSize)
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)

            drawArc(
                color = disabledColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )

            // Speed Arc
            drawArc(
                color = tertiaryColor,
                startAngle = -90f,
                sweepAngle = (acc / 100f) * 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )

            rotate(degrees = animatedAngle, pivot = center) {
                drawLine(
                    color = if (acc != 0f) secondaryColor else disabledColor,
                    start = center,
                    end = Offset(size.width / 2, 20f),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}


@Composable
fun TiltSensorHandler(onDirectionChanged: (Float) -> Unit) {
    val context = LocalContext.current
    val deadZone = 1.0f
    val maxTilt = 6.0f

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
                    yAxis > deadZone -> {
                        // Map (deadZone..maxTilt) to (0..100)
                        ((yAxis - deadZone) / (maxTilt - deadZone) * 100f).coerceIn(0f, 100f)
                    }

                    yAxis < -deadZone -> {
                        // Map (-maxTilt..-deadZone) to (-100..0)
                        ((yAxis + deadZone) / (maxTilt - deadZone) * 100f).coerceIn(-100f, 0f)
                    }

                    else -> 0f
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
fun Accelerator(size: Dp, icon: Int, onPressStateChanged: (Boolean) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isPressed) {
        onPressStateChanged(isPressed)
    }

    Box(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(10.dp)
            )
            .border(2.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = { })
            .padding(16.dp)
            .size(size, size), contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(icon),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
            modifier = Modifier.size(size)
        )
    }
}

@Composable
fun Break(size: Dp, icon: Int, onPressed: () -> Unit) {
    OutlinedButton(
        modifier = Modifier
            .padding(8.dp)
            .size(size),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.tertiary),
        contentPadding = PaddingValues(0.dp),
        onClick = {
            onPressed()
        }) {
        Icon(
            imageVector = ImageVector.vectorResource(icon),
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = null,
            modifier = Modifier.size(size * 0.6f)
        )
    }
}