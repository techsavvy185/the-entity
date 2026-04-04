package com.bitbenders.theentity.ui.screens.shared_screens.game_over

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VictoryScreenViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(VictoryScreenUiState())
    val uiState: StateFlow<VictoryScreenUiState> = _uiState.asStateFlow()

    private val staticChars = "░▒▓█▀▄▌▐│─┤┐└┴┬├┼╔╗╚╝║═╬▲▼◄►☺☻♦♣♠•◘○"

    init {
        viewModelScope.launch {
            // Phase 1: Static noise — screen fills with garbage
            _uiState.update { it.copy(phase = VictoryPhase.Static) }
            repeat(30) { tick ->
                val lines = (0..11).map { buildStaticLine(40) }
                _uiState.update { it.copy(staticLines = lines) }
                delay(if (tick < 10) 80L else 50L)
            }

            // Phase 2: Purge — simulated progress counter
            _uiState.update { it.copy(phase = VictoryPhase.Purge) }
            val steps = listOf(4, 11, 19, 27, 38, 44, 52, 61, 73, 80, 88, 94, 97, 100)
            for (pct in steps) {
                _uiState.update { it.copy(purgePercent = pct) }
                delay(if (pct < 50) 200L else 120L)
            }
            delay(400)

            // Phase 3: Reveal — final message typed
            _uiState.update { it.copy(phase = VictoryPhase.Reveal, staticLines = emptyList()) }
            val message = "CONNECTION SEVERED."
            for (i in 1..message.length) {
                _uiState.update { it.copy(finalText = message.take(i)) }
                delay(70)
            }
            delay(800)
            _uiState.update { it.copy(phase = VictoryPhase.Complete, showSubtext = true) }
        }
    }

    private fun buildStaticLine(length: Int): String {
        return (1..length).map { staticChars.random() }.joinToString("")
    }
}
