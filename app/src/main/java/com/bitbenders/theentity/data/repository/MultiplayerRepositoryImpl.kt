package com.bitbenders.theentity.data.repository

import com.bitbenders.theentity.data.remote.dto.MultiplayerEventDto
import com.bitbenders.theentity.data.remote.spacetime.SpaceTimeDbClient
import com.bitbenders.theentity.domain.models.P2HardwareAction
import com.bitbenders.theentity.domain.repository.IMultiplayerRepository
import com.bitbenders.theentity.domain.repository.P1SyncState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MultiplayerRepositoryImpl @Inject constructor(
    private val spaceTimeDbClient: SpaceTimeDbClient,
) : IMultiplayerRepository {
    override val incomingHardwareActions: Flow<P2HardwareAction> =
        spaceTimeDbClient.observeHardwareActions()

    private val _outgoingStateEvents = MutableSharedFlow<MultiplayerEventDto.P1StateBroadcastDto>(
        replay = 0,
        extraBufferCapacity = 32,
    )
    val outgoingStateEvents: Flow<MultiplayerEventDto.P1StateBroadcastDto> = _outgoingStateEvents.asSharedFlow()

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
}
