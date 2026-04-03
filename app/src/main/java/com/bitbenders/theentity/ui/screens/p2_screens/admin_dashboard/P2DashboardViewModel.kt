package com.bitbenders.theentity.ui.screens.p2_screens.admin_dashboard

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class P2DashboardViewModel @Inject constructor(
    // In actual implementation, we inject IMultiplayerRepository to send hardware signals to backend
) : ViewModel() {

    private val _uiState = MutableStateFlow(P2DashboardUiState())
    val uiState: StateFlow<P2DashboardUiState> = _uiState.asStateFlow()

    fun onDialTurned(value: Float) {
        _uiState.update { it.copy(currentDialValue = value) }
        // repo.emitDialTurn(value)
    }

    fun onKeypadSymbolClicked(symbol: String) {
        _uiState.update { it.copy(keypadInput = it.keypadInput + symbol) }
        // repo.emitKeypadPress(symbol)
    }

    fun clearKeypadInput() {
        _uiState.update { it.copy(keypadInput = "") }
    }
}

