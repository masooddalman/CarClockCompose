package com.whiteCat.carclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
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
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.asphalt),
                        contentDescription = "Asphalt background",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds // Use Crop to fill without distortion
                    )
                    Scaffold(
                        containerColor = Color.Transparent,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.Transparent),

                        ) { _ ->

                        val debugMode by remember { mutableStateOf(false) }
                        val secondHandType by remember { mutableStateOf(SecondHandType.Line) }

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

                            if (!debugMode) {
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
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp) // Add padding to not have the border on the edge
                                .drawBehind {
                                    when (secondHandType) {
                                        SecondHandType.Rectangle -> {
                                            drawRectangularSecondHandle(
                                                secondProgress = secondProgress,
                                                strokeWidth = secondHandleWidth.toPx(),
                                                path = path,
                                                pathMeasure = pathMeasure,
                                                cornerRadius = secondHandleCornerRadius.toPx()
                                            )
                                        }
                                        SecondHandType.Line ->  {
                                            drawHorizontalLineSecondHandle(
                                                secondProgress = secondProgress.value,
                                                strokeWidth = secondHandleWidth.toPx()
                                            )
                                        }
                                    }
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
                                            val previousValue =
                                                if (minuteUnits == 0) 9 else minuteUnits - 1
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

}
