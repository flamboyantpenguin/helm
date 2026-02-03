/**
 * Modified by flamboyantpenguin [2026]
 * Portions of this code are derived from [Original Project Name]
 * * Original License: Apache License 2.0 (See LICENSE.GamePad in this project)
 * Original Source: https://github.com/MohamedRejeb/Compose-Interactive-Gamepad
 */

package `in`.org.dawn.helm.wheels.remote

import androidx.compose.ui.geometry.Offset
import kotlin.math.abs

enum class Position {
    Top, Right, Bottom, Left, TopRight, TopLeft, BottomLeft, BottomRight;

    operator fun contains(subPosition: Position): Boolean {
        return when (this) {
            TopRight -> subPosition == Top || subPosition == Right
            TopLeft -> subPosition == Top || subPosition == Left
            BottomRight -> subPosition == Bottom || subPosition == Right
            BottomLeft -> subPosition == Bottom || subPosition == Left
            else -> false
        }
    }
}

fun Position.getOffset(
    buttonSizePx: Float
): Offset {
    return when (this) {
        Position.Top -> Offset(x = 0f, y = -buttonSizePx * 1.25f)
        Position.Right -> Offset(x = buttonSizePx * 1.25f, y = 0f)
        Position.Bottom -> Offset(x = 0f, y = buttonSizePx * 1.25f)
        Position.Left -> Offset(x = -buttonSizePx * 1.25f, y = 0f)
        Position.TopRight -> Offset(x = buttonSizePx * 1.25f, y = -buttonSizePx * 1.25f)
        Position.TopLeft -> Offset(x = -buttonSizePx * 1.25f, y = -buttonSizePx * 1.25f)
        Position.BottomLeft -> Offset(x = -buttonSizePx * 1.25f, y = buttonSizePx * 1.25f)
        Position.BottomRight -> Offset(x = buttonSizePx * 1.25f, y = buttonSizePx * 1.25f)
    }
}

fun getPosition(
    isPrecise: Boolean = false,
    offset: Offset,
    buttonSizePx: Float,
): Position? {

    val isRight = offset.x > buttonSizePx
    val isLeft = -offset.x > buttonSizePx
    val isBottom = offset.y > buttonSizePx
    val isTop = -offset.y > buttonSizePx

    if (isPrecise) {
        if (isTop && isRight) return Position.TopRight
        if (isTop && isLeft) return Position.TopLeft
        if (isBottom && isRight) return Position.BottomRight
        if (isBottom && isLeft) return Position.BottomLeft
    }

    return if (abs(offset.x) > abs(offset.y)) {
        if (isRight) return Position.Right
        else if (isLeft) return Position.Left
        else null
    }
    else {
        if (isTop) return Position.Top
        else if (isBottom) return Position.Bottom
        else null
    }
}