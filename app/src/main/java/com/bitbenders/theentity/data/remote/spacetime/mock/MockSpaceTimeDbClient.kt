package com.bitbenders.theentity.data.remote.spacetime.mock

import com.bitbenders.theentity.domain.models.P2HardwareAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Mock implementation of SpaceTimeDbClient for frontend development.
 * Simulates P2 (Operator) hardware actions with realistic patterns.
 */
class MockSpaceTimeDbClient @Inject constructor() {

    fun observeHardwareActions(): Flow<P2HardwareAction> = flow {
        var tick = 0L
        while (true) {
            // Smooth oscillation in [0f..1f] to mimic a continuously turning frequency dial.
            val normalizedDial = (((tick % 240L).toFloat() / 239f) * 2f - 1f)
                .let { kotlin.math.abs(it) }
                .coerceIn(0f, 1f)
            emit(P2HardwareAction.DialTurn(normalizedDial))

            // Roughly every ~2 seconds at 60fps, emit a keypad event as well.
            if (tick % 120L == 0L) {
                val symbol = MOCK_SYMBOLS[(tick / 120L % MOCK_SYMBOLS.size).toInt()]
                emit(P2HardwareAction.KeypadPress(symbol))
            }

            tick++
            delay(16)
        }
    }

    companion object {
        private val MOCK_SYMBOLS = listOf(
            "A", "B", "C", "D",
            "1", "2", "3", "4",
            "⟲", "⟳", "◆", "⊙"
        )
    }
}

