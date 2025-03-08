package com.keneth.realestateapplication.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ConfettiScreen() {
    var showConfetti by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (showConfetti) {
            ConfettiEffect()
        }

        Button(
            onClick = { showConfetti = !showConfetti },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("Toggle Confetti")
        }
    }
}