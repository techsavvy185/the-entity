package com.bitbenders.theentity.ui.screens.shared_screens.game_over

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitbenders.theentity.ui.theme.EntityBlack
import com.bitbenders.theentity.ui.theme.EntityTypography

@Composable
fun KillScreen(
    viewModel: KillScreenViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(uiState.typedText) {
        if (uiState.typedText.isNotEmpty() && !uiState.isComplete) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    // Blinking cursor
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(530),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorBlink"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EntityBlack),
        contentAlignment = Alignment.Center
    ) {
        val displayText = if (!uiState.isComplete) {
            uiState.typedText + "█"
        } else {
            uiState.typedText
        }

        Text(
            text = displayText,
            color = if (!uiState.isComplete) {
                Color(0xFFFF1A1A).copy(alpha = if (displayText.endsWith("█")) cursorAlpha.coerceIn(0.4f, 1f) else 1f)
            } else {
                Color(0xFFFF1A1A)
            },
            style = EntityTypography.displayLarge.copy(
                fontSize = 32.sp,
                lineHeight = 42.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}
