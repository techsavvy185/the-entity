package com.bitbenders.theentity.ui.screens.p2_screens.admin_manual

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class P2ManualViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(P2ManualUiState())
    val uiState: StateFlow<P2ManualUiState> = _uiState.asStateFlow()
}

