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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onNavigateToVictory: () -> Unit = {},
    onNavigateToDefeat: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current
    val listState = rememberLazyListState()

    // Navigate to victory/defeat screens
    LaunchedEffect(uiState.isVictory) {
        if (uiState.isVictory) onNavigateToVictory()
    }
    LaunchedEffect(uiState.showKillScreen) {
        if (uiState.showKillScreen) onNavigateToDefeat()
    }

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

    // ═══════ WAITING FOR OPERATOR ═══════
    if (uiState.isWaitingForOperator) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(EntityBlack)
                .staticNoise(0.3f)
                .crtTerminalEffect()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ROOM CODE",
                color = EntityGreen.copy(alpha = 0.6f),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.roomId,
                color = EntityRed,
                style = MaterialTheme.typography.displayLarge.copy(
                    letterSpacing = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(32.dp))
            var dots by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
            LaunchedEffect(Unit) {
                while (true) {
                    dots = when (dots.length) {
                        0 -> "."
                        1 -> ".."
                        2 -> "..."
                        else -> ""
                    }
                    kotlinx.coroutines.delay(500)
                }
            }
            Text(
                text = "WAITING FOR OPERATOR$dots",
                color = EntityGreen,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        return
    }

    // ═══════ ROUND 3: FULL-SCREEN CALIBRATION MATRIX ═══════
    if (uiState.roundNumber == 3 && uiState.bossOptions.isNotEmpty()) {
        CalibrationMatrixScreen(
            uiState = uiState,
            onOptionTouched = { optionId ->
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.onBossOptionTouched(optionId, committed = true)
            },
            modifier = modifier
        )
        return
    }

    // ═══════ ROUNDS 1–2: NORMAL TERMINAL ═══════
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
            Text(
                text = "T-${uiState.timerString}",
                color = EntityRed,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.weight(1f))
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

        // Cipher slots row
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

        // ═══════ INPUT FIELD / RETRO KEYBOARD ═══════
        if (uiState.roundNumber < 3 && !uiState.showKillScreen && !uiState.isVictory) {
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

// ═══════ FULL-SCREEN CALIBRATION MATRIX ═══════

@Composable
private fun CalibrationMatrixScreen(
    uiState: P1TerminalUiState,
    onOptionTouched: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EntityBlack)
            .screenShake(uiState.isShaking)
            .staticNoise(uiState.currentStaticIntensity)
            .crtTerminalEffect()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Header: Timer + Strikes ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, EntityBorder)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "T-${uiState.timerString}",
                color = EntityRed,
                style = MaterialTheme.typography.headlineMedium
            )
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

        Spacer(modifier = Modifier.height(24.dp))

        // ── Title ──
        Text(
            text = "HOSTILE LEXICAL\nCALIBRATION",
            color = EntityGreen,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp,
                letterSpacing = 4.sp,
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Calibration Key ──
        Text(
            text = "KEY: ${uiState.calibrationKey}",
            color = EntityRed,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp,
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "SELECT THE CORRECT WORD",
            color = EntityGreen.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyMedium.copy(
                letterSpacing = 2.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        // ── 3×2 Homophone Grid ──
        val rows = uiState.bossOptions.chunked(3)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { option ->
                    val isSelected = uiState.selectedBossOptionId == option.id
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.6f)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) EntityRed else EntityGreen
                            )
                            .background(
                                if (isSelected) EntityRed.copy(alpha = 0.1f) else Color.Transparent
                            )
                            .clickable { onOptionTouched(option.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option.text,
                            color = if (isSelected) EntityRed else EntityGreen,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                // Pad remaining cells in short rows
                repeat(3 - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // ── Cipher slots at bottom ──
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
