package com.bitbenders.theentity.ui.screens.shared_screens.lobby

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random
import javax.inject.Inject

@HiltViewModel
class LobbyViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(LobbyUiState())
    val uiState: StateFlow<LobbyUiState> = _uiState.asStateFlow()

    fun onCreateRoomClicked() {
        val code = generateRoomCode()
        latestCreatedRoomCode = code
        _uiState.update {
            it.copy(
                mode = LobbyMode.CREATE,
                roomCode = code,
                joinCodeInput = "",
                connectionError = null,
            )
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
                joinCodeInput = input.uppercase().take(6),
                connectionError = null,
            )
        }
    }

    fun canJoinCurrentCode(): Boolean {
        val entered = _uiState.value.joinCodeInput
        return entered.length == ROOM_CODE_LENGTH
    }

    fun resolveJoinCodeOrError(): String? {
        val entered = _uiState.value.joinCodeInput
        if (entered.length != ROOM_CODE_LENGTH) {
            _uiState.update { it.copy(connectionError = "Room code must be 6 characters") }
            return null
        }

        val activeCode = latestCreatedRoomCode
        if (activeCode.isNotEmpty() && entered != activeCode) {
            _uiState.update { it.copy(connectionError = "Room not found. Check code and retry") }
            return null
        }

        return entered
    }

    private fun generateRoomCode(): String {
        return buildString {
            repeat(ROOM_CODE_LENGTH) {
                append(ROOM_ALPHABET[Random.nextInt(ROOM_ALPHABET.length)])
            }
        }
    }

    companion object {
        private const val ROOM_CODE_LENGTH = 6
        private const val ROOM_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

        @Volatile
        private var latestCreatedRoomCode: String = ""
    }
}

