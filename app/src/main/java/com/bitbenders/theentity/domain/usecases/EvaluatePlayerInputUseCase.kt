package com.bitbenders.theentity.domain.usecases

import com.bitbenders.theentity.domain.repository.IEntityBackendRepository
import com.bitbenders.theentity.domain.repository.IGameEngineRepository
import javax.inject.Inject

/**
 * Orchestrates the ArmorIQ → Gemini Terminal Validator pipeline
 * for Player 1's terminal input.
 *
 * 1. First checks ArmorIQ guardrails (forbidden words, logic contradictions).
 * 2. If ArmorIQ allows, proceeds to Gemini's strict kill-phrase validation.
 * 3. Adds a strike if ArmorIQ blocks (forbidden word violation).
 */
class EvaluatePlayerInputUseCase @Inject constructor(
    private val backendRepository: IEntityBackendRepository,
    private val gameEngineRepository: IGameEngineRepository,
) {
    suspend operator fun invoke(
        input: String,
        hiddenAnswer: String,
    ): PlayerInputResult {
        // Step 1 – ArmorIQ guardrail check
        val armorIqResult = backendRepository.verifyPlayerInput(
            playerInput = input,
            hiddenAnswer = hiddenAnswer,
        )

        if (!armorIqResult.allowed) {
            val reason = armorIqResult.blockReason ?: "Input blocked by ArmorIQ"
            gameEngineRepository.addStrike(reason = reason)

            return PlayerInputResult(
                accepted = false,
                forbiddenTriggered = true,
                reason = reason,
                extractedChunk = null,
            )
        }

        // Step 2 – Gemini terminal validation (kill-phrase match)
        val terminalResult = backendRepository.validateTerminal(
            playerInput = input,
            hiddenAnswer = hiddenAnswer,
        )

        return PlayerInputResult(
            accepted = terminalResult.success,
            forbiddenTriggered = false,
            reason = terminalResult.reason,
            extractedChunk = if (terminalResult.success) input else null,
        )
    }
}

data class PlayerInputResult(
    val accepted: Boolean,
    val forbiddenTriggered: Boolean,
    val reason: String,
    val extractedChunk: String? = null,
)
