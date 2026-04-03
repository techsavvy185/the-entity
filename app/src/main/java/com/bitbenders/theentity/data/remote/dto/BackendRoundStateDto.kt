package com.bitbenders.theentity.data.remote.dto

data class BackendRoundStateDto(
    val roundNumber: Int,
    val phaseLabel: String,
    val instruction: String,
    val seed: String,
)

data class PromptEvaluationDto(
    val accepted: Boolean,
    val forbiddenTriggered: Boolean,
    val reason: String,
    val extractedChunk: String? = null,
)

data class PersonaConfigDto(
    val targetWord: String,
    val forbiddenWords: List<String>,
)

