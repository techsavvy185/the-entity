package com.bitbenders.theentity.ui.screens.p1_screens.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.bitbenders.theentity.ui.components.screenShake
import com.bitbenders.theentity.ui.components.staticNoise
import com.bitbenders.theentity.ui.theme.EntityBlack
import com.bitbenders.theentity.ui.theme.EntityBorder
import com.bitbenders.theentity.ui.theme.EntityGreen
import com.bitbenders.theentity.ui.theme.EntityRed

@Composable
fun P1TerminalScreen(
    viewModel: P1TerminalViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current
    val listState = rememberLazyListState()

    // Haptic feedback on shake
    LaunchedEffect(uiState.isShaking) {
        if (uiState.isShaking) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    // Auto-scroll to latest message
    LaunchedEffect(uiState.chatHistory.size) {
        if (uiState.chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(uiState.chatHistory.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EntityBlack)
            .screenShake(uiState.isShaking)
            .staticNoise(uiState.currentStaticIntensity)
            .padding(16.dp)
    ) {
        // ═══════ HEADER ═══════
        // Status bar with timer and strikes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, EntityBorder)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Timer
            Text(
                text = "T-${uiState.timerString}",
                color = EntityGreen,
                style = MaterialTheme.typography.titleLarge
            )

            // Round/Persona info
            Text(
                text = uiState.currentPersona,
                color = EntityGreen,
                style = MaterialTheme.typography.labelLarge
            )

            // Strikes with color coding
            Text(
                text = "R${uiState.roundNumber} ${uiState.roundPhase.name}",
                color = EntityGreen,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "STRIKES ${uiState.currentStrikes}/${uiState.maxStrikes}",
                color = if (uiState.currentStrikes > 0) EntityRed else EntityGreen,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Cipher slots row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, EntityBorder)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SLOTS:",
                color = EntityGreen,
                style = MaterialTheme.typography.labelLarge
            )

            uiState.cipherSlots.forEachIndexed { _, chunk ->
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .border(1.dp, EntityGreen)
                        .background(EntityBlack)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "[ ${chunk?.textValue ?: "_"} ]",
                        color = if (chunk != null) EntityGreen else EntityGreen.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ═══════ CHAT DISPLAY ═══════
        // Terminal view for chat history with typewriter effect
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, EntityGreen)
                .background(EntityBlack)
                .padding(12.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.chatHistory) { message ->
                    // Color different message types
                    val textColor = when {
                        message.startsWith("ERR:") -> EntityRed
                        message.startsWith("[ENTITY_ZERO]:") -> EntityGreen
                        message.startsWith("[SYSTEM]:") -> EntityRed
                        message.startsWith(">>>") -> EntityGreen.copy(alpha = 0.8f)
                        message.startsWith(">") -> EntityGreen.copy(alpha = 0.6f)
                        else -> EntityGreen
                    }

                    Text(
                        text = message,
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                // Show the typewriter effect line being typed
                if (uiState.typewriterLine.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            Text(
                                text = uiState.typewriterLine,
                                color = EntityGreen,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            // Blinking cursor during typewriter effect
                            if (uiState.showTypingCursor) {
                                Text(
                                    text = "_",
                                    color = EntityGreen,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                } else {
                    // Show blinking cursor prompt after last message when not typing
                    item {
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            Text(
                                text = "> ",
                                color = EntityGreen.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            // Blinking cursor below last message
                            if (uiState.showInputCursor) {
                                Text(
                                    text = "_",
                                    color = EntityGreen,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ═══════ INPUT FIELD ═══════
        // Terminal input with send on action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, EntityGreen),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Input text field
            BasicTextField(
                value = uiState.inputText,
                onValueChange = { viewModel.onInputChanged(it) },
                modifier = Modifier
                    .weight(1f)
                    .background(EntityBlack)
                    .padding(12.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = EntityGreen),
                cursorBrush = SolidColor(EntityGreen),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.submitPrompt()
                    }
                )
            )

            // Send button / visual indicator
            Box(
                modifier = Modifier
                    .background(EntityBlack)
                    .border(1.dp, EntityBorder)
                    .padding(12.dp)
                    .clickable(enabled = uiState.inputText.isNotBlank()) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.submitPrompt()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⏎",
                    color = if (uiState.inputText.isNotBlank()) EntityGreen else EntityGreen.copy(alpha = 0.3f),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
