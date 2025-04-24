package com.example.pomoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.tooling.preview.Preview
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                PomoApp()
        }
    }
}

enum class TimerMode {
    WORK,
    BREAK
}

@Composable
fun PomoApp() {
    var focusInput by remember { mutableStateOf("25") }
    var breakInput by remember { mutableStateOf("5") }

    val focusMinutes = focusInput.toIntOrNull()?.coerceIn(1, 90) ?: 25
    val maxBreakMinutes = (focusMinutes - 1).coerceAtLeast(1)
    val breakMinutes = breakInput.toIntOrNull()?.coerceIn(1, maxBreakMinutes) ?: 5

    var mode by remember { mutableStateOf(TimerMode.WORK) }
    var timeLeft by remember { mutableIntStateOf(focusMinutes * 60) }
    var isRunning by remember { mutableStateOf(false) }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val cardColor = if (mode == TimerMode.WORK) Color(0xFFE83944) else Color(0xFF81D4FA)

    // When mode changes, reset the timer based on the mode
    LaunchedEffect(isRunning) {
        timeLeft = if (mode == TimerMode.WORK) focusMinutes * 60 else breakMinutes * 60
    }

    // Timer CountDown Logic
    LaunchedEffect(isRunning) {
        while (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        if (timeLeft == 0) {
            isRunning = false
            mode = if (mode == TimerMode.WORK) TimerMode.BREAK else TimerMode.WORK

        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Inputs
        OutlinedTextField(
            value = focusInput,
            onValueChange = { focusInput = it },
            label = { Text("Set Focus Time (min)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .padding(16.dp)
        )
        OutlinedTextField(
            value = breakInput,
            onValueChange = { breakInput = it },
            label = { Text("Set Break Time (min)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Timer Display
        TimerCard(minutes, seconds,cardColor)


        Spacer(modifier = Modifier.height(24.dp))

        // CONTROLS

        TimerControls(
            isRunning = isRunning,
            onStartPause = {
                if (!isRunning && timeLeft == 0) {
                    timeLeft = if (mode == TimerMode.WORK) focusMinutes * 60 else breakMinutes * 60
                }
                isRunning = !isRunning
            },
            onReset = {
                isRunning = false
                timeLeft = if (mode == TimerMode.WORK) focusMinutes * 60 else breakMinutes * 60
            }
        )
            }

    }


@Composable
fun TimerCard(minutes: Int, seconds: Int, cardColor: Color) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = String.format(Locale.US, "%02d:%02d", minutes, seconds),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun TimerControls(
    isRunning: Boolean,
    onStartPause: () -> Unit,
    onReset: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        StartPauseButton(isRunning, onStartPause)
        ResetButton(onReset)
    }
}

@Composable
fun StartPauseButton(isRunning: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(if (isRunning) "Pause" else "Start")
    }
}

@Composable
fun ResetButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("Reset")
    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
        PomoApp()
}