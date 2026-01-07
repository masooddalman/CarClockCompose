package com.whiteCat.carclock

import androidx.compose.animation.core.Animatable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

enum class SecondHandType {
    Rectangle, Line
}

fun DrawScope.drawHorizontalLineSecondHandle(
    secondProgress: Float,
    strokeWidth: Float
) {
    val yPosition = size.height - (strokeWidth / 2)
    val startX = 0f
    val endX = size.width * secondProgress

    drawLine(
        color = Color.Cyan,
        start = Offset(startX, yPosition),
        end = Offset(endX, yPosition),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}

fun DrawScope.drawRectangularSecondHandle(
    secondProgress:  Animatable<Float, *>,
    path: Path,
    pathMeasure: PathMeasure,
    strokeWidth: Float,
    cornerRadius: Float
) {
    val segmentPath = drawSecondHandle(
        path = path,
        pathMeasure = pathMeasure,
        secondProgress = secondProgress,
        size = size,
        cornerRadius = CornerRadius(cornerRadius),
        strokeWidth = strokeWidth
    )
    drawPath(
        path = segmentPath,
        color = Color.Cyan,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round
        )
    )
}


private fun drawSecondHandle(path: Path,
                             pathMeasure: PathMeasure,
                             secondProgress: Animatable<Float, *>,
                             size: Size,
                             strokeWidth: Float,
                             cornerRadius: CornerRadius) : Path{

    // Adjust bounds for the stroke width to prevent clipping
    val bounds = Rect(
        left = strokeWidth / 2,
        top = strokeWidth / 2,
        right = size.width - strokeWidth / 2,
        bottom = size.height - strokeWidth / 2
    )

    path.reset() // Clear the path for redrawing

    // Start at the top-center
    path.moveTo(x = bounds.center.x, y = bounds.top)

    // Draw line to the top-right corner
    path.lineTo(x = bounds.right - cornerRadius.x, y = bounds.top)

    // Draw the top-right arc
    path.arcTo(
        rect = Rect(
            left = bounds.right - 2 * cornerRadius.x,
            top = bounds.top,
            right = bounds.right,
            bottom = bounds.top + 2 * cornerRadius.y
        ),
        startAngleDegrees = -90f,
        sweepAngleDegrees = 90f,
        forceMoveTo = false
    )

    // Draw line down to the bottom-right corner
    path.lineTo(x = bounds.right, y = bounds.bottom - cornerRadius.y)

    // Draw the bottom-right arc
    path.arcTo(
        rect = Rect(
            left = bounds.right - 2 * cornerRadius.x,
            top = bounds.bottom - 2 * cornerRadius.y,
            right = bounds.right,
            bottom = bounds.bottom
        ),
        startAngleDegrees = 0f,
        sweepAngleDegrees = 90f,
        forceMoveTo = false
    )

    // Draw line to the bottom-left corner
    path.lineTo(x = bounds.left + cornerRadius.x, y = bounds.bottom)

    // Draw the bottom-left arc
    path.arcTo(
        rect = Rect(
            left = bounds.left,
            top = bounds.bottom - 2 * cornerRadius.y,
            right = bounds.left + 2 * cornerRadius.x,
            bottom = bounds.bottom
        ),
        startAngleDegrees = 90f,
        sweepAngleDegrees = 90f,
        forceMoveTo = false
    )

    // Draw line up to the top-left corner
    path.lineTo(x = bounds.left, y = bounds.top + cornerRadius.y)

    // Draw the top-left arc
    path.arcTo(
        rect = Rect(
            left = bounds.left,
            top = bounds.top,
            right = bounds.left + 2 * cornerRadius.x,
            bottom = bounds.top + 2 * cornerRadius.y
        ),
        startAngleDegrees = 180f,
        sweepAngleDegrees = 90f,
        forceMoveTo = false
    )

    // Draw line back to the top-center starting point
    path.lineTo(x = bounds.center.x, y = bounds.top)

    // Associate the path with the path measure
    pathMeasure.setPath(path, false)

    // Create a new path to draw the segment into
    val segmentPath = Path()

    // Get a segment of the full path based on the animated progress
    pathMeasure.getSegment(
        startDistance = 0f,
        stopDistance = pathMeasure.length * secondProgress.value,
        destination = segmentPath,
        startWithMoveTo = true
    )

    return segmentPath
}