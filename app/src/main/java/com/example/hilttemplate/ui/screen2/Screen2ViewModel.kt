package com.example.hilttemplate.ui.screen2

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class Screen2ViewModel @Inject constructor(

): ViewModel() {
    private val _uiState = MutableStateFlow(Screen2UiState())
    val uiState = _uiState.asStateFlow()


}