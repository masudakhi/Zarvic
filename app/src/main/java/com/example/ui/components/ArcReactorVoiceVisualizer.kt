package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisBlue
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisNeonGreen
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ArcReactorVoiceVisualizer(
    isListening: Boolean,
    isSpeaking: Boolean,
    amplitude: Float,
    size: Dp = 220.dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ArcReactorRotation")

    // Slow continuous outer rotation
    val outerAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outerAngle"
    )

    // Reverse middle rotation
    val innerAngle by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "innerAngle"
    )

    // Subtle breathing pulse
    val idlePulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idlePulse"
    )

    // Scale calculation based on state and real-time audio amplitude
    val dynamicScale = when {
        isSpeaking -> 1.05f + (amplitude.coerceIn(0f, 1f) * 0.25f)
        isListening -> 1.08f + (amplitude.coerceIn(0f, 1f) * 0.2f)
        else -> idlePulse
    }

    val primaryAuraColor = when {
        isSpeaking -> JarvisCyan
        isListening -> JarvisNeonGreen
        else -> JarvisBlue
    }

    Box(
        modifier = modifier
            .size(size)
            .clickable(onClick = onClick)
            .testTag("arc_reactor_voice_visualizer"),
        contentAlignment = Alignment.Center
    ) {
        // High-tech Arc Reactor Canvas Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = this.size.minDimension / 2f

            // Outer energy ring
            drawCircle(
                color = primaryAuraColor.copy(alpha = 0.25f),
                radius = baseRadius * 0.95f,
                style = Stroke(width = 2.dp.toPx())
            )

            // Outer segmented ticks
            val tickCount = 24
            for (i in 0 until tickCount) {
                val angleRad = Math.toRadians((outerAngle + (i * 360f / tickCount)).toDouble())
                val start = Offset(
                    (center.x + (baseRadius * 0.88f) * cos(angleRad)).toFloat(),
                    (center.y + (baseRadius * 0.88f) * sin(angleRad)).toFloat()
                )
                val end = Offset(
                    (center.x + (baseRadius * 0.95f) * cos(angleRad)).toFloat(),
                    (center.y + (baseRadius * 0.95f) * sin(angleRad)).toFloat()
                )
                drawLine(
                    color = primaryAuraColor.copy(alpha = if (i % 3 == 0) 0.8f else 0.35f),
                    start = start,
                    end = end,
                    strokeWidth = if (i % 3 == 0) 2.5.dp.toPx() else 1.2.dp.toPx()
                )
            }

            // Middle dynamic glowing arc segments
            val segmentRadius = baseRadius * 0.78f
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        primaryAuraColor.copy(alpha = 0.1f),
                        primaryAuraColor,
                        JarvisCyan.copy(alpha = 0.8f),
                        Color.Transparent
                    )
                ),
                startAngle = innerAngle,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(center.x - segmentRadius, center.y - segmentRadius),
                size = androidx.compose.ui.geometry.Size(segmentRadius * 2, segmentRadius * 2),
                style = Stroke(width = 3.dp.toPx())
            )

            // Inner glowing ripple during voice activity
            if (isListening || isSpeaking) {
                drawCircle(
                    color = primaryAuraColor.copy(alpha = 0.35f * amplitude.coerceIn(0.2f, 1f)),
                    radius = baseRadius * 0.65f * dynamicScale,
                    style = Stroke(width = 4.dp.toPx())
                )
            }
        }

        // Central Holographic Avatar Core using the user's uploaded image
        Box(
            modifier = Modifier
                .size(size * 0.54f)
                .scale(dynamicScale)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    spotColor = primaryAuraColor,
                    ambientColor = primaryAuraColor
                )
                .clip(CircleShape)
                .background(JarvisBackground)
                .border(2.5.dp, primaryAuraColor, CircleShape)
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.user_avatar),
                contentDescription = "JARVIS Voice & User Holographic Avatar",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            // Holographic scanline overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                primaryAuraColor.copy(alpha = if (isSpeaking || isListening) 0.25f else 0.1f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}
