package com.keneth.realestateapplication.views

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    numParticles: Int = 100, // Number of confetti particles
    colors: List<Color> = listOf(
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Yellow,
        Color.Magenta
    )
) {
    // State to hold the positions and rotations of confetti particles
    val particles = remember { mutableStateListOf<Particle>() }

    // Initialize particles with random positions, velocities, and rotations
    LaunchedEffect(Unit) {
        repeat(numParticles) {
            particles.add(
                Particle(
                    x = Random.nextFloat(),
                    y = Random.nextFloat(),
                    velocityX = Random.nextFloat() * 4 - 2,
                    velocityY = Random.nextFloat() * 4 - 2,
                    rotation = Random.nextFloat() * 360,
                    rotationSpeed = Random.nextFloat() * 4 - 2,
                    color = colors.random()
                )
            )
        }
    }

    // Animate particles
    val infiniteTransition = rememberInfiniteTransition()
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Update particle positions and rotations
    LaunchedEffect(animationProgress) {
        particles.forEachIndexed { index, particle ->
            particles[index] = particle.update()
        }
    }

    // Draw confetti particles
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val x = particle.x * size.width
            val y = particle.y * size.height

            rotate(particle.rotation, pivot = Offset(x, y)) {
                drawRect(
                    color = particle.color,
                    topLeft = Offset(x, y),
                    size = androidx.compose.ui.geometry.Size(10.dp.toPx(), 10.dp.toPx())
                )
            }
        }
    }
}

// Data class to represent a confetti particle
data class Particle(
    val x: Float,
    val y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    val color: Color
) {
    fun update(): Particle {
        return copy(
            x = x + velocityX * 0.01f,
            y = y + velocityY * 0.01f,
            rotation = rotation + rotationSpeed
        )
    }
}