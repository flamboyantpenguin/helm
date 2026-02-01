/**
 * Modified by flamboyantpenguin [2026]
 * Portions of this code are derived from [Original Project Name]
 * * Original License: Apache License 2.0 (See LICENSE.GamePad in this project)
 * Original Source: https://github.com/MohamedRejeb/Compose-Interactive-Gamepad
 */

package `in`.org.dawn.helm.shapes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import `in`.org.dawn.helm.drawGlowCircle
import `in`.org.dawn.helm.drawGlowLine
import `in`.org.dawn.helm.drawGlowRect
import `in`.org.dawn.helm.drawGlowTriangle
import `in`.org.dawn.helm.wheels.remote.Position

@Composable
fun DrawShape(
    position: Position,
    isSelected: Boolean,
) {
    val paint = remember {
        Paint().apply {
            style = PaintingStyle.Stroke
            strokeWidth = 30f
        }
    }

    val frameworkPaint = remember {
        paint.asFrameworkPaint()
    }

    when (position) {
        Position.Top -> {

            val strokeColor = MaterialTheme.colorScheme.secondary
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 16.dp)
            ) {
                if (isSelected) {
                    drawGlowTriangle(
                        color = Color(0, 255, 0), frameworkPaint = frameworkPaint, paint = paint
                    )
                } else {
                    drawPoints(
                        points = listOf(
                            Offset(size.width / 2, 0f),
                            Offset(0f, size.height),
                            Offset(size.width, size.height),
                            Offset(size.width / 2, 0f),
                        ),
                        color = strokeColor,
                        pointMode = PointMode.Polygon,
                        strokeWidth = 4.dp.toPx()
                    )
                }
            }
        }

        Position.Right -> {
            val color = Color(255, 0, 0)

            val strokeColor = MaterialTheme.colorScheme.secondary
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                if (isSelected) {
                    drawGlowCircle(
                        color = color, frameworkPaint = frameworkPaint, paint = paint
                    )
                } else {
                    drawCircle(
                        color = strokeColor, style = Stroke(width = 4.dp.toPx())
                    )
                }
            }
        }

        Position.Bottom -> {
            val color = Color(0, 0, 255)

            val strokeColor = MaterialTheme.colorScheme.secondary
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (isSelected) {
                    drawGlowLine(
                        color = color,
                        frameworkPaint = frameworkPaint,
                        paint = paint,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height),
                    )

                    drawGlowLine(
                        color = color,
                        frameworkPaint = frameworkPaint,
                        paint = paint,
                        start = Offset(size.width, 0f),
                        end = Offset(0f, size.height),
                    )
                } else {
                    drawLine(
                        color = strokeColor,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = 4.dp.toPx()
                    )

                    drawLine(
                        color = strokeColor,
                        start = Offset(size.width, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = 4.dp.toPx()
                    )
                }
            }
        }

        Position.Left -> {
            val color = Color(255, 192, 203)
            val strokeColor = MaterialTheme.colorScheme.secondary

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (isSelected) {
                    drawGlowRect(
                        color = color, frameworkPaint = frameworkPaint, paint = paint
                    )
                } else {
                    drawRect(
                       strokeColor, style = Stroke(width = 4.dp.toPx())
                    )
                }

            }
        }

        else -> {}
    }
}