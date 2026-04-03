package com.bitbenders.theentity.ui.screens.p1_screens.boss_phase

data class HomophoneOption(
    val id: Int,
    val text: String,
    val isGlitching: Boolean = false,
    val isCorrect: Boolean = false
)

data class P1BossUiState(
    val options: List<HomophoneOption> = emptyList(),
    val isShaking: Boolean = false,
    val requiresCommit: Boolean = true
)

