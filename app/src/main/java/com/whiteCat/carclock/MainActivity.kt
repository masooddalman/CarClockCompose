package com.whiteCat.carclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.whiteCat.carclock.ui.theme.CarClockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarClockTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var currentDigit by remember { mutableIntStateOf(8) }
                    Column(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DigitalCarNumber(number = currentDigit)

                        Spacer(modifier = Modifier.height(30.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { if (currentDigit > 0) currentDigit-- else currentDigit = 9}) { Text("-") }
                            Text(text = "$currentDigit", modifier = Modifier.padding(16.dp))
                            Button(onClick = { if (currentDigit < 9) currentDigit++ else currentDigit = 0}) { Text("+") }
                        }
                    }
                }
            }
        }
    }
}
