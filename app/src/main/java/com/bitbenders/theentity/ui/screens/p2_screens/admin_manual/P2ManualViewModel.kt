package com.bitbenders.theentity.ui.screens.p2_screens.admin_manual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitbenders.theentity.domain.repository.IEntityBackendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class P2ManualViewModel @Inject constructor(
    private val backendRepository: IEntityBackendRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(P2ManualUiState())
    val uiState: StateFlow<P2ManualUiState> = _uiState.asStateFlow()

    init {
        refreshFromActiveRoom()
    }

    fun refreshFromActiveRoom() {
        viewModelScope.launch {
            val selection = backendRepository.peekRoundOneSelection() ?: return@launch
            val activeRule = RuleEntry(
                title = "[ACTIVE ROOM] ${selection.persona.uppercase()}",
                details = "Target: ${selection.targetWord} | Forbidden: ${selection.forbiddenWords.joinToString(", ")}",
            )

            _uiState.update { state ->
                state.copy(
                    personaOverrides = listOf(activeRule) + state.personaOverrides.filterNot {
                        it.title.equals(selection.persona, ignoreCase = true) ||
                            it.title.equals("[ACTIVE ROOM] ${selection.persona}", ignoreCase = true) ||
                            it.title.startsWith("[ACTIVE ROOM]", ignoreCase = true)
                    },
                )
            }
        }
    }
}

