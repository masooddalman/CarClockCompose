import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.whiteCat.carclock.PathDefinition
import com.whiteCat.carclock.R
import com.whiteCat.carclock.RotationState
import com.whiteCat.carclock.SegmentPosition
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

// --- DATA STRUCTURES AND HELPERS (Place at top of file) ---

private operator fun Float.times(offset: Offset) = offset * this

/**
 * Calculates a point on a cubic Bézier curve.
 *
 * This function uses the cubic Bézier formula to determine the coordinates of a point
 * on the curve at a specific progression `t`. The curve is defined by four points:
 * a start point (p0), two control points (p1 and p2), and an end point (p3).
 *
 * The formula is: B(t) = (1-t)³P₀ + 3(1-t)²tP₁ + 3(1-t)t²P₂ + t³P₃
 *
 * @param rotationValue The progression along the curve, a value between 0.0f (start) and 1.0f (end).
 * @param startPoint The starting point of the curve.
 * @param cp1 The first control point, which influences the curve's direction from the start.
 * @param cp2 The second control point, which influences the curve's direction towards the end.
 * @param endpoint The ending point of the curve.
 * @return An [Offset] representing the calculated point on the curve for the given `t`.
 */
fun getBezierPoint(rotationValue: Float, startPoint: Offset, cp1: Offset, cp2: Offset, endpoint: Offset): Offset {
    val u = 1 - rotationValue
    val tt = rotationValue * rotationValue
    val uu = u * u
    val uuu = uu * u
    val ttt = tt * rotationValue
    return uuu * startPoint + 3 * uu * rotationValue * cp1 + 3 * u * tt * cp2 + ttt * endpoint
}

/**
 * Calculates the tangent vector of a cubic Bezier curve at a given time `t`.
 * The tangent vector indicates the direction of the curve at that point.
 * This is the derivative of the Bezier curve equation.
 *
 * @param rotationValue The time parameter along the curve, from 0.0 to 1.0.
 * @param startPoint The start point of the curve.
 * @param cp1 The first control point.
 * @param cp2 The second control point.
 * @param endPoint The end point of the curve.
 * @return The tangent vector as an [Offset], representing the direction and speed at point `t`.
 */
fun getBezierTangent(rotationValue: Float, startPoint: Offset, cp1: Offset, cp2: Offset, endPoint: Offset): Offset {
    val u = 1 - rotationValue
    // The derivative of the Bézier curve formula
    return 3f * u * u * (cp1 - startPoint) + 6f * u * rotationValue * (cp2 - cp1) + 3f * rotationValue * rotationValue * (endPoint - cp2)
}

// Data class to hold position, rotation, and a stable control point
private data class SegmentDetails(val position: Offset, val controlPoint: Offset)


@Composable
fun Car(path: PathDefinition, delay: Long = 300) {
    val gridSize = 50f
    val carIndex = path.carIndex

    val controlPointDistance = remember(carIndex) {
        val baseDistance = 80f
        val perCarOffset = 15f
        baseDistance + (carIndex * perCarOffset)
    }

    val segmentDetailsMap = remember(controlPointDistance) {
        SegmentPosition.values().associateWith { segment ->
            val position = Offset(segment.x * gridSize, segment.y * gridSize)
            val angleRad = Math.toRadians(segment.rotation.toDouble()).toFloat()
            val controlPoint = Offset(
                x = position.x + controlPointDistance * cos(angleRad),
                y = position.y + controlPointDistance * sin(angleRad)
            )
            SegmentDetails(position, controlPoint)
        }
    }

    val initialPosition = segmentDetailsMap.getValue(path.start).position
    var currentPos by remember { mutableStateOf(initialPosition) }
    var currentRotation by remember { mutableStateOf(RotationState.getRotation(carIndex)) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(path) {
        val startDetails = segmentDetailsMap.getValue(path.start)
        val endDetails = segmentDetailsMap.getValue(path.end)
        val startPosition = startDetails.position
        val endPosition = endDetails.position

        // If start and end are the same, snap to the position and do nothing.
        if (startPosition == endPosition) {
            currentPos = endPosition
            Log.v("car${carIndex}","start == end > pathEnd:${path.end.rotation} - currentRotation:${currentRotation} - lastKnownRotation : ${RotationState.getRotation(carIndex)}")
            currentRotation = RotationState.getRotation(carIndex)
            progress.snapTo(1f) // Mark as "done"
            return@LaunchedEffect
        }

        // cp1 is the control point associated with the START position
        // cp2 is the control point associated with the END position
        val controlPoint1 = startDetails.controlPoint
        val controlPoint2 = endDetails.controlPoint

        progress.snapTo(0f)
        progress.animateTo(
            1f,
            tween(
                durationMillis = 3000,
                easing = FastOutSlowInEasing,
                delayMillis = delay.toInt()
            )
        ) {
            currentPos = getBezierPoint(value, startPosition, controlPoint1, controlPoint2, endPosition)
            val tangent = getBezierTangent(value, startPosition, controlPoint1, controlPoint2, endPosition)
            if (tangent.getDistanceSquared() > 0) {
                currentRotation = Math.toDegrees(atan2(tangent.y, tangent.x).toDouble()).toFloat()

            }
        }
        currentPos = endPosition
        RotationState.updateRotation(carIndex,currentRotation)
    }

    Box(
        modifier = Modifier
            .offset(x = currentPos.x.dp + 10.dp, y = currentPos.y.dp + 10.dp)
            .rotate(currentRotation)
            .size(width = 75.dp, height = 30.dp),
//            .background(Color.Red, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.car_white1),
            modifier = Modifier
                .width(75.dp)
                .aspectRatio(1f),
            contentDescription = "car${carIndex}"
        )
    }
}
