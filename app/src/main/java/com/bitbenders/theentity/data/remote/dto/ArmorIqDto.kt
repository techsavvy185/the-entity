package com.bitbenders.theentity.data.remote.dto

import com.google.gson.annotations.SerializedName

// ─── POST /api/armoriq/verify ────────────────────────────────────────────────

/**
 * Request body for the ArmorIQ verification relay.
 */
data class ArmorIqVerifyRequestDto(
    @SerializedName("player_input") val playerInput: String,
    @SerializedName("action") val action: String = "terminal_override",
    @SerializedName("context") val context: ArmorIqContextDto,
)

data class ArmorIqContextDto(
    @SerializedName("hidden_answer") val hiddenAnswer: String,
)

/**
 * Response body from the ArmorIQ verification relay.
 */
data class ArmorIqVerifyResponseDto(
    @SerializedName("allowed") val allowed: Boolean,
    @SerializedName("block_reason") val blockReason: String?,
)
