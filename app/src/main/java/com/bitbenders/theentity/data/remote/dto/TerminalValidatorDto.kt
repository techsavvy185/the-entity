package com.bitbenders.theentity.data.remote.dto

import com.google.gson.annotations.SerializedName

// ─── POST /api/gemini/terminal-validator ─────────────────────────────────────

/**
 * Request body for the Gemini terminal validator.
 */
data class TerminalValidatorRequestDto(
    @SerializedName("player_input") val playerInput: String,
    @SerializedName("hidden_answer") val hiddenAnswer: String,
)

/**
 * Response body from the Gemini terminal validator.
 * [reason] is intentionally non-leaky to prevent exploitation.
 */
data class TerminalValidatorResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("reason") val reason: String,
)
