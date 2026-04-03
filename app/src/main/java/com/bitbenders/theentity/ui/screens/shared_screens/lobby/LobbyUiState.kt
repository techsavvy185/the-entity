package com.bitbenders.theentity.ui.screens.shared_screens.lobby

enum class LobbyMode {
    NONE,
    CREATE,
    JOIN,
}

data class LobbyUiState(
    val isConnecting: Boolean = false,
    val connectionError: String? = null,
    val mode: LobbyMode = LobbyMode.NONE,
    val roomCode: String = "",
    val joinCodeInput: String = "",
)

