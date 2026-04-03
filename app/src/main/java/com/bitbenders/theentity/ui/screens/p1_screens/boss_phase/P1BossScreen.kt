package com.bitbenders.theentity.ui.screens.p1_screens.boss_phase

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.bitbenders.theentity.ui.components.screenShake
import com.bitbenders.theentity.ui.theme.EntityBlack
import com.bitbenders.theentity.ui.theme.EntityBorder
import com.bitbenders.theentity.ui.theme.EntityGreen

@Composable
fun P1BossScreen(
    viewModel: P1BossViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(uiState.isShaking) {
        if (uiState.isShaking) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EntityBlack)
            .screenShake(uiState.isShaking)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "LEXICAL CALIBRATION REQUIRED",
            color = EntityGreen,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(48.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.options) { option ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .border(2.dp, EntityBorder)
                        .background(if (option.isGlitching) EntityGreen else EntityBlack)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    // Simulated hesitant touch (just touching, not releasing)
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.onOptionTouched(option.id, committed = false)
                                    // Wait for release
                                    val success = tryAwaitRelease()
                                    if (success) {
                                        viewModel.onOptionTouched(option.id, committed = true)
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (!option.isGlitching) {
                        Text(
                            text = option.text,
                            color = EntityGreen,
                            style = MaterialTheme.typography.titleLarge
                        )
                    } else {
                        // The black censor bar glitch over the text
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.8f)
                                .background(EntityBlack)
                        )
                    }
                }
            }
        }
    }
}

