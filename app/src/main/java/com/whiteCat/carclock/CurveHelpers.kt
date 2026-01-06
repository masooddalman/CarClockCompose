package com.whiteCat.carclock

import androidx.compose.ui.geometry.Offset

private operator fun Float.times(offset: Offset) = offset * this

// CUBIC (2 control points)
fun getCubicBezierPoint(t: Float, p0: Offset, p1: Offset, p2: Offset, p3: Offset): Offset {
    val u = 1 - t
    val tt = t * t
    val uu = u * u
    val uuu = uu * u
    val ttt = tt * t
    return uuu * p0 + 3 * uu * t * p1 + 3 * u * tt * p2 + ttt * p3
}
fun getCubicBezierTangent(t: Float, p0: Offset, p1: Offset, p2: Offset, p3: Offset): Offset {
    val u = 1 - t
    // The derivative of the Bézier curve formula
    return 3f * u * u * (p1 - p0) + 6f * u * t * (p2 - p1) + 3f * t * t * (p3 - p2)
}

// QUADRATIC (1 control point)
fun getQuadraticBezierPoint(t: Float, p0: Offset, p1: Offset, p2: Offset): Offset {
    val u = 1 - t
    val uu = u * u
    val tt = t * t
    return uu * p0 + 2 * u * t * p1 + tt * p2
}
fun getQuadraticBezierTangent(t: Float, p0: Offset, p1: Offset, p2: Offset): Offset {
    val u = 1 - t
    return 2f * u * (p1 - p0) + 2f * t * (p2 - p1)
}

// LINEAR (0 control points)
fun getLinearPoint(t: Float, p0: Offset, p1: Offset): Offset {
    return p0 + (p1 - p0) * t
}
fun getLinearTangent(p0: Offset, p1: Offset): Offset {
    return p1 - p0
}
