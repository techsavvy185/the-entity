package com.bitbenders.theentity.ui.screens.p1_screens.anomalies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitbenders.theentity.domain.usecases.ResolveAnomalyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class P1AnomalyViewModel @Inject constructor(
    private val resolveAnomalyUseCase: ResolveAnomalyUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        P1AnomalyUiState(
            lockedGlyphs = listOf("☉", "☊", "♇", "⚼"),
            isLockdownActive = true
        )
    )
    val uiState: StateFlow<P1AnomalyUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            resolveAnomalyUseCase.observeStaticIntensity().collect { intensity ->
                _uiState.update { it.copy(staticIntensity = intensity) }
            }
        }

        viewModelScope.launch {
            resolveAnomalyUseCase.observeKeypadSymbols().collect { symbol ->
                val currentGlyphs = _uiState.value.lockedGlyphs
                if (currentGlyphs.isNotEmpty() && currentGlyphs.first() == symbol) {
                    val newGlyphs = currentGlyphs.drop(1)
                    _uiState.update {
                        it.copy(
                            lockedGlyphs = newGlyphs,
                            isLockdownActive = newGlyphs.isNotEmpty()
                        )
                    }
                }
            }
        }
    }
}

