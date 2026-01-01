package com.whiteCat.carclock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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

// static segment positions of a digit number
enum class SegmentPosition(val x: Int, val y: Int, val rotation: Float) {
    Top(1, 0, 0f),
    TopLeft(0, 1, -90f),
    TopRight(2, 1, 90f),
    Middle(1, 2, 0f),
    BottomLeft(0, 3, -90f),
    BottomRight(2, 3, 90f),
    Bottom(1, 4, 180f),
    // garage positions
    GarageTopLeft(-2, -3, 45f),
    GarageTopCenter(1, -3, 0f),
    GarageTopRight(4, -3, -45f),
    GarageBottomLeft(-2, 8, -45f),
    GarageBottomCenter(1, 8, 0f),
    GarageBottomRight(4, 8, 45f)
}

// if manual config missing, generate default paths
fun generateDefaultPaths(fromDigit: Int, toDigit: Int): List<PathDefinition> {
    val requiredSegmentsForFrom = digitMap[fromDigit] ?: setOf()
    val requiredSegmentsForTo = digitMap[toDigit] ?: setOf()

    val preferredSegments = listOf(
        SegmentPosition.Top,
        SegmentPosition.TopLeft,
        SegmentPosition.TopRight,
        SegmentPosition.Middle,
        SegmentPosition.BottomLeft,
        SegmentPosition.BottomRight,
        SegmentPosition.Bottom
    )

    return List(7) { carIndex ->
        val preferredSegment = preferredSegments[carIndex]

        // Determine the start position
        val startPos = if (preferredSegment in requiredSegmentsForFrom) {
            preferredSegment // Car was on the digit
        } else {
            // Car was in its peer garage
            when (carIndex) {
                0, 3 -> SegmentPosition.GarageTopCenter
                1 -> SegmentPosition.GarageTopLeft
                2 -> SegmentPosition.GarageTopRight
                4 -> SegmentPosition.GarageBottomLeft
                5 -> SegmentPosition.GarageBottomRight
                6 -> SegmentPosition.GarageBottomCenter
                else -> SegmentPosition.GarageTopCenter
            }
        }

        // Determine the end position
        val endPos = if (preferredSegment in requiredSegmentsForTo) {
            preferredSegment // Car needs to be on the digit
        } else {
            // Car needs to be in its peer garage
            when (carIndex) {
                0, 3 -> SegmentPosition.GarageTopCenter
                1 -> SegmentPosition.GarageTopLeft
                2 -> SegmentPosition.GarageTopRight
                4 -> SegmentPosition.GarageBottomLeft
                5 -> SegmentPosition.GarageBottomRight
                6 -> SegmentPosition.GarageBottomCenter
                else -> SegmentPosition.GarageTopCenter
            }
        }

        PathDefinition(carIndex, start = startPos, end = endPos)
    }
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
    var carPaths by remember {
        mutableStateOf(List(7) { i ->
            val initialGarage = when(i) {
                0, 3 -> SegmentPosition.GarageTopCenter
                1 -> SegmentPosition.GarageTopLeft
                2 -> SegmentPosition.GarageTopRight
                4 -> SegmentPosition.GarageBottomLeft
                5 -> SegmentPosition.GarageBottomRight
                else -> SegmentPosition.GarageBottomCenter
            }
            PathDefinition(carIndex = i, start = initialGarage, end = initialGarage)
        })
    }

    var previousNumber by remember { mutableStateOf(0) } // Assuming start from 0
    val staggerDelay = 500L // Delay in ms between each car starting

    LaunchedEffect(number) {
        // Requirement 1 & 2: Check for a manual config first.
        val config = TransitionConfig.getInstance().data[previousNumber to number]
        val newPaths = config?.paths ?: generateDefaultPaths(previousNumber, number)

        carPaths = newPaths
        previousNumber = number
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(180.dp),
//            .background(Color.Black.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.width(180.dp).height(280.dp)) {
            // Requirement 4: The animation order is dictated by the list order.
            carPaths.forEachIndexed { index, path ->
                Car(
                    path = path,
                    delay = index * staggerDelay
                )
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
