package com.whiteCat.carclock

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// static segment positions of a digit number
enum class SegmentPosition(val x: Int, val y: Int, val rotation: Float) {
    Top(1, 0, 0f),
    TopLeft(0, 1, 90f),
    TopRight(2, 1, 90f),
    Middle(1, 2, 0f),
    BottomLeft(0, 3, 90f),
    BottomRight(2, 3, 90f),
    Bottom(1, 4, 0f),
    // garage position to store extra pieces
    Garage(-5, -5, 45f) 
}


/**
 * Maps each digit (0-9) to a set of [SegmentPosition]s that form the visual representation
 * of that digit on a seven-segment display. This is used to determine which segments
 * should be "active" or occupied by a car to display a specific number.
 */
val digitMap = mapOf(
    0 to setOf(SegmentPosition.Top, SegmentPosition.TopLeft, SegmentPosition.TopRight, SegmentPosition.BottomLeft, SegmentPosition.BottomRight, SegmentPosition.Bottom),
    1 to setOf(SegmentPosition.TopRight, SegmentPosition.BottomRight),
    2 to setOf(SegmentPosition.Top, SegmentPosition.TopRight, SegmentPosition.Middle, SegmentPosition.BottomLeft, SegmentPosition.Bottom),
    3 to setOf(SegmentPosition.Top, SegmentPosition.TopRight, SegmentPosition.Middle, SegmentPosition.BottomRight, SegmentPosition.Bottom),
    4 to setOf(SegmentPosition.TopLeft, SegmentPosition.Middle, SegmentPosition.TopRight, SegmentPosition.BottomRight),
    5 to setOf(SegmentPosition.Top, SegmentPosition.TopLeft, SegmentPosition.Middle, SegmentPosition.BottomRight, SegmentPosition.Bottom),
    6 to setOf(SegmentPosition.Top, SegmentPosition.TopLeft, SegmentPosition.Middle, SegmentPosition.BottomLeft, SegmentPosition.BottomRight, SegmentPosition.Bottom),
    7 to setOf(SegmentPosition.Top, SegmentPosition.TopRight, SegmentPosition.BottomRight),
    8 to setOf(SegmentPosition.Top, SegmentPosition.TopLeft, SegmentPosition.TopRight, SegmentPosition.Middle, SegmentPosition.BottomLeft, SegmentPosition.BottomRight, SegmentPosition.Bottom),
    9 to setOf(SegmentPosition.Top, SegmentPosition.TopLeft, SegmentPosition.TopRight, SegmentPosition.Middle, SegmentPosition.BottomRight, SegmentPosition.Bottom)
)

@Composable
fun DigitalCarNumber(number: Int) {
    //hold current car assignment
    val carAssignments = remember(number) {
        assignCarsToSegments(number)
    }

    Box(
        modifier = Modifier
            .width(300.dp)
            .height(300.dp)
            .background(Color.Black.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
    ) {
        // rendering cars
        for (i in 0 until 7) {
            // finding target based on logic
            val targetPosition = carAssignments.getOrElse(i) { SegmentPosition.Garage }
            Car(target = targetPosition)
        }
    }
}

@Composable
fun Car(target: SegmentPosition) {
    // animation settings
    val animSpec = tween<Dp>(durationMillis = 800, easing = FastOutSlowInEasing)
    val rotSpec = tween<Float>(durationMillis = 800, easing = FastOutSlowInEasing)


    val gridSize = 50
    
    // animation X و Y
    val animatedX by animateDpAsState(targetValue = (target.x * gridSize).dp, animationSpec = animSpec)
    val animatedY by animateDpAsState(targetValue = (target.y * gridSize).dp, animationSpec = animSpec)
    val animatedRotation by animateFloatAsState(targetValue = target.rotation, animationSpec = rotSpec)

    // the car
    Box(
        modifier = Modifier
            .offset(x = animatedX + 10.dp, y = animatedY + 10.dp) // +10 for margin
            .rotate(animatedRotation)
            .size(width = 75.dp, height = 15.dp) // car size
            .background(Color.Red, RoundedCornerShape(4.dp))
    )
}

// assigning cars
fun assignCarsToSegments(digit: Int): List<SegmentPosition> {
    val requiredSegments = digitMap[digit] ?: emptySet()
    val assignments = mutableListOf<SegmentPosition>()
    
    // we need a list for indexing
    val requiredList = requiredSegments.toList()

    // using cars as needed
    for (i in 0 until 7) {
        if (i < requiredList.size) {
            // we need this car, send it to position
            assignments.add(requiredList[i])
        } else {
            // we don't need it, send it ti garage
            assignments.add(SegmentPosition.Garage)
        }
    }
    return assignments
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DigitalCarNumber(number = 0)
}