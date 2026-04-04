package com.bitbenders.theentity.ui.screens.shared_screens.game_over

data class VictoryScreenUiState(
    val phase: VictoryPhase = VictoryPhase.Static,
    val staticLines: List<String> = emptyList(),
    val purgePercent: Int = 0,
    val finalText: String = "",
    val showSubtext: Boolean = false
)

enum class VictoryPhase {
    Static,
    Purge,
    Reveal,
    Complete
}
