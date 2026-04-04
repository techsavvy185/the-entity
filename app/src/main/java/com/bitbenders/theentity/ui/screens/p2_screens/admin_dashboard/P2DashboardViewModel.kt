package com.bitbenders.theentity.ui.screens.p2_screens.admin_dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitbenders.theentity.data.round1.RoundOneCatalog
import com.bitbenders.theentity.domain.models.P2HardwareAction
import com.bitbenders.theentity.domain.repository.IEntityBackendRepository
import com.bitbenders.theentity.domain.repository.IMultiplayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class P2DashboardViewModel @Inject constructor(
    private val backendRepository: IEntityBackendRepository,
    private val multiplayerRepository: IMultiplayerRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(P2DashboardUiState())
    val uiState: StateFlow<P2DashboardUiState> = _uiState.asStateFlow()

    init {
        seedPersonaEntries()
        startPersonaRefresh()
        startMissionTimer()
    }

    fun onDialTurned(value: Float) {
        val clamped = value.coerceIn(0f, 100f)
        _uiState.update {
            it.copy(
                currentDialValue = clamped,
                tuningValue = clamped.toInt()
            )
        }
        appendLog("> TUNING MATRIX SET TO ${clamped.toInt()}")
        viewModelScope.launch {
            multiplayerRepository.sendHardwareAction(P2HardwareAction.DialTurn(clamped))
        }
    }

    fun onTuningValueChanged(value: Int) {
        val clamped = value.coerceIn(0, 100)
        _uiState.update {
            it.copy(
                tuningValue = clamped,
                currentDialValue = clamped.toFloat()
            )
        }
        appendLog("> TUNING MATRIX SET TO $clamped")
    }

    fun onKeypadSymbolClicked(symbol: String) {
        _uiState.update {
            if (it.keypadSequence.size >= 8) it
            else it.copy(keypadSequence = it.keypadSequence + symbol)
        }
        appendLog("> ROOT KEY ACCEPTED: $symbol")
        viewModelScope.launch {
            multiplayerRepository.sendHardwareAction(P2HardwareAction.KeypadPress(symbol))
        }
    }

    fun clearKeypadInput() {
        _uiState.update { it.copy(keypadSequence = emptyList()) }
        appendLog("> ROOT BUFFER CLEARED")
    }

    fun submitKeypadSequence() {
        val sequence = uiState.value.keypadSequence
        if (sequence.isEmpty()) {
            appendLog("> ROOT OVERRIDE REJECTED: EMPTY SEQUENCE")
            return
        }
        appendLog("> ROOT OVERRIDE ACCEPTED: ${sequence.joinToString(" ")}")
        _uiState.update { it.copy(keypadSequence = emptyList()) }
    }

    fun onRedactionPressChanged(key: RedactionKey, pressed: Boolean) {
        _uiState.update {
            when (key) {
                RedactionKey.Persona -> it.copy(isPersonaRevealPressed = pressed)
                RedactionKey.Dissection -> it.copy(isDissectionRevealPressed = pressed)
                RedactionKey.DataIndex -> it.copy(isDataIndexRevealPressed = pressed)
            }
        }
    }

    fun addStrike() {
        _uiState.update {
            val next = (it.strikes + 1).coerceAtMost(it.maxStrikes)
            it.copy(strikes = next)
        }
        appendLog("> STRIKE REGISTERED")
    }

    private fun startMissionTimer() {
        viewModelScope.launch {
            while (true) {
                delay(1_000)
                _uiState.update { state ->
                    if (state.missionSecondsRemaining <= 0) state
                    else state.copy(missionSecondsRemaining = state.missionSecondsRemaining - 1)
                }
            }
        }
    }

    private fun appendLog(message: String) {
        _uiState.update { state ->
            val next = (state.actionLog + message).takeLast(14)
            state.copy(actionLog = next)
        }
    }

    private fun seedPersonaEntries() {
        val entries = RoundOneCatalog.personas.zip(RoundOneCatalog.wordPuzzles).map { (persona, puzzle) ->
            PersonaTabEntry(
                persona = persona,
                targetWord = puzzle.targetWord,
                forbiddenWords = puzzle.forbiddenWords,
            )
        }
        _uiState.update { it.copy(personaEntries = entries) }
    }

    private fun startPersonaRefresh() {
        viewModelScope.launch {
            while (true) {
                val selection = backendRepository.peekRoundOneSelection()
                _uiState.update { state ->
                    state.copy(
                        activePersonaEntry = selection?.let {
                            PersonaTabEntry(
                                persona = it.persona,
                                targetWord = it.targetWord,
                                forbiddenWords = it.forbiddenWords,
                            )
                        }
                    )
                }
                delay(1_000)
            }
        }
    }
}

enum class RedactionKey {
    Persona,
    Dissection,
    DataIndex
}
