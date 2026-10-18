package com.jarvis.ai.ui.components
import androidx.compose.runtime.getValue

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jarvis.ai.ui.theme.LocalReducedMotion
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

/**
 * Waveform bars. Animated while [active]; flat when idle or when reduced motion is on.
 * This is a visual animation only. It does NOT read microphone audio levels yet
 * (that comes with voice input in Stage 3).
 */
@Composable
fun Waveform(
    active: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    barCount: Int = 28
) {
    val reduced = LocalReducedMotion.current
    val animate = active && !reduced
    val transition = rememberInfiniteTransition(label = "waveform")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing)),
        label = "phase"
    )

    Canvas(modifier = modifier.fillMaxWidth().height(48.dp)) {
        val slot = size.width / barCount
        val barWidth = slot * 0.55f
        val midY = size.height / 2f
        for (i in 0 until barCount) {
            val level = if (animate) {
                0.2f + 0.8f * abs(sin(phase + i * 0.45f))
            } else {
                0.12f
            }
            val h = size.height * level
            val x = i * slot + (slot - barWidth) / 2f
            drawRoundRect(
                color = color.copy(alpha = 0.9f),
                topLeft = Offset(x, midY - h / 2f),
                size = Size(barWidth, h),
                cornerRadius = CornerRadius(barWidth / 2f)
            )
        }
    }
}
