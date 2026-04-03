package com.bitbenders.theentity.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import com.bitbenders.theentity.ui.theme.EntityBlack
import com.bitbenders.theentity.ui.theme.EntityBorder
import com.bitbenders.theentity.ui.theme.EntityGreen

@Composable
fun BrutalistTerminalView(
    text: String,
    modifier: Modifier = Modifier,
    showCursor: Boolean = true,
    isShaking: Boolean = false,
    staticIntensity: Float = 0f,
) {
    val cursorAlpha = rememberCursorAlpha()
    val renderedText = if (showCursor) {
        buildAnnotatedStringWithCursor(text = text, alpha = cursorAlpha)
    } else {
        AnnotatedString(text)
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(EntityBlack)
            .screenShake(isShaking)
            .staticNoise(staticIntensity),
        shape = RectangleShape,
        color = EntityBlack,
        border = BorderStroke(1.dp, EntityBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = renderedText,
                style = MaterialTheme.typography.bodyLarge,
                color = EntityGreen,
            )
        }
    }
}

@Composable
private fun rememberCursorAlpha(): Float {
    val transition = rememberInfiniteTransition(label = "cursor_transition")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 900
                1f at 0
                1f at 450
                0f at 900
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "cursor_alpha",
    )
    return alpha
}

private fun buildAnnotatedStringWithCursor(text: String, alpha: Float): AnnotatedString {
    return AnnotatedString.Builder().apply {
        append(text)
        pushStyle(SpanStyle(color = EntityGreen.copy(alpha = alpha)))
        append("█")
        pop()
    }.toAnnotatedString()
}
