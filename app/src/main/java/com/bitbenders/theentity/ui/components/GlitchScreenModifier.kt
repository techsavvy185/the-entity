package com.bitbenders.theentity.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.composed
import com.bitbenders.theentity.ui.theme.EntityGreen

fun Modifier.screenShake(
    isShaking: Boolean,
): Modifier = composed {
    if (!isShaking) return@composed this

    val transition = rememberInfiniteTransition(label = "shake_transition")
    val shakeX by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 80
                -6f at 0
                6f at 20
                -4f at 40
                4f at 60
                0f at 80
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "shake_x",
    )

    val shakeY by transition.animateFloat(
        initialValue = 4f,
        targetValue = -4f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 80
                4f at 0
                -4f at 20
                2f at 40
                -2f at 60
                0f at 80
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "shake_y",
    )

    this.graphicsLayer {
        translationX = shakeX
        translationY = shakeY
    }
}

fun Modifier.staticNoise(
    intensity: Float,
): Modifier = composed {
    val clamped = intensity.coerceIn(0f, 1f)
    if (clamped <= 0f) return@composed this

    val transition = rememberInfiniteTransition(label = "static_transition")
    val flicker by transition.animateFloat(
        initialValue = 0.04f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 120
                0.04f at 0
                0.12f at 30
                0.06f at 60
                0.18f at 90
                0.08f at 120
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "static_flicker",
    )

    this
        .graphicsLayer {
            alpha = (1f - (clamped * 0.2f)).coerceIn(0.8f, 1f)
        }
        .background(EntityGreen.copy(alpha = flicker * clamped))
}

