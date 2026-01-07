package com.whiteCat.carclock

import androidx.compose.ui.geometry.Offset
import com.whiteCat.carclock.SegmentPosition.Companion.FaceDown
import com.whiteCat.carclock.SegmentPosition.Companion.FaceLeft
import com.whiteCat.carclock.SegmentPosition.Companion.FaceRight
import com.whiteCat.carclock.SegmentPosition.Companion.FaceUp


data class PathDefinition (
    val carIndex: Int,
    val start: SegmentPosition,
    val end: SegmentPosition
)

data class LayoutConfig(
    val paths: List<PathDefinition>
)


class TransitionConfig {
    companion object{
        private var instance: TransitionConfig? = null

        fun getInstance(): TransitionConfig {
            if (instance == null) {
                instance = TransitionConfig()
            }
            return instance!!
        }
    }



    val animationDuration = 7000
    val staggerDelay = 1500L

    /**
     * A map that defines the transition configurations between different clock digit layouts.
     *
     * The key of the map is a `Pair<Int, Int>` representing the transition from a starting digit
     * (the first `Int`) to a target digit (the second `Int`). For example, `(0 to 1)` defines the
     * animation for transitioning from digit '0' to digit '1'.
     *
     * The value is a [LayoutConfig] object, which contains a list of [PathDefinition]s. Each
     * `PathDefinition` specifies the movement of a single car segment from its start position to
     * its end position.
     *
     * The order of `PathDefinition`s within the `paths` list is crucial as it dictates the
     * sequence of the animations. Cars that need to move are typically placed at the beginning of
     * the list to ensure they animate first, while static cars (whose start and end positions
     * are the same) are placed at the end.
     */
    val data = mapOf(
        (0 to 1) to LayoutConfig(
            paths = listOf(
                PathDefinition(0, SegmentPosition.Top, SegmentPosition.GarageTopRight),
                PathDefinition(6, SegmentPosition.Bottom, SegmentPosition.GarageBottomLeft),
                PathDefinition(1, SegmentPosition.TopLeft, SegmentPosition.GarageTopLeft),
                PathDefinition(4, SegmentPosition.BottomLeft, SegmentPosition.GarageTopLeft),
                PathDefinition(
                    3,
                    SegmentPosition.GarageBottomLeft,
                    SegmentPosition.GarageBottomLeft
                ),

                PathDefinition(2, SegmentPosition.TopRight, SegmentPosition.TopRight),
                PathDefinition(5, SegmentPosition.BottomRight, SegmentPosition.BottomRight)
            )
        ),
        (1 to 2) to LayoutConfig(
            paths = listOf(
                PathDefinition(5, SegmentPosition.BottomRight, SegmentPosition.GarageBottomRight),
//                PathDefinition(2, SegmentPosition.TopRight,    SegmentPosition.GarageBottomCenter),
                PathDefinition(
                    3,
                    SegmentPosition.GarageBottomRight,
                    SegmentPosition.Middle.copy(rotation = FaceLeft)
                ),
                PathDefinition(
                    4,
                    SegmentPosition.GarageTopLeft,
                    SegmentPosition.BottomLeft.copy(rotation = FaceDown)
                ),
                PathDefinition(
                    0,
                    SegmentPosition.GarageTopLeft,
                    SegmentPosition.Top.copy(rotation = FaceRight)
                ),
                PathDefinition(
                    6,
                    SegmentPosition.GarageBottomRight,
                    SegmentPosition.Bottom.copy(rotation = FaceRight)
                ),

                PathDefinition(2, SegmentPosition.TopRight, SegmentPosition.TopRight),
            )
        ),
        (2 to 3) to LayoutConfig(
            paths = listOf(
                PathDefinition(2, SegmentPosition.TopRight, SegmentPosition.GarageBottomRight),
                PathDefinition(
                    4,
                    SegmentPosition.BottomLeft.copy(rotation = FaceDown),
                    SegmentPosition.GarageBottomLeft
                ),
                PathDefinition(
                    2,
                    SegmentPosition.GarageBottomRight,
                    SegmentPosition.TopRight.copy(rotation = FaceUp)
                ),
                PathDefinition(
                    5,
                    SegmentPosition.GarageBottomRight,
                    SegmentPosition.BottomRight.copy(rotation = FaceUp)
                ),

                PathDefinition(0, SegmentPosition.Top, SegmentPosition.Top),
                PathDefinition(1, SegmentPosition.GarageTopLeft, SegmentPosition.GarageTopLeft),
                PathDefinition(3, SegmentPosition.Middle, SegmentPosition.Middle),
                PathDefinition(6, SegmentPosition.Bottom, SegmentPosition.Bottom),
            )
        ),
        (3 to 4) to LayoutConfig(
            paths = listOf(
                PathDefinition(
                    6,
                    SegmentPosition.Bottom.copy(rotation = FaceRight),
                    SegmentPosition.GarageBottomRight
                ),
                PathDefinition(
                    1,
                    SegmentPosition.GarageTopLeft,
                    SegmentPosition.TopLeft.copy(rotation = FaceDown)
                ),
                PathDefinition(
                    0,
                    SegmentPosition.Top.copy(rotation = FaceRight),
                    SegmentPosition.GarageTopRight
                ),

                PathDefinition(2, SegmentPosition.TopRight, SegmentPosition.TopRight),
                PathDefinition(3, SegmentPosition.Middle, SegmentPosition.Middle),
                PathDefinition(
                    4,
                    SegmentPosition.GarageBottomLeft,
                    SegmentPosition.GarageBottomLeft
                ),
                PathDefinition(5, SegmentPosition.BottomRight, SegmentPosition.BottomRight)
            )
        ),
        (4 to 5) to LayoutConfig(
            paths = listOf(
                PathDefinition(
                    0,
                    SegmentPosition.GarageTopLeft,
                    SegmentPosition.Top.copy(rotation = FaceRight)
                ),
                PathDefinition(
                    2,
                    SegmentPosition.TopRight.copy(rotation = FaceUp),
                    SegmentPosition.GarageTopCenter
                ),
                PathDefinition(
                    6,
                    SegmentPosition.GarageBottomLeft,
                    SegmentPosition.Bottom.copy(rotation = FaceRight)
                ),

                PathDefinition(
                    5,
                    SegmentPosition.BottomRight.copy(rotation = FaceUp),
                    SegmentPosition.BottomRight.copy(rotation = FaceUp)
                ),
                PathDefinition(1, SegmentPosition.TopLeft, SegmentPosition.TopLeft),
                PathDefinition(3, SegmentPosition.Middle, SegmentPosition.Middle),
                PathDefinition(
                    4,
                    SegmentPosition.GarageBottomLeft,
                    SegmentPosition.GarageBottomLeft
                ),
            )
        ),

        (5 to 6) to LayoutConfig(
            paths = listOf(
                PathDefinition(
                    1,
                    SegmentPosition.TopLeft.copy(rotation = FaceDown),
                    SegmentPosition.GarageBottomLeft
                ),
                PathDefinition(
                    4,
                    SegmentPosition.GarageTopLeft,
                    SegmentPosition.BottomLeft.copy(rotation = FaceDown)
                ),
                PathDefinition(
                    1,
                    SegmentPosition.GarageTopLeft,
                    SegmentPosition.TopLeft.copy(rotation = FaceDown)
                ),
                PathDefinition(0, SegmentPosition.Top, SegmentPosition.Top),
                PathDefinition(2, SegmentPosition.GarageTopRight, SegmentPosition.GarageTopRight),

                PathDefinition(3, SegmentPosition.Middle, SegmentPosition.Middle),
                PathDefinition(5, SegmentPosition.BottomRight, SegmentPosition.BottomRight),
                PathDefinition(6, SegmentPosition.Bottom, SegmentPosition.Bottom),
            )
        ),

        (6 to 7) to LayoutConfig(
            paths = listOf(
                PathDefinition(
                    4,
                    SegmentPosition.BottomLeft.copy(rotation = FaceDown),
                    SegmentPosition.GarageBottomLeft
                ),
                PathDefinition(
                    3,
                    SegmentPosition.Middle.copy(rotation = FaceLeft),
                    SegmentPosition.GarageBottomLeft
                ),
                PathDefinition(
                    5,
                    SegmentPosition.BottomRight.copy(rotation = FaceUp),
                    SegmentPosition.GarageTopRight
                ),
                PathDefinition(
                    6,
                    SegmentPosition.Bottom.copy(rotation = FaceRight),
                    SegmentPosition.GarageBottomRight
                ),
                PathDefinition(
                    1,
                    SegmentPosition.TopLeft.copy(rotation = FaceDown),
                    SegmentPosition.GarageBottomLeft
                ),
                PathDefinition(
                    5,
                    SegmentPosition.GarageTopRight,
                    SegmentPosition.BottomRight.copy(rotation = FaceDown)
                ),
                PathDefinition(
                    2,
                    SegmentPosition.GarageTopRight,
                    SegmentPosition.TopRight.copy(rotation = FaceDown)
                ),

                PathDefinition(0, SegmentPosition.Top, SegmentPosition.Top),


                )
        ),
        (7 to 8) to LayoutConfig(
            paths = listOf(

                PathDefinition(1, SegmentPosition.GarageBottomLeft, SegmentPosition.TopLeft),
                PathDefinition(3, SegmentPosition.GarageBottomLeft, SegmentPosition.Middle),
                PathDefinition(4, SegmentPosition.GarageBottomLeft, SegmentPosition.BottomLeft),
                PathDefinition(6, SegmentPosition.GarageBottomCenter, SegmentPosition.Bottom),

                PathDefinition(5, SegmentPosition.BottomRight, SegmentPosition.BottomRight),
                PathDefinition(2, SegmentPosition.TopRight, SegmentPosition.TopRight),
                PathDefinition(0, SegmentPosition.Top, SegmentPosition.Top),
            )
        ),
        (8 to 9) to LayoutConfig(
            paths = listOf(
                PathDefinition(1, SegmentPosition.TopLeft, SegmentPosition.GarageTopLeft),
                PathDefinition(4, SegmentPosition.BottomLeft, SegmentPosition.GarageTopLeft),
                PathDefinition(0, SegmentPosition.Top, SegmentPosition.Top),
                PathDefinition(1, SegmentPosition.GarageBottomLeft, SegmentPosition.TopLeft),

                PathDefinition(2, SegmentPosition.TopRight, SegmentPosition.TopRight),
                PathDefinition(3, SegmentPosition.Middle, SegmentPosition.Middle),
                PathDefinition(5, SegmentPosition.BottomRight, SegmentPosition.BottomRight),
                PathDefinition(6, SegmentPosition.Bottom, SegmentPosition.Bottom),
            )
        ),
        //in a digital clock only four digits (1,2,5 and 9) can transform to zero
        (1 to 0) to LayoutConfig(
            paths = listOf(
                PathDefinition(1, SegmentPosition.GarageBottomCenter, SegmentPosition.TopLeft),
                PathDefinition(4, SegmentPosition.GarageBottomLeft, SegmentPosition.BottomLeft),
                PathDefinition(6, SegmentPosition.GarageBottomCenter, SegmentPosition.Bottom),
                PathDefinition(0, SegmentPosition.GarageTopCenter, SegmentPosition.Top),

                PathDefinition(
                    3,
                    SegmentPosition.GarageBottomLeft,
                    SegmentPosition.GarageBottomLeft
                ),
                PathDefinition(2, SegmentPosition.TopRight, SegmentPosition.TopRight),
                PathDefinition(5, SegmentPosition.BottomRight, SegmentPosition.BottomRight)
            )
        ),
        (2 to 0) to LayoutConfig(
            paths = listOf(
                PathDefinition(
                    3,
                    SegmentPosition.Middle.copy(rotation = FaceLeft),
                    SegmentPosition.GarageTopLeft
                ),
                PathDefinition(
                    6,
                    SegmentPosition.Bottom.copy(rotation = FaceLeft),
                    SegmentPosition.GarageBottomLeft
                ),
                PathDefinition(
                    4,
                    SegmentPosition.BottomLeft.copy(rotation = FaceDown),
                    SegmentPosition.GarageBottomLeft
                ),
                PathDefinition(2, SegmentPosition.TopRight, SegmentPosition.GarageBottomRight),

                PathDefinition(1, SegmentPosition.GarageBottomLeft, SegmentPosition.TopLeft),
                PathDefinition(4, SegmentPosition.GarageBottomLeft, SegmentPosition.BottomLeft),
                PathDefinition(5, SegmentPosition.GarageTopRight, SegmentPosition.TopRight),
                PathDefinition(2, SegmentPosition.GarageTopRight, SegmentPosition.BottomRight),
                PathDefinition(6, SegmentPosition.GarageBottomRight, SegmentPosition.Bottom),

                PathDefinition(0, SegmentPosition.Top, SegmentPosition.Top),
            )
        ),
        (5 to 0) to LayoutConfig(
            paths = listOf(
                PathDefinition(3, SegmentPosition.Middle, SegmentPosition.GarageTopRight),
                PathDefinition(4, SegmentPosition.GarageBottomLeft, SegmentPosition.BottomLeft),
                PathDefinition(2, SegmentPosition.GarageTopRight, SegmentPosition.TopRight),

                PathDefinition(0, SegmentPosition.Top, SegmentPosition.Top),
                PathDefinition(1, SegmentPosition.TopLeft, SegmentPosition.TopLeft),
                PathDefinition(5, SegmentPosition.BottomRight, SegmentPosition.BottomRight),
                PathDefinition(6, SegmentPosition.Bottom, SegmentPosition.Bottom),
            )
        ),
        (9 to 0) to LayoutConfig(
            paths = listOf(
                PathDefinition(5, SegmentPosition.BottomRight, SegmentPosition.GarageBottomRight),
                PathDefinition(2, SegmentPosition.TopRight, SegmentPosition.GarageBottomRight),
                PathDefinition(3, SegmentPosition.Middle, SegmentPosition.GarageTopRight),
                PathDefinition(4, SegmentPosition.GarageBottomLeft, SegmentPosition.BottomLeft),
                PathDefinition(5, SegmentPosition.GarageTopRight, SegmentPosition.BottomRight),
                PathDefinition(2, SegmentPosition.GarageTopRight, SegmentPosition.TopRight),

                PathDefinition(1, SegmentPosition.TopLeft, SegmentPosition.TopLeft),
                PathDefinition(0, SegmentPosition.Top, SegmentPosition.Top),
                PathDefinition(6, SegmentPosition.Bottom, SegmentPosition.Bottom),
            )
        )
    )
}