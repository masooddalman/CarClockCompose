package com.whiteCat.carclock

import Car
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.Random

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
fun DigitalCarNumber(number: Int, modifier: Modifier = Modifier) {
    var carAssignments by remember {
        mutableStateOf(
            List(7) { i ->
                when (i) {
                    1 -> SegmentPosition.GarageTopLeft
                    0, 2 -> SegmentPosition.GarageTopRight
                    3 -> if (Random().nextBoolean()) SegmentPosition.GarageBottomLeft else SegmentPosition.GarageTopLeft
                    4 -> SegmentPosition.GarageBottomLeft
                    5, 6 -> SegmentPosition.GarageBottomRight
                    else -> SegmentPosition.GarageBottomRight
                }
            }
        )
    }

    // the previous state to calculate the transitions
    val previousAssignments = remember { mutableStateOf(carAssignments) }
    // holds the calculated delays for each car
    var carDelays by remember { mutableStateOf(List(7) { 0L }) }

    LaunchedEffect(number) {
        // Calculate the final, desired state for the new number
        val finalAssignments = updateCarAssignments(number, previousAssignments.value)

        val garagePositions = setOf(
            SegmentPosition.GarageTopLeft,
            SegmentPosition.GarageTopRight,
            SegmentPosition.GarageBottomLeft,
            SegmentPosition.GarageBottomRight
        )

        val staggerDelay = 300L // Delay in ms between each car starting

        // Calculate delays for the PARKING phase ---
        val parkingDelays = MutableList(7) { 0L }
        var parkingSequence = 0
        for (i in 0 until 7) {
            val isGoingToGarage = finalAssignments[i] in garagePositions && previousAssignments.value[i] !in garagePositions
            if (isGoingToGarage) {
                parkingDelays[i] = parkingSequence * staggerDelay
                parkingSequence++
            }
        }

        // Create the intermediate state: only move cars TO the garage
        val parkingAssignments = List(7) { i ->
            val isGoingToGarage = finalAssignments[i] in garagePositions && previousAssignments.value[i] !in garagePositions
            if (isGoingToGarage) {
                finalAssignments[i] // Send this car to the garage now
            } else {
                previousAssignments.value[i] // Keep others in their current spot
            }
        }

        // Apply the parking phase delays
        carDelays = parkingDelays
        // Apply the first phase (parking cars)
        carAssignments = parkingAssignments

        // Wait for the last parking car to start its animation
        val parkingDuration = (parkingSequence - 1).coerceAtLeast(0) * staggerDelay + 1000
        delay(parkingDuration)

        // Calculate delays for the DEPLOYING phase
        val deployingDelays = MutableList(7) { 0L }
        var deployingSequence = 0
        for (i in 0 until 7) {
            val isComingFromGarage = finalAssignments[i] !in garagePositions && previousAssignments.value[i] in garagePositions
            if (isComingFromGarage) {
                deployingDelays[i] = deployingSequence * staggerDelay
                deployingSequence++
            }
        }

        // Apply the deploying phase delays
        carDelays = deployingDelays
        // Apply the second phase (deploying cars from garage).
        carAssignments = finalAssignments

        // Update the previous state for the next number change.
        previousAssignments.value = finalAssignments
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(180.dp)
            .background(Color.Black.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(180.dp) // Approximate width of the digit layout
                .height(280.dp)
                .background(Color.Yellow)// Approximate height of the digit layout
        ) {
            // The rendering part remains the same.
            for (i in 0 until 7) {
                val targetPosition = carAssignments[i]
                Car(carIndex = i, target = targetPosition, delay = carDelays[i])
            }
        }
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
                    1 -> SegmentPosition.GarageTopLeft
                    0, 2 -> SegmentPosition.GarageTopRight
                    3 -> if (Random().nextBoolean()) SegmentPosition.GarageBottomLeft else SegmentPosition.GarageTopLeft
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
