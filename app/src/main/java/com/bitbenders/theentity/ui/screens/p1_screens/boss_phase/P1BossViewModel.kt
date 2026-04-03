package com.bitbenders.theentity.ui.screens.p1_screens.boss_phase

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
class P1BossViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(P1BossUiState())
    val uiState: StateFlow<P1BossUiState> = _uiState.asStateFlow()

    init {
        val initialWords = listOf("WAIT", "WEIGHT", "RIGHT", "WRITE", "HOLE", "WHOLE")
        _uiState.update {
            it.copy(
                options = initialWords.mapIndexed { index, word ->
                    HomophoneOption(id = index, text = word, isCorrect = word == "WRITE")
                }
            )
        }
        startGlitchCycle()
    }

    private fun startGlitchCycle() {
        viewModelScope.launch {
            while (true) {
                delay((1000..3000).random().toLong())
                _uiState.update { state ->
                    val options = state.options.toMutableList()
                    val target = options.indices.random()
                    options[target] = options[target].copy(isGlitching = !options[target].isGlitching)
                    state.copy(options = options)
                }
            }
        }
    }

    fun onOptionTouched(id: Int, committed: Boolean) {
        if (!committed) {
            // Shuffle violently on hesitant touch
            viewModelScope.launch {
                _uiState.update { state ->
                    state.copy(options = state.options.shuffled(), isShaking = true)
                }
                delay(200)
                _uiState.update { it.copy(isShaking = false) }
            }
        } else {
            // Evaluated committed choice
            val option = _uiState.value.options.find { it.id == id }
            if (option?.isCorrect == true) {
                // Success handling
            } else {
                viewModelScope.launch {
                    _uiState.update { it.copy(isShaking = true) }
                    delay(500)
                    _uiState.update { it.copy(isShaking = false) }
                }
            }
        }
    }
}

