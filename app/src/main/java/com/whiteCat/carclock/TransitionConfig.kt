package com.whiteCat.carclock

data class PathDefinition(
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
                PathDefinition(0, SegmentPosition.Top,         SegmentPosition.GarageTopCenter),
                PathDefinition(6, SegmentPosition.Bottom,      SegmentPosition.GarageBottomCenter),
                PathDefinition(4, SegmentPosition.BottomLeft,  SegmentPosition.GarageBottomLeft),
                PathDefinition(1, SegmentPosition.TopLeft,     SegmentPosition.GarageBottomCenter),

                PathDefinition(3, SegmentPosition.GarageBottomLeft,      SegmentPosition.GarageBottomLeft),
                PathDefinition(2, SegmentPosition.TopRight,    SegmentPosition.TopRight),
                PathDefinition(5, SegmentPosition.BottomRight, SegmentPosition.BottomRight)
            )
        ),
        (1 to 0) to LayoutConfig(
            paths = listOf(
                PathDefinition(1,  SegmentPosition.GarageBottomCenter  ,SegmentPosition.TopLeft  ),
                PathDefinition(4,  SegmentPosition.GarageBottomLeft  ,SegmentPosition.BottomLeft),
                PathDefinition(6,  SegmentPosition.GarageBottomCenter,SegmentPosition.Bottom ),
                PathDefinition(0,  SegmentPosition.GarageTopCenter   ,SegmentPosition.Top      ),

                PathDefinition(3, SegmentPosition.GarageBottomLeft,      SegmentPosition.GarageBottomLeft),
                PathDefinition(2, SegmentPosition.TopRight,    SegmentPosition.TopRight),
                PathDefinition(5, SegmentPosition.BottomRight, SegmentPosition.BottomRight)
            )
        )
    )
}