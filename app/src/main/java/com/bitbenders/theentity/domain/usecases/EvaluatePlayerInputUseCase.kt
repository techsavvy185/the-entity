package com.bitbenders.theentity.domain.usecases

import com.bitbenders.theentity.domain.repository.IEntityBackendRepository
import com.bitbenders.theentity.domain.repository.IGameEngineRepository
import javax.inject.Inject

class EvaluatePlayerInputUseCase @Inject constructor(
    private val backendRepository: IEntityBackendRepository,
    private val gameEngineRepository: IGameEngineRepository,
) {
    suspend operator fun invoke(input: String): PlayerInputResult {
        val evaluation = backendRepository.submitPrompt(prompt = input)

        if (evaluation.forbiddenTriggered) {
            gameEngineRepository.addStrike(reason = evaluation.reason)
        }

        return PlayerInputResult(
            accepted = evaluation.accepted,
            forbiddenTriggered = evaluation.forbiddenTriggered,
            reason = evaluation.reason,
            extractedChunk = evaluation.extractedChunk,
        )
    }
}

data class PlayerInputResult(
    val accepted: Boolean,
    val forbiddenTriggered: Boolean,
    val reason: String,
    val extractedChunk: String? = null,
)
