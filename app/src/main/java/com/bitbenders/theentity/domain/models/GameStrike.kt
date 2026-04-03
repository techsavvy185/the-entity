package com.bitbenders.theentity.domain.models

/**
 * Encapsulates strike policy and current strike progression.
 */
data class GameStrike(
    val currentStrikes: Int,
    val maxStrikes: Int,
    val timePenaltySeconds: Int,
) {
    val isGameOver: Boolean
        get() = currentStrikes >= maxStrikes
}

