package com.bitbenders.theentity.domain.repository

import com.bitbenders.theentity.domain.models.GameStrike
import kotlinx.coroutines.flow.Flow

interface IGameEngineRepository {
    val remainingTimeSeconds: Flow<Int>
    val currentStrikeState: Flow<GameStrike>

    suspend fun addStrike(reason: String)
    suspend fun endGame(reason: String)
}

