package com.bitbenders.theentity.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response from GET /health
 */
data class HealthResponseDto(
    @SerializedName("status") val status: String,
    @SerializedName("mock_mode") val mockMode: Boolean,
    @SerializedName("supported_routes") val supportedRoutes: List<String>,
)
