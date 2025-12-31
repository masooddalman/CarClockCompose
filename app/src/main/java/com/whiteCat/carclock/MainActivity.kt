package com.whiteCat.carclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.whiteCat.carclock.ui.theme.CarClockTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarClockTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->

                    val debugMode by remember { mutableStateOf(false) }

                    var hourTens by remember { mutableIntStateOf(0) }
                    var hourUnits by remember { mutableIntStateOf(0) }
                    var minuteTens by remember { mutableIntStateOf(0) }
                    var minuteUnits by remember { mutableIntStateOf(0) }
                    val secondProgress = remember { Animatable(0f) }

                    val path = remember { Path() }
                    val pathMeasure = remember { PathMeasure() }

                    val secondHandleWidth = 5.dp
                    val secondHandleCornerRadius = 48.dp

                    LaunchedEffect(Unit) {

                        if(!debugMode) {
                            calculateClock(
                                hour = { hourTensString ->
                                    hourTens = hourTensString[0].digitToInt()
                                    hourUnits = hourTensString[1].digitToInt()
                                },
                                minute = { minuteTensString ->
                                    minuteTens = minuteTensString[0].digitToInt()
                                    minuteUnits = minuteTensString[1].digitToInt()
                                },
                                second = { secondString ->
                                    val currentSecond = secondString.toFloat()
                                    val targetAngle = currentSecond / 60f
                                    secondProgress.animateTo(
                                        targetValue = targetAngle,
                                        animationSpec = tween(
                                            durationMillis = 600, // Animate over 1 second
                                            easing = EaseOutBounce
                                        )
                                    )
                                }
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){

                        Image(
                            painter = painterResource(id = R.drawable.asphalt),
                            contentDescription = "Asphalt background",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillBounds // Use Crop to fill without distortion
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp) // Add padding to not have the border on the edge
                                .drawBehind {

                                    val segmentPath = drawSecondHandle(
                                        path = path,
                                        pathMeasure = pathMeasure,
                                        secondProgress = secondProgress,
                                        size = size,
                                        cornerRadius = CornerRadius(secondHandleCornerRadius.toPx()),
                                        strokeWidth = secondHandleWidth.toPx()
                                    )

                                    // Draw the segmented path
                                    drawPath(
                                        path = segmentPath,
                                        color = Color.Cyan,
                                        style = Stroke(
                                            width = secondHandleWidth.toPx(),
                                            cap = StrokeCap.Round
                                        )
                                    )
                                },
                            contentAlignment = Alignment.Center

                        ){
                            Row(
                                modifier = Modifier
                                    .fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DigitalCarNumber(number = hourTens)
                                DigitalCarNumber(number = hourUnits)
                                ClockDigitSeparator(
                                    onNext = if (debugMode) {
                                        {
                                            val nextValue = (minuteUnits + 1) % 10
                                            minuteUnits = nextValue
                                        }
                                    } else null,
                                    onPrevious = if (debugMode) {
                                        {
                                            val previousValue = if (minuteUnits == 0) 9 else minuteUnits - 1
                                            minuteUnits = previousValue
                                        }
                                    } else null
                                )
                                DigitalCarNumber(number = minuteTens)
                                DigitalCarNumber(number = minuteUnits)

                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun calculateClock(hour: (String) -> Unit, minute: (String) -> Unit, second: suspend (String) -> Unit){
        val hourFormat = SimpleDateFormat("HH", Locale.ROOT)
        val minuteFormat = SimpleDateFormat("mm",
            Locale.ROOT)
        val secondFormat = SimpleDateFormat("ss", Locale.ROOT)

        while (true) {
            val currentTime = Date()
            val hourString = hourFormat.format(currentTime)
            val minuteString = minuteFormat.format(currentTime)
            val secondString = secondFormat.format(currentTime)

            hour.invoke(hourString)
            minute.invoke(minuteString)
            second.invoke(secondString)

            delay(100) // Wait for a second before updating again
        }
    }

    fun drawSecondHandle(path: Path,
                         pathMeasure: PathMeasure,
                         secondProgress: Animatable<Float, *>,
                         size: Size,
                         strokeWidth: Float,
                         cornerRadius: CornerRadius) : Path{

        // Adjust bounds for the stroke width to prevent clipping
        val bounds = Rect(
            left = strokeWidth / 2,
            top = strokeWidth / 2,
            right = size.width - strokeWidth / 2,
            bottom = size.height - strokeWidth / 2
        )

        path.reset() // Clear the path for redrawing

        // Start at the top-center
        path.moveTo(x = bounds.center.x, y = bounds.top)

        // Draw line to the top-right corner
        path.lineTo(x = bounds.right - cornerRadius.x, y = bounds.top)

        // Draw the top-right arc
        path.arcTo(
            rect = Rect(
                left = bounds.right - 2 * cornerRadius.x,
                top = bounds.top,
                right = bounds.right,
                bottom = bounds.top + 2 * cornerRadius.y
            ),
            startAngleDegrees = -90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        // Draw line down to the bottom-right corner
        path.lineTo(x = bounds.right, y = bounds.bottom - cornerRadius.y)

        // Draw the bottom-right arc
        path.arcTo(
            rect = Rect(
                left = bounds.right - 2 * cornerRadius.x,
                top = bounds.bottom - 2 * cornerRadius.y,
                right = bounds.right,
                bottom = bounds.bottom
            ),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        // Draw line to the bottom-left corner
        path.lineTo(x = bounds.left + cornerRadius.x, y = bounds.bottom)

        // Draw the bottom-left arc
        path.arcTo(
            rect = Rect(
                left = bounds.left,
                top = bounds.bottom - 2 * cornerRadius.y,
                right = bounds.left + 2 * cornerRadius.x,
                bottom = bounds.bottom
            ),
            startAngleDegrees = 90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        // Draw line up to the top-left corner
        path.lineTo(x = bounds.left, y = bounds.top + cornerRadius.y)

        // Draw the top-left arc
        path.arcTo(
            rect = Rect(
                left = bounds.left,
                top = bounds.top,
                right = bounds.left + 2 * cornerRadius.x,
                bottom = bounds.top + 2 * cornerRadius.y
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        // Draw line back to the top-center starting point
        path.lineTo(x = bounds.center.x, y = bounds.top)

        // Associate the path with the path measure
        pathMeasure.setPath(path, false)

        // Create a new path to draw the segment into
        val segmentPath = Path()

        // Get a segment of the full path based on the animated progress
        pathMeasure.getSegment(
            startDistance = 0f,
            stopDistance = pathMeasure.length * secondProgress.value,
            destination = segmentPath,
            startWithMoveTo = true
        )

       return segmentPath
    }
}
