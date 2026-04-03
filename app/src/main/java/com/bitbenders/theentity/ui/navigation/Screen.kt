package com.bitbenders.theentity.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object LobbyRoute : Screen

    @Serializable
    data class P1TerminalRoute(val roomCode: String) : Screen

    @Serializable
    data class P2DashboardRoute(val roomCode: String) : Screen

    @Serializable
    data object KillScreenRoute : Screen
}

