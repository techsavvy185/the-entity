package com.bitbenders.theentity.domain.usecases

import com.bitbenders.theentity.domain.repository.IEntityBackendRepository
import com.bitbenders.theentity.domain.repository.IGameEngineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Manages the overall game state and determines win/loss conditions.
 * Coordinates between backend, game engine, and multiplayer systems.
 */
class GameStateManagerUseCase @Inject constructor(
    private val gameEngineRepository: IGameEngineRepository,
    private val backendRepository: IEntityBackendRepository,
) {

    /**
     * Observes if the game is still active.
     * Returns false if time runs out or player reaches max strikes.
     */
    fun observeGameActive(): Flow<Boolean> {
        return combine(
            gameEngineRepository.remainingTimeSeconds,
            gameEngineRepository.currentStrikeState,
        ) { timeSeconds, strikeState ->
            timeSeconds > 0 && !strikeState.isGameOver
        }
    }

    /**
     * Observes game progress (ciphers collected out of 4).
     */
    fun observeGameProgress(): Flow<GameProgress> {
        // This will be populated by other use cases tracking cipher collection
        return combine(
            gameEngineRepository.remainingTimeSeconds,
            gameEngineRepository.currentStrikeState,
        ) { _, strikeState ->
            GameProgress(
                ciphersCollected = 0, // Will be tracked elsewhere
                totalCiphers = 4,
                strikeCount = strikeState.currentStrikes,
                maxStrikes = strikeState.maxStrikes,
            )
        }
    }

    /**
     * Determines the win/loss condition.
     */
    fun observeGameStatus(): Flow<GameStatus> {
        return combine(
            gameEngineRepository.remainingTimeSeconds,
            gameEngineRepository.currentStrikeState,
        ) { timeSeconds, strikeState ->
            when {
                strikeState.isGameOver -> GameStatus.LOST
                timeSeconds <= 0 -> GameStatus.LOST
                // Victory when all 4 ciphers collected - will enhance this
                else -> GameStatus.ACTIVE
            }
        }
    }
}

data class GameProgress(
    val ciphersCollected: Int,
    val totalCiphers: Int,
    val strikeCount: Int,
    val maxStrikes: Int,
) {
    val ciphersRemaining: Int get() = totalCiphers - ciphersCollected
    val progress: Float get() = ciphersCollected.toFloat() / totalCiphers
}

enum class GameStatus {
    ACTIVE,
    WON,
    LOST,
    PAUSED,
}

