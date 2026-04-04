package com.bitbenders.theentity.ui.screens.shared_screens.game_over

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

private val TerminalGreen = Color(0xFF39FF14)
private val DimGreen = Color(0xFF1A7A0A)

@Composable
fun VictoryScreen(
    viewModel: VictoryScreenViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(uiState.phase) {
        if (uiState.phase == VictoryPhase.Complete) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EntityBlack),
        contentAlignment = Alignment.Center
    ) {
        when (uiState.phase) {
            VictoryPhase.Static -> {
                // Garbage static filling screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    uiState.staticLines.forEach { line ->
                        Text(
                            text = line,
                            color = DimGreen.copy(alpha = 0.7f),
                            style = EntityTypography.bodyMedium.copy(
                                fontSize = 11.sp,
                                lineHeight = 13.sp
                            ),
                            maxLines = 1
                        )
                    }
                }
            }

            VictoryPhase.Purge -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "PURGING ENTITY...",
                        color = TerminalGreen,
                        style = EntityTypography.headlineMedium.copy(
                            fontSize = 18.sp,
                            lineHeight = 20.sp
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                    // Progress bar made of block characters
                    val filled = (uiState.purgePercent * 30 / 100)
                    val bar = "█".repeat(filled) + "░".repeat(30 - filled)
                    Text(
                        text = "[$bar]",
                        color = TerminalGreen,
                        style = EntityTypography.bodyMedium.copy(
                            fontSize = 12.sp,
                            lineHeight = 14.sp
                        )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "${uiState.purgePercent}%",
                        color = TerminalGreen,
                        style = EntityTypography.headlineMedium.copy(
                            fontSize = 24.sp,
                            lineHeight = 26.sp
                        )
                    )
                }
            }

            VictoryPhase.Reveal, VictoryPhase.Complete -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    Text(
                        text = uiState.finalText,
                        color = TerminalGreen,
                        style = EntityTypography.displayLarge.copy(
                            fontSize = 34.sp,
                            lineHeight = 40.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    AnimatedVisibility(
                        visible = uiState.showSubtext,
                        enter = fadeIn()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(Modifier.height(24.dp))
                            Text(
                                text = "SYSTEM INTEGRITY RESTORED",
                                color = TerminalGreen.copy(alpha = 0.6f),
                                style = EntityTypography.labelLarge.copy(
                                    fontSize = 12.sp,
                                    lineHeight = 14.sp
                                )
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "You got out. Not everyone does.",
                                color = Color(0xFF8CEAAA).copy(alpha = 0.5f),
                                style = EntityTypography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    lineHeight = 18.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
