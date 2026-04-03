package com.bitbenders.theentity.domain.repository

import com.bitbenders.theentity.domain.models.PersonaConfig

interface IEntityBackendRepository {
    suspend fun startRound(roundNumber: Int): BackendRoundState
    suspend fun submitPrompt(prompt: String): PromptEvaluation
    suspend fun fetchNextPersona(): PersonaConfig
}

data class BackendRoundState(
    val roundNumber: Int,
    val phaseLabel: String,
    val instruction: String,
    val seed: String,
)

data class PromptEvaluation(
    val accepted: Boolean,
    val forbiddenTriggered: Boolean,
    val reason: String,
    val extractedChunk: String? = null,
)

