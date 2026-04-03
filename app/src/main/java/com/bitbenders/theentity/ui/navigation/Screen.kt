package com.bitbenders.theentity.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object LobbyRoute : Screen

    @Serializable
    data object P1TerminalRoute : Screen

    @Serializable
    data object P2DashboardRoute : Screen

    @Serializable
    data object KillScreenRoute : Screen
}

