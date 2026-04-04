package com.bitbenders.theentity.data.remote.spacetime

import com.bitbenders.theentity.domain.models.P2HardwareAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SpaceTimeDbClient @Inject constructor() {
    fun observeHardwareActions(): Flow<P2HardwareAction> = flow {
        var tick = 0L
        while (true) {
            // Use a low-frequency signal to avoid flooding Compose with state updates.
            val normalizedDial = (((tick % 40L).toFloat() / 39f) * 2f - 1f)
                .let { kotlin.math.abs(it) }
                .coerceIn(0f, 1f)

            emit(P2HardwareAction.DialTurn(normalizedDial))

            // Emit a keypad event roughly every 2 seconds.
            if (tick % 8L == 0L) {
                val symbol = MOCK_SYMBOLS[(tick / 8L % MOCK_SYMBOLS.size).toInt()]
                emit(P2HardwareAction.KeypadPress(symbol))
            }

            tick++
            delay(250)
        }
    }

    companion object {
        private val MOCK_SYMBOLS = listOf("☉", "☊", "♇", "⚼", "⌬", "⋔", "⟐", "⟁", "✶")
    }
}
