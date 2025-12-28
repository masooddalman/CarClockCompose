import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.whiteCat.carclock.SegmentPosition
import kotlin.math.abs

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