package com.whiteCat.carclock

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ClockDigitSeparator(onNext: (() -> Unit)? = null, onPrevious: (() -> Unit)? = null){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.cone),
            modifier = Modifier.width(25.dp)
                .aspectRatio(1f)
                .clickable(
                    enabled = onNext != null,
                    onClick = { onNext?.invoke() }
                ),
            contentDescription = "Clock Separator Cone"
        )

        Spacer(modifier = Modifier.padding(64.dp))

        Image(
            painter = painterResource(id = R.drawable.cone),
            modifier = Modifier.width(25.dp)
                .aspectRatio(1f).rotate(180f)
                .clickable(
                    enabled = onPrevious != null,
                    onClick = { onPrevious?.invoke() }
                ),
            contentDescription = "Clock Separator Cone"
        )
    }
}

@Preview
@Composable
fun ClockDigitSeparatorPreview(){
    ClockDigitSeparator()
}