package com.bitbenders.theentity.ui.screens.p1_screens.terminal

import com.bitbenders.theentity.domain.models.CipherChunk

enum class RoundPhase {
    BOOT,
    ACTIVE,
    LOCKDOWN,
    STATIC,
    COMPLETE,
    FAILED,
}

data class BossOptionUi(
    val id: Int,
    val text: String,
    val isGlitched: Boolean = false,
)

data class P1TerminalUiState(
    val timerString: String = "05:00",
    val currentStrikes: Int = 0,
    val maxStrikes: Int = 3,
    val chatHistory: List<String> = emptyList(),
    val currentPersona: String = "BOOTING...",
    val cipherSlots: List<CipherChunk?> = listOf(null, null, null),
    val roundNumber: Int = 1,
    val roundInstruction: String = "Initializing extraction protocol...",
    val roundPhase: RoundPhase = RoundPhase.BOOT,
    val inputText: String = "",
    val isShaking: Boolean = false,
    val lockedGlyphs: List<String> = emptyList(),
    val bossOptions: List<BossOptionUi> = emptyList(),
    val selectedBossOptionId: Int? = null,
    val calibrationKey: String = "",
    val showKillScreen: Boolean = false,
    val isVictory: Boolean = false,
    val currentStaticIntensity: Float = 0f,
    // Typewriter effect fields
    val typewriterLine: String = "",  // Current line being typed out
    val showTypingCursor: Boolean = false,  // Blinking cursor visibility
    val isTypewriting: Boolean = false,  // Whether we're currently in typewriter mode
    // Input prompt cursor
    val showInputCursor: Boolean = true,  // Blinking cursor for input prompt
    // Wait for P2
    val isWaitingForOperator: Boolean = false,
    val roomId: String = "",
)
