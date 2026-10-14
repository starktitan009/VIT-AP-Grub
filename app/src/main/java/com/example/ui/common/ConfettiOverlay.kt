package com.example.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class ConfettiParticle(
    val xRatio: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val angle: Float
)

@Composable
fun ConfettiOverlay(triggerTime: Long) {
    if (triggerTime == 0L) return

    val colors = listOf(
        Color(0xFFFF6B00),
        Color(0xFFFFA726),
        Color(0xFF38BDF8),
        Color(0xFF22C55E),
        Color(0xFFA855F7),
        Color(0xFFEC4899),
        Color(0xFFFBBF24)
    )

    val particles = remember(triggerTime) {
        List(60) {
            ConfettiParticle(
                xRatio = Random.nextFloat(),
                speed = Random.nextFloat() * 0.7f + 0.3f,
                size = Random.nextFloat() * 12f + 8f,
                color = colors.random(),
                angle = Random.nextFloat() * 360f
            )
        }
    }

    val progress = remember(triggerTime) { Animatable(0f) }

    LaunchedEffect(triggerTime) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2400, easing = LinearEasing)
        )
    }

    if (progress.value < 1f) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            particles.forEach { p ->
                val currentY = progress.value * height * p.speed * 1.3f
                val currentX = p.xRatio * width + kotlin.math.sin((progress.value * 10f + p.angle).toDouble()).toFloat() * 30f
                val alpha = (1f - progress.value).coerceIn(0f, 1f)

                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = p.size / 2,
                    center = Offset(currentX, currentY)
                )
            }
        }
    }
}
