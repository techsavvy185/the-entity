package com.bitbenders.theentity.ui.utils

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Typewriter effect - text appears character by character.
 * Useful for system messages and dialogue.
 */
@Composable
fun TypewriterEffect(
    text: String,
    durationMs: Int = 50,
): String {
    val infiniteTransition = rememberInfiniteTransition(label = "typewriter")
    val charIndex = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = text.length.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(text.length * durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "char_index"
    )

    return text.substring(0, minOf(charIndex.value.toInt(), text.length))
}

/**
 * Glitch effect - creates visual corruption on text/elements.
 * Useful for error messages and forbidden word feedback.
 */
@Composable
fun GlitchEffect(
    intensity: Float = 1f, // 0f to 1f
): Modifier {
    val transition = rememberInfiniteTransition(label = "glitch")

    val offsetX = transition.animateFloat(
        initialValue = 0f,
        targetValue = 10f * intensity,
        animationSpec = infiniteRepeatable(
            animation = tween(200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glitch_x"
    )

    val offsetY = transition.animateFloat(
        initialValue = 0f,
        targetValue = 5f * intensity,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glitch_y"
    )

    return Modifier.offset(x = offsetX.value.dp, y = offsetY.value.dp)
}

/**
 * Pulse effect - element scales in and out smoothly.
 * Useful for success feedback or important indicators.
 */
@Composable
fun PulseEffect(
    durationMs: Int = 1000,
    minScale: Float = 0.9f,
    maxScale: Float = 1.1f,
): Modifier {
    val transition = rememberInfiniteTransition(label = "pulse")

    val scale = transition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs / 2, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    return Modifier.graphicsLayer(scaleX = scale.value, scaleY = scale.value)
}

/**
 * Fade in effect - element gradually appears.
 * Useful for new messages or UI elements.
 */
@Composable
fun FadeInEffect(
    durationMs: Int = 500,
): Modifier {
    val transition = rememberInfiniteTransition(label = "fade_in")

    val alpha = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Restart
        ),
        label = "fade_alpha"
    )

    return Modifier.graphicsLayer(alpha = alpha.value)
}

/**
 * Bounce effect - element bounces up and down.
 * Useful for attract attention or confirm actions.
 */
@Composable
fun BounceEffect(
    durationMs: Int = 600,
    bounceHeightDp: Float = 10f,
): Modifier {
    val transition = rememberInfiniteTransition(label = "bounce")

    val bounceY = transition.animateFloat(
        initialValue = 0f,
        targetValue = bounceHeightDp,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce_y"
    )

    return Modifier.offset(y = bounceY.value.dp)
}

/**
 * Rotation effect - element spins continuously.
 * Useful for loading indicators.
 */
@Composable
fun RotationEffect(
    durationMs: Int = 2000,
): Modifier {
    val transition = rememberInfiniteTransition(label = "rotation")

    val rotation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate_degrees"
    )

    return Modifier.graphicsLayer(rotationZ = rotation.value)
}

/**
 * Shake effect - element vibrates horizontally.
 * Useful for errors or warnings.
 */
@Composable
fun ShakeEffect(
    durationMs: Int = 200,
    shakeAmount: Float = 5f,
): Modifier {
    val transition = rememberInfiniteTransition(label = "shake")

    val shakeX = transition.animateFloat(
        initialValue = -shakeAmount,
        targetValue = shakeAmount,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake_x"
    )

    return Modifier.offset(x = shakeX.value.dp)
}

/**
 * Blink effect - element fades in and out.
 * Useful for alerts or important messages.
 */
@Composable
fun BlinkEffect(
    durationMs: Int = 1000,
): Modifier {
    val transition = rememberInfiniteTransition(label = "blink")

    val alpha = transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs / 2, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink_alpha"
    )

    return Modifier.graphicsLayer(alpha = alpha.value)
}

