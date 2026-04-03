package com.bitbenders.theentity.domain.usecases

import com.bitbenders.theentity.domain.repository.IMultiplayerRepository
import com.bitbenders.theentity.domain.repository.P1SyncState
import javax.inject.Inject

class SyncGameStateUseCase @Inject constructor(
    private val multiplayerRepository: IMultiplayerRepository,
) {
    suspend operator fun invoke(state: P1SyncState) {
        multiplayerRepository.sendP1StateToP2(state)
    }
}
