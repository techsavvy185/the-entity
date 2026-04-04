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
class KillScreenViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(KillScreenUiState())
    val uiState: StateFlow<KillScreenUiState> = _uiState.asStateFlow()

    private val targetSequence = "Pattern Absorbed.\nThank you for helping us improve."

    init {
        viewModelScope.launch {
            delay(2000) // Eerie initial pause — black screen
            for (i in 1..targetSequence.length) {
                _uiState.update { it.copy(typedText = targetSequence.take(i)) }
                val char = targetSequence[i - 1]
                delay(
                    when {
                        char == '.' -> 600L   // Long pause after periods
                        char == '\n' -> 800L  // Dramatic pause at line break
                        else -> 90L           // Steady typing
                    }
                )
            }
            _uiState.update { it.copy(isComplete = true) }
        }
    }
}
