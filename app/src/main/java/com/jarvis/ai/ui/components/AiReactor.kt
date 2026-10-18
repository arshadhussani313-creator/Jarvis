package com.jarvis.ai.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.jarvis.ai.domain.model.AssistantStatus
import com.jarvis.ai.ui.theme.JarvisAlert
import com.jarvis.ai.ui.theme.JarvisBlue
import com.jarvis.ai.ui.theme.JarvisCyan
import com.jarvis.ai.ui.theme.JarvisOk
import com.jarvis.ai.ui.theme.LocalReducedMotion

/** Color used for the reactor and waveform in each assistant state. */
fun assistantColor(status: AssistantStatus): Color = when (status) {
    AssistantStatus.IDLE -> JarvisCyan
    AssistantStatus.LISTENING -> JarvisOk
    AssistantStatus.THINKING -> JarvisBlue
    AssistantStatus.SPEAKING -> JarvisCyan
    AssistantStatus.ERROR -> JarvisAlert
}

/**
 * Animated central reactor. Speed and pulse change per state.
 * With reduced motion enabled, rings and pulse are held still.
 */
@Composable
fun AiReactor(status: AssistantStatus, modifier: Modifier = Modifier) {
    val reduced = LocalReducedMotion.current
    val transition = rememberInfiniteTransition(label = "reactor")

    val spinState = transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animationSpec = tween(spinDurationMs(status), easing = LinearEasing)
        ),
        label = "spin"
    )
    val pulseState = transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animationSpec = tween(pulseDurationMs(status), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val color = assistantColor(status)

    Canvas(modifier = modifier.aspectRatio(1f)) {
        val center = this.center
        val radius = size.minDimension / 2f
        val angle = if (reduced) 0f else spinState.value
        val scale = if (reduced) 1f else pulseState.value

        // Outer glow.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = 0.45f), Color.Transparent),
                center = center,
                radius = radius * scale
            ),
            radius = radius * scale,
            center = center
        )

        // Three counter-rotating arc rings.
        val rings = listOf(
            Triple(0.92f, 4f, 110f),
            Triple(0.78f, 3f, 150f),
            Triple(0.64f, 6f, 90f)
        )
        rings.forEachIndexed { index, (fraction, widthDp, sweep) ->
            val r = radius * fraction
            val direction = if (index % 2 == 0) 1f else -1f
            rotate(degrees = angle * direction, pivot = center) {
                drawArc(
                    color = color.copy(alpha = 0.85f),
                    startAngle = index * 70f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(center.x - r, center.y - r),
                    size = Size(r * 2, r * 2),
                    style = Stroke(width = widthDp.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = color.copy(alpha = 0.35f),
                    startAngle = index * 70f + 180f,
                    sweepAngle = 40f,
                    useCenter = false,
                    topLeft = Offset(center.x - r, center.y - r),
                    size = Size(r * 2, r * 2),
                    style = Stroke(width = widthDp.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        // Core.
        drawCircle(color = color, radius = radius * 0.22f * scale, center = center)
        drawCircle(color = Color.White.copy(alpha = 0.8f), radius = radius * 0.08f, center = center)
    }
}

private fun spinDurationMs(status: AssistantStatus): Int = when (status) {
    AssistantStatus.IDLE -> 12000
    AssistantStatus.LISTENING -> 2400
    AssistantStatus.THINKING -> 900
    AssistantStatus.SPEAKING -> 1600
    AssistantStatus.ERROR -> 6000
}

private fun pulseDurationMs(status: AssistantStatus): Int = when (status) {
    AssistantStatus.IDLE -> 2600
    AssistantStatus.LISTENING -> 800
    AssistantStatus.THINKING -> 500
    AssistantStatus.SPEAKING -> 700
    AssistantStatus.ERROR -> 1500
}
