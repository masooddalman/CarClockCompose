package com.whiteCat.carclock

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlinx.coroutines.delay

// static segment positions of a digit number
enum class SegmentPosition(val x: Int, val y: Int, val rotation: Float) {
    Top(1, 0, 0f),
    TopLeft(0, 1, 90f),
    TopRight(2, 1, 90f),
    Middle(1, 2, 0f),
    BottomLeft(0, 3, 90f),
    BottomRight(2, 3, 90f),
    Bottom(1, 4, 0f),
    // garage positions
    GarageTopLeft(-2, -3, 45f),
    GarageTopRight(4, -3, 45f),
    GarageBottomLeft(-2, 6, 45f),
    GarageBottomRight(4, 6, 45f)
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
    var carAssignments by remember {
        mutableStateOf(
            List(7) { i ->
                when (i) {
                    0, 1 -> SegmentPosition.GarageTopLeft
                    2 -> SegmentPosition.GarageTopRight
                    3 -> if (java.util.Random().nextBoolean()) SegmentPosition.GarageBottomLeft else SegmentPosition.GarageTopRight
                    4 -> SegmentPosition.GarageBottomLeft
                    5, 6 -> SegmentPosition.GarageBottomRight
                    else -> SegmentPosition.GarageBottomRight // Default fallback
                }
            }
        )
    }

    // re-assign the cars when the number changes
    LaunchedEffect(number) {
        carAssignments = updateCarAssignments(number, carAssignments)
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
            val targetPosition = carAssignments[i]
            Car(carIndex = i, target = targetPosition)
        }
    }
}

@Composable
fun Car(carIndex: Int, target: SegmentPosition) {
    val gridSize = 50

    val initialGarage =  when (carIndex) {
        0, 1 -> SegmentPosition.GarageTopLeft
        2 -> SegmentPosition.GarageTopRight
        3 -> if (java.util.Random().nextBoolean()) SegmentPosition.GarageBottomLeft else SegmentPosition.GarageTopRight
        4 -> SegmentPosition.GarageBottomLeft
        5, 6 -> SegmentPosition.GarageBottomRight
        else -> SegmentPosition.GarageBottomRight // Default fallback
    }

    val animatedX = remember { Animatable((initialGarage.x * gridSize).toFloat()) }
    val animatedY = remember { Animatable((initialGarage.y * gridSize).toFloat()) }
    val animatedRotation = remember { Animatable(initialGarage.rotation) }

    LaunchedEffect(target) {
        val targetX = (target.x * gridSize).toFloat()
        val targetY = (target.y * gridSize).toFloat()

        val currentX = animatedX.value
        val currentY = animatedY.value

        val xTravelDuration = (abs(targetX - currentX) / gridSize * 300).toLong().coerceIn(200, 800)
        val yTravelDuration = (abs(targetY - currentY) / gridSize * 300).toLong().coerceIn(200, 800)

        // Move horizontally
        if (abs(currentX - targetX) > 0.1f) {
            val rotation = if (targetX > currentX) 0f else 180f
            // Rotate first
            animatedRotation.animateTo(rotation, tween(durationMillis = 200, easing = LinearEasing))
            // Then move
            animatedX.animateTo(targetX, tween(durationMillis = xTravelDuration.toInt(), easing = FastOutSlowInEasing))
        }

        // Move vertically
        if (abs(currentY - targetY) > 0.1f) {
            val rotation = if (targetY > currentY) 90f else -90f
            // Rotate first
            animatedRotation.animateTo(rotation, tween(durationMillis = 200, easing = LinearEasing))
            // Then move
            animatedY.animateTo(targetY, tween(durationMillis = yTravelDuration.toInt(), easing = FastOutSlowInEasing))
        }

        // Final rotation at destination
        animatedRotation.animateTo(target.rotation, tween(durationMillis = 200))
    }


    // the car
    Box(
        modifier = Modifier
            .offset(x = animatedX.value.dp + 10.dp, y = animatedY.value.dp + 10.dp) // +10 for margin
            .rotate(animatedRotation.value)
            .size(width = 75.dp, height = 30.dp) // car size
            .background(Color.Red, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("$carIndex")
    }
}

// lazily assigning cars
fun updateCarAssignments(digit: Int, previousAssignments: List<SegmentPosition>): List<SegmentPosition> {
    val requiredSegments = digitMap[digit] ?: setOf()
    // Define preferred segment for each car. This creates a stable mapping.
    val preferredSegments = listOf(
        SegmentPosition.Top,
        SegmentPosition.TopLeft,
        SegmentPosition.TopRight,
        SegmentPosition.Middle,
        SegmentPosition.BottomLeft,
        SegmentPosition.BottomRight,
        SegmentPosition.Bottom
    )

    val garagePositions = setOf(
        SegmentPosition.GarageTopLeft,
        SegmentPosition.GarageTopRight,
        SegmentPosition.GarageBottomLeft,
        SegmentPosition.GarageBottomRight
    )

    return List(7) { carIndex ->
        val preferredSegment = preferredSegments[carIndex]
        val previousPosition = previousAssignments[carIndex]
        if (preferredSegment in requiredSegments) {
            // If this car's preferred segment is needed for the new digit, assign it there.
            preferredSegment
        } else {
            // only move the car if it's not already in a garage
            // and it's already in a garage, keep it there.
            if (previousPosition in garagePositions) {
                previousPosition
            } else {
                // send it to its designated garage.
                when (carIndex) {
                    0, 1 -> SegmentPosition.GarageTopLeft
                    2 -> SegmentPosition.GarageTopRight
                    3 -> if (java.util.Random().nextBoolean()) SegmentPosition.GarageBottomLeft else SegmentPosition.GarageTopRight
                    4 -> SegmentPosition.GarageBottomLeft
                    5, 6 -> SegmentPosition.GarageBottomRight
                    else -> SegmentPosition.GarageBottomRight // Default fallback
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DigitalCarNumberPreview() {
    var number by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000) // delay between number changes
            number = (number + 1) % 10
        }
    }

    DigitalCarNumber(number = number)
}
