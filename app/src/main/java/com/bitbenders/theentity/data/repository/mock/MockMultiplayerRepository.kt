package com.bitbenders.theentity.data.repository.mock

import com.bitbenders.theentity.data.remote.dto.MultiplayerEventDto
import com.bitbenders.theentity.data.remote.spacetime.mock.MockSpaceTimeDbClient
import com.bitbenders.theentity.domain.models.P2HardwareAction
import com.bitbenders.theentity.domain.repository.IMultiplayerRepository
import com.bitbenders.theentity.domain.repository.P1SyncState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of IMultiplayerRepository for frontend development.
 * Simulates P2 hardware actions and state synchronization.
 */
@Singleton
class MockMultiplayerRepository @Inject constructor(
    private val mockSpaceTimeDbClient: MockSpaceTimeDbClient,
) : IMultiplayerRepository {

    // ...existing code...
    override val incomingHardwareActions: Flow<P2HardwareAction> =
        mockSpaceTimeDbClient.observeHardwareActions()

    private val _outgoingStateEvents = MutableSharedFlow<MultiplayerEventDto.P1StateBroadcastDto>(
        replay = 0,
        extraBufferCapacity = 32,
    )

    // ...existing code...
    override suspend fun sendP1StateToP2(state: P1SyncState) {
        _outgoingStateEvents.emit(
            MultiplayerEventDto.P1StateBroadcastDto(
                sessionId = state.sessionId,
                roundNumber = state.roundNumber,
                phase = state.phase,
                remainingTimeSeconds = state.remainingTimeSeconds,
                strikeCount = state.strikeCount,
                anomalyActive = state.anomalyActive,
                statusMessage = state.statusMessage,
                timestampMs = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun sendHardwareAction(action: P2HardwareAction) {
        // Mock: Just acknowledge the hardware action was sent
        // In real implementation, this would be serialized and transmitted to P2
    }
}

