package com.bitbenders.theentity.data.remote.dto

sealed class MultiplayerEventDto {
    data class DialTurnEventDto(
        val value: Float,
        val timestampMs: Long,
    ) : MultiplayerEventDto()

    data class KeypadPressEventDto(
        val symbol: String,
        val timestampMs: Long,
    ) : MultiplayerEventDto()

    data class P1StateBroadcastDto(
        val sessionId: String,
        val roundNumber: Int,
        val phase: String,
        val remainingTimeSeconds: Int,
        val strikeCount: Int,
        val anomalyActive: Boolean,
        val statusMessage: String,
        val timestampMs: Long,
    ) : MultiplayerEventDto()
}

