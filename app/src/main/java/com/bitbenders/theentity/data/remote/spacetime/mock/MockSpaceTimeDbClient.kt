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
            // Keep update cadence low to reduce recomposition pressure.
            val normalizedDial = (((tick % 40L).toFloat() / 39f) * 2f - 1f)
                .let { kotlin.math.abs(it) }
                .coerceIn(0f, 1f)
            emit(P2HardwareAction.DialTurn(normalizedDial))

            // Emit keypad activity roughly every 2 seconds.
            if (tick % 8L == 0L) {
                val symbol = MOCK_SYMBOLS[(tick / 8L % MOCK_SYMBOLS.size).toInt()]
                emit(P2HardwareAction.KeypadPress(symbol))
            }

            tick++
            delay(250)
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

