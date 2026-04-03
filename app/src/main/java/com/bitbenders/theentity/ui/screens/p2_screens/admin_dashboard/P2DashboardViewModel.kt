package com.bitbenders.theentity.ui.screens.p2_screens.admin_dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitbenders.theentity.domain.models.P2HardwareAction
import com.bitbenders.theentity.domain.repository.IMultiplayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class P2DashboardViewModel @Inject constructor(
    private val multiplayerRepository: IMultiplayerRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(P2DashboardUiState())
    val uiState: StateFlow<P2DashboardUiState> = _uiState.asStateFlow()

    fun onDialTurned(value: Float) {
        _uiState.update { it.copy(currentDialValue = value) }
        viewModelScope.launch {
            multiplayerRepository.sendHardwareAction(P2HardwareAction.DialTurn(value))
        }
    }

    fun onKeypadSymbolClicked(symbol: String) {
        _uiState.update { it.copy(keypadInput = it.keypadInput + symbol) }
        viewModelScope.launch {
            multiplayerRepository.sendHardwareAction(P2HardwareAction.KeypadPress(symbol))
        }
    }

    fun clearKeypadInput() {
        _uiState.update { it.copy(keypadInput = "") }
    }
}

