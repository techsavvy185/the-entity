package com.bitbenders.theentity.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.bitbenders.theentity.ui.screens.shared_screens.lobby.LobbyScreen
import com.bitbenders.theentity.ui.screens.shared_screens.lobby.LobbyViewModel
import com.bitbenders.theentity.ui.screens.p1_screens.terminal.P1TerminalScreen
import com.bitbenders.theentity.ui.screens.p1_screens.terminal.P1TerminalViewModel
import com.bitbenders.theentity.ui.screens.p2_screens.admin_dashboard.P2DashboardScreen
import com.bitbenders.theentity.ui.screens.p2_screens.admin_dashboard.P2DashboardViewModel
import com.bitbenders.theentity.ui.screens.shared_screens.game_over.KillScreen
import com.bitbenders.theentity.ui.screens.shared_screens.game_over.KillScreenViewModel
import com.bitbenders.theentity.ui.screens.shared_screens.game_over.VictoryScreen
import com.bitbenders.theentity.ui.screens.shared_screens.game_over.VictoryScreenViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.activity.compose.BackHandler
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.entryProvider

@Composable
fun EntityNavGraph() {
    val backstack = remember { mutableStateListOf<Screen>(Screen.LobbyRoute) }

    BackHandler(enabled = backstack.size > 1) {
        backstack.removeLastOrNull()
    }

    NavDisplay(
        backStack = backstack,
        onBack = {
            backstack.removeLastOrNull()
        },
        entryProvider = entryProvider {
            entry<Screen.LobbyRoute> {
                val viewModel: LobbyViewModel = hiltViewModel()
                LobbyScreen(
                    viewModel = viewModel,
                    onJoinAsTrapped = { roomCode -> backstack.add(Screen.P1TerminalRoute(roomCode)) },
                    onJoinAsOperator = { roomCode -> backstack.add(Screen.P2DashboardRoute(roomCode)) }
                )
            }

            entry<Screen.P1TerminalRoute> { route ->
                val viewModel: P1TerminalViewModel = hiltViewModel()
                androidx.compose.runtime.LaunchedEffect(route.roomCode) {
                    viewModel.setRoomCode(route.roomCode)
                }
                P1TerminalScreen(
                    viewModel = viewModel,
                    onNavigateToVictory = { backstack.add(Screen.VictoryScreenRoute) },
                    onNavigateToDefeat = { backstack.add(Screen.KillScreenRoute) },
                )
            }

            entry<Screen.P2DashboardRoute> { route ->
                val viewModel: P2DashboardViewModel = hiltViewModel()
                androidx.compose.runtime.LaunchedEffect(route.roomCode) {
                    viewModel.setRoomCode(route.roomCode)
                }
                P2DashboardScreen(viewModel = viewModel)
            }

            entry<Screen.KillScreenRoute> {
                val viewModel: KillScreenViewModel = hiltViewModel()
                KillScreen(
                    viewModel = viewModel,
                    onTimeout = { 
                        backstack.clear()
                        backstack.add(Screen.LobbyRoute) 
                    }
                )
            }

            entry<Screen.VictoryScreenRoute> {
                val viewModel: VictoryScreenViewModel = hiltViewModel()
                VictoryScreen(
                    viewModel = viewModel,
                    onTimeout = { 
                        backstack.clear()
                        backstack.add(Screen.LobbyRoute) 
                    }
                )
            }
        }
    )
}
