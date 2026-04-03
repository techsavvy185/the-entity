package com.bitbenders.theentity.ui.screens.p1_screens.terminal

import com.bitbenders.theentity.domain.models.CipherChunk

data class P1TerminalUiState(
    val timerString: String = "05:00",
    val currentStrikes: Int = 0,
    val maxStrikes: Int = 3,
    val chatHistory: List<String> = emptyList(),
    val currentPersona: String = "BOOTING...",
    val cipherSlots: List<CipherChunk?> = listOf(null, null, null, null),
    val inputText: String = "",
    val isShaking: Boolean = false,
    val currentStaticIntensity: Float = 0f
)

