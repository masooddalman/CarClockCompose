package com.whiteCat.carclock

data class SegmentPosition(val x: Int, val y: Int, val rotation: Float) {

    companion object{

        val FaceRight = 0f
        val FaceLeft = 180f
        val FaceUp = -90f
        val FaceDown = 90f


        // static segment positions of a digit number
        val Top = SegmentPosition(1, 0, FaceRight)
        val TopLeft = SegmentPosition(0, 1, FaceUp)
        val TopRight = SegmentPosition(2, 1, FaceDown)
        val Middle = SegmentPosition(1, 2, FaceRight)
        val BottomLeft = SegmentPosition(0, 3, FaceUp)
        val BottomRight = SegmentPosition(2, 3, FaceDown)
        val Bottom = SegmentPosition(1, 4, FaceLeft)
        // garage positions
        val GarageTopLeft = SegmentPosition(-2, -10, 45f)
        val GarageTopCenter = SegmentPosition(1, -10, 0f)
        val GarageTopRight = SegmentPosition(4, -10, -45f)
        val GarageBottomLeft = SegmentPosition(-2, 15, -45f)
        val GarageBottomCenter = SegmentPosition(1, 15, 0f)
        val GarageBottomRight = SegmentPosition(4, 15, 45f)


        val values = listOf(
            Top, TopLeft, TopRight, Middle, BottomLeft, BottomRight, Bottom,
            GarageTopLeft, GarageTopCenter, GarageTopRight, GarageBottomLeft, GarageBottomCenter, GarageBottomRight
        )
    }
}

//// static segment positions of a digit number
//enum class SegmentPosition(val x: Int, val y: Int, val rotation: Float) {
//    Top(1, 0, 0f),
//    TopLeft(0, 1, -90f),
//    TopRight(2, 1, 90f),
//    Middle(1, 2, 0f),
//    BottomLeft(0, 3, -90f),
//    BottomRight(2, 3, 90f),
//    Bottom(1, 4, 180f),
//    // garage positions
//    GarageTopLeft(-2, -10, 45f),
//    GarageTopCenter(1, -10, 0f),
//    GarageTopRight(4, -10, -45f),
//    GarageBottomLeft(-2, 15, -45f),
//    GarageBottomCenter(1, 15, 0f),
//    GarageBottomRight(4, 15, 45f)
//}