import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.whiteCat.carclock.PathDefinition
import com.whiteCat.carclock.R
import com.whiteCat.carclock.RotationState
import com.whiteCat.carclock.TransitionConfig
import com.whiteCat.carclock.getCubicBezierPoint
import com.whiteCat.carclock.getCubicBezierTangent
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


// Data class to hold position, rotation, and a stable control point
private data class SegmentDetails(val position: Offset, val controlPoint: Offset)


@Composable
fun Car(path: PathDefinition,
        delay: Long = 300,
        gridSize: Float = 50f,
        carSize: Dp = 75.dp,
        margin: Dp = 10.dp) {
    val carIndex = path.carIndex

    val controlPointDistance = remember(carIndex) {
        val baseDistance = 80f
        val perCarOffset = 15f
        baseDistance + (carIndex * perCarOffset)
    }


    val initialPosition = Offset(path.start.x * gridSize, path.start.y * gridSize)
    var currentPos by remember { mutableStateOf(initialPosition) }
    var currentRotation by remember { mutableStateOf(RotationState.getRotation(carIndex)) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(path) {
        val startPosition = Offset(path.start.x * gridSize, path.start.y * gridSize)
        val endPosition = Offset(path.end.x * gridSize, path.end.y * gridSize)


        // If start and end are the same, snap to the position and do nothing.
        if (startPosition == endPosition) {
            currentPos = endPosition
            Log.v(
                "car${carIndex}",
                "start == end > pathEnd:${path.end.rotation} - currentRotation:${currentRotation} - lastKnownRotation : ${
                    RotationState.getRotation(carIndex)
                }"
            )
            currentRotation = RotationState.getRotation(carIndex)
            progress.snapTo(1f) // Mark as "done"
            return@LaunchedEffect
        }

        currentPos = startPosition
        currentRotation =
            RotationState.getRotation(carIndex) // Start with the exact last known rotation
        progress.snapTo(0f)

        // This variable will hold the final, imprecise rotation from the animation
        var finalAnimatedRotation = currentRotation

        progress.snapTo(0f)
        progress.animateTo(
            1f,
            tween(
                durationMillis = TransitionConfig.getInstance().animationDuration,
                easing = FastOutSlowInEasing,
                delayMillis = delay.toInt()
            )
        ) {

            val startAngleRad = Math.toRadians(path.start.rotation.toDouble()).toFloat()
            val cp1 = Offset(
                x = startPosition.x + controlPointDistance * cos(startAngleRad),
                y = startPosition.y + controlPointDistance * sin(startAngleRad)
            )


            val endAngleRad = Math.toRadians(path.end.rotation.toDouble()).toFloat()
            val cp2 = Offset(
                x = endPosition.x - controlPointDistance * cos(endAngleRad), // Project backwards from end
                y = endPosition.y - controlPointDistance * sin(endAngleRad)
            )


            currentPos = getCubicBezierPoint(value, startPosition, cp1, cp2, endPosition)
            val tangent = getCubicBezierTangent(value, startPosition, cp1, cp2, endPosition)
            if (tangent.getDistanceSquared() > 0) {
                currentRotation = Math.toDegrees(atan2(tangent.y, tangent.x).toDouble()).toFloat()
            }

        if (value == 1f) {
            finalAnimatedRotation = currentRotation
        }
    }

        currentPos = endPosition
        currentRotation = finalAnimatedRotation
        RotationState.updateRotation(carIndex, finalAnimatedRotation)

    }

    Box(
        modifier = Modifier
            .offset(x = currentPos.x.dp + margin, y = currentPos.y.dp + margin)
            .rotate(currentRotation)
            .width(carSize),
//            .background(Color.Red, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.car_white1),
            modifier = Modifier
                .width(carSize)
                .aspectRatio(1f),
            contentDescription = "car${carIndex}"
        )
    }
}
