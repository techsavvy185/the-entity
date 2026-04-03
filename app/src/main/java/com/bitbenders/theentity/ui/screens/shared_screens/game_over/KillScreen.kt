package com.bitbenders.theentity.ui.screens.shared_screens.game_over

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.bitbenders.theentity.ui.theme.EntityBlack

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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EntityBlack),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = uiState.typedText,
            color = Color.White,
            fontSize = 24.sp,
            fontFamily = FontFamily.Monospace // Pure stark monospace contrast
        )
    }
}

