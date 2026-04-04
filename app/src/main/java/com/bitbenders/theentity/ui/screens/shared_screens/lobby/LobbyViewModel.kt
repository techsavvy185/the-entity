package com.bitbenders.theentity.ui.screens.shared_screens.lobby

import androidx.lifecycle.ViewModel
import com.bitbenders.theentity.domain.repository.IEntityBackendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LobbyViewModel @Inject constructor(
    private val backendRepository: IEntityBackendRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LobbyUiState())
    val uiState: StateFlow<LobbyUiState> = _uiState.asStateFlow()

    fun onCreateRoomClicked() {
        _uiState.update { it.copy(isConnecting = true, connectionError = null) }

        runCatching {
            runBlocking { backendRepository.initiateRoom(seedLabel = "The Entity") }
        }.onSuccess { session ->
            latestCreatedRoomCode = session.roomId
            _uiState.update {
                it.copy(
                    isConnecting = false,
                    mode = LobbyMode.CREATE,
                    roomCode = session.roomId,
                    joinCodeInput = "",
                    connectionError = null,
                )
            }
        }.onFailure { error ->
            _uiState.update {
                it.copy(
                    isConnecting = false,
                    mode = LobbyMode.CREATE,
                    connectionError = error.message ?: "Failed to create room",
                )
            }
        }
    }

    fun onJoinModeClicked() {
        _uiState.update {
            it.copy(
                mode = LobbyMode.JOIN,
                connectionError = null,
            )
        }
    }

    fun onJoinCodeChanged(input: String) {
        _uiState.update {
            it.copy(
                joinCodeInput = input
                    .trim()
                    .uppercase(Locale.US)
                    .filter { ch -> ch.isLetterOrDigit() }
                    .take(6),
                connectionError = null,
            )
        }
    }

    fun canJoinCurrentCode(): Boolean {
        val entered = _uiState.value.joinCodeInput
        return entered.length == ROOM_CODE_LENGTH
    }

    fun resolveJoinCodeOrError(): String? {
        val entered = _uiState.value.joinCodeInput.trim().uppercase(Locale.US)
        if (entered.length != ROOM_CODE_LENGTH) {
            _uiState.update { it.copy(connectionError = "Room code must be 6 characters") }
            return null
        }

        _uiState.update { it.copy(isConnecting = true, connectionError = null) }
        val result = runCatching {
            runBlocking { backendRepository.joinRoom(entered) }
        }

        val joined = result.getOrNull()

        if (joined == null) {
            val reason = result.exceptionOrNull()?.message?.takeIf { it.isNotBlank() }
                ?: "Room not found. Check code and retry"
            _uiState.update {
                it.copy(
                    isConnecting = false,
                    connectionError = reason,
                )
            }
            return null
        }

        _uiState.update { it.copy(isConnecting = false, connectionError = null) }
        return joined.roomId
    }

    companion object {
        private const val ROOM_CODE_LENGTH = 6

        @Volatile
        private var latestCreatedRoomCode: String = ""
    }
}

