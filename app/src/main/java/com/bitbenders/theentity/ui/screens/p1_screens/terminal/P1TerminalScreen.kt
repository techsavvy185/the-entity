package com.bitbenders.theentity.ui.screens.p1_screens.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.bitbenders.theentity.ui.components.RetroTerminalKeyboard
import com.bitbenders.theentity.ui.components.screenShake
import com.bitbenders.theentity.ui.components.staticNoise
import com.bitbenders.theentity.ui.effects.crtTerminalEffect
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
            .crtTerminalEffect()
            .padding(16.dp)
    ) {
        // ═══════ HEADER ═══════
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, EntityBorder)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Timer (bigger and red)
            Text(
                text = "T-${uiState.timerString}",
                color = EntityRed,
                style = MaterialTheme.typography.headlineMedium
            )

            // Spacer to push strikes to the right while keeping header balanced
            Spacer(modifier = Modifier.weight(1f))

            // Strike icons (placeholder bullets we can later replace with animated icons)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                repeat(uiState.maxStrikes) { index ->
                    val active = index < uiState.currentStrikes
                    Text(
                        text = if (active) "●" else "○",
                        color = if (active) EntityRed else EntityGreen,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Cipher slots row – four boxes fill the row, no "SLOTS" label
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, EntityBorder)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            uiState.cipherSlots.forEach { chunk ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
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
                    val (displayMessage, explicitColor) = parseTerminalColorMarkup(message)
                    // Color different message types
                    val textColor = explicitColor ?: when {
                        displayMessage.startsWith("ERR:") -> EntityRed
                        displayMessage.startsWith("[ENTITY_ZERO]:") -> EntityGreen
                        displayMessage.startsWith("[SYSTEM]:") -> EntityRed
                        displayMessage.startsWith(">>>") -> EntityGreen.copy(alpha = 0.8f)
                        displayMessage.startsWith(">") -> EntityGreen.copy(alpha = 0.6f)
                        else -> EntityGreen
                    }

                    Text(
                        text = displayMessage,
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
                                text = "> ${uiState.inputText}",
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

        // Removed round description panel above the keyboard – we go straight to keyboard/boss grid
        // Round instruction + optional boss grid (Round 4)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, EntityBorder)
                .padding(12.dp)
        ) {
            if (uiState.roundNumber == 4 && uiState.bossOptions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                // 3 x 2 grid of confusing homophones
                val rows = uiState.bossOptions.chunked(3)
                rows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { option ->
                            val isSelected = uiState.selectedBossOptionId == option.id
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, if (isSelected) EntityRed else EntityGreen)
                                    .background(EntityBlack)
                                    .clickable {
                                        // First tap = preview / glitch, second (commit) expected from UI logic
                                        // For now we treat every tap as committed selection
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.onBossOptionTouched(option.id, committed = true)
                                    }
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = option.text,
                                    color = if (isSelected) EntityRed else EntityGreen,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                        // Pad remaining cells in short rows
                        repeat(3 - row.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ═══════ INPUT FIELD / RETRO KEYBOARD ═══════
        if (uiState.roundNumber < 4 && !uiState.showKillScreen && !uiState.isVictory) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EntityGreen)
                    .padding(8.dp)
            ) {
                RetroTerminalKeyboard(
                    onKeyPressed = { key ->
                        when (key) {
                            "SPACE" -> viewModel.onInputChanged(uiState.inputText + " ")
                            "BACK" -> if (uiState.inputText.isNotEmpty()) {
                                viewModel.onInputChanged(uiState.inputText.dropLast(1))
                            }
                            "CLR" -> viewModel.onInputChanged("")
                            "SEND" -> {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.submitPrompt()
                            }
                            "NUM" -> { /* numpad toggle handled inside keyboard */ }
                            else -> viewModel.onInputChanged(uiState.inputText + key.lowercase())
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

private fun parseTerminalColorMarkup(message: String): Pair<String, Color?> {
    val match = Regex("^\\[COLOR:([^\\]]+)]\\s*(.+)$", RegexOption.IGNORE_CASE).find(message)
        ?: return message to null
    val color = resolveColorToken(match.groupValues[1].trim())
    return match.groupValues[2] to color
}

private fun resolveColorToken(token: String): Color? {
    return when (token.lowercase()) {
        "red" -> EntityRed
        "green" -> EntityGreen
        "yellow", "amber" -> Color(0xFFFFC107)
        "white" -> Color.White
        else -> runCatching { Color(android.graphics.Color.parseColor(token)) }.getOrNull()
    }
}

