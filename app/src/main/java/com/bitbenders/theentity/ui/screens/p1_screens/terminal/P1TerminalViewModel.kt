package com.bitbenders.theentity.ui.screens.p1_screens.terminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitbenders.theentity.domain.repository.IEntityBackendRepository
import com.bitbenders.theentity.domain.repository.IGameEngineRepository
import com.bitbenders.theentity.domain.usecases.EvaluatePlayerInputUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class P1TerminalViewModel @Inject constructor(
    private val evaluatePlayerInputUseCase: EvaluatePlayerInputUseCase,
    private val gameEngineRepository: IGameEngineRepository,
    private val entityBackendRepository: IEntityBackendRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(P1TerminalUiState())
    val uiState: StateFlow<P1TerminalUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            gameEngineRepository.remainingTimeSeconds.collect { seconds ->
                val mins = seconds / 60
                val secs = seconds % 60
                _uiState.update {
                    it.copy(timerString = String.format("%02d:%02d", mins, secs))
                }
            }
        }

        viewModelScope.launch {
            gameEngineRepository.currentStrikeState.collect { strikeState ->
                val prevStrikes = _uiState.value.currentStrikes
                _uiState.update {
                    it.copy(
                        currentStrikes = strikeState.currentStrikes,
                        maxStrikes = strikeState.maxStrikes
                    )
                }
                if (strikeState.currentStrikes > prevStrikes) {
                    triggerShake()
                }
            }
        }

        viewModelScope.launch {
            try {
                val roundState = entityBackendRepository.startRound(1)
                val persona = entityBackendRepository.fetchNextPersona()
                _uiState.update {
                    it.copy(
                        currentPersona = "PERSONA IDENTIFIED: ${roundState.phaseLabel}",
                        chatHistory = listOf("AI BOOT SEQUENCE INITIATED...", "AWAITING INPUT.")
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(currentPersona = "ERROR: OFFLINE") }
            }
        }
    }

    fun onInputChanged(newText: String) {
        _uiState.update { it.copy(inputText = newText) }
    }

    fun submitPrompt() {
        val prompt = _uiState.value.inputText
        if (prompt.isBlank()) return

        _uiState.update { state ->
            state.copy(
                inputText = "",
                chatHistory = state.chatHistory + "> $prompt"
            )
        }

        viewModelScope.launch {
            try {
                val result = evaluatePlayerInputUseCase(prompt)
                _uiState.update { state ->
                    val history = state.chatHistory.toMutableList()
                    if (result.forbiddenTriggered) {
                        history.add("ERR: FORBIDDEN LEXICON DETECTED.")
                        history.add("STRIKE APPLIED.")
                    } else {
                        history.add(result.reason)
                    }

                    if (result.extractedChunk != null) {
                        history.add("EXTRACTED: ${result.extractedChunk}")
                        // Mock update chunk slots (would need actual parsing/id placing)
                    }

                    state.copy(chatHistory = history)
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(chatHistory = state.chatHistory + "ERR: CONNECTION LOST.")
                }
            }
        }
    }

    private fun triggerShake() {
        viewModelScope.launch {
            _uiState.update { it.copy(isShaking = true) }
            delay(500)
            _uiState.update { it.copy(isShaking = false) }
        }
    }
}

