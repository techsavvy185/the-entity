package com.bitbenders.theentity.domain.repository

import com.bitbenders.theentity.domain.models.P2HardwareAction
import kotlinx.coroutines.flow.Flow

interface IMultiplayerRepository {
    val incomingHardwareActions: Flow<P2HardwareAction>

    suspend fun sendP1StateToP2(state: P1SyncState)
    suspend fun sendHardwareAction(action: P2HardwareAction)
}

data class P1SyncState(
    val sessionId: String,
    val roundNumber: Int,
    val phase: String,
    val remainingTimeSeconds: Int,
    val strikeCount: Int,
    val anomalyActive: Boolean,
    val statusMessage: String,
)

