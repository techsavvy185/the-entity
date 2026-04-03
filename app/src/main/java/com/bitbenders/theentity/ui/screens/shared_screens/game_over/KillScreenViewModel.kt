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

    private val targetSequence = "SUBJECT INTEGRATED. PATTERN ACQUIRED."

    init {
        viewModelScope.launch {
            delay(1500) // The eerie initial pause
            for (i in 1..targetSequence.length) {
                _uiState.update { it.copy(typedText = targetSequence.take(i)) }
                delay(120) // Slow, methodical typing
            }
            _uiState.update { it.copy(isComplete = true) }
        }
    }
}

