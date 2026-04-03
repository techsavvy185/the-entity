package com.bitbenders.theentity.data.repository

import com.bitbenders.theentity.domain.models.GameStrike
import com.bitbenders.theentity.domain.repository.IGameEngineRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class GameEngineRepositoryImpl @Inject constructor() : IGameEngineRepository {

    private val engineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val mutationMutex = Mutex()

    private val _remainingTimeSeconds = MutableStateFlow(INITIAL_TIME_SECONDS)
    override val remainingTimeSeconds: Flow<Int> = _remainingTimeSeconds.asStateFlow()

    private val _currentStrikeState = MutableStateFlow(
        GameStrike(
            currentStrikes = 0,
            maxStrikes = MAX_STRIKES,
            timePenaltySeconds = STRIKE_PENALTY_SECONDS,
        ),
    )
    override val currentStrikeState: Flow<GameStrike> = _currentStrikeState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startTimerIfNeeded()
    }

    override suspend fun addStrike(reason: String) {
        mutationMutex.withLock {
            val current = _currentStrikeState.value
            val nextStrikes = (current.currentStrikes + 1).coerceAtMost(current.maxStrikes)
            _currentStrikeState.value = current.copy(currentStrikes = nextStrikes)

            val penalizedTime = (_remainingTimeSeconds.value - current.timePenaltySeconds).coerceAtLeast(0)
            _remainingTimeSeconds.value = penalizedTime

            if (nextStrikes >= current.maxStrikes || penalizedTime <= 0) {
                stopTimer()
            }
        }
    }

    override suspend fun endGame(reason: String) {
        mutationMutex.withLock {
            _remainingTimeSeconds.value = 0
            stopTimer()
        }
    }

    private fun startTimerIfNeeded() {
        if (timerJob?.isActive == true) return

        timerJob = engineScope.launch {
            while (_remainingTimeSeconds.value > 0 && !_currentStrikeState.value.isGameOver) {
                delay(1_000)
                mutationMutex.withLock {
                    if (_remainingTimeSeconds.value > 0 && !_currentStrikeState.value.isGameOver) {
                        _remainingTimeSeconds.value -= 1
                    }
                    if (_remainingTimeSeconds.value <= 0) {
                        stopTimer()
                    }
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    companion object {
        private const val INITIAL_TIME_SECONDS = 300
        private const val STRIKE_PENALTY_SECONDS = 30
        private const val MAX_STRIKES = 3
    }
}
