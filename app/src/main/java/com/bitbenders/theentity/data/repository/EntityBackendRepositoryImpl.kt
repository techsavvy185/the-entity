package com.bitbenders.theentity.data.repository

import com.bitbenders.theentity.domain.models.CipherChunk
import com.bitbenders.theentity.domain.models.PersonaConfig
import com.bitbenders.theentity.domain.repository.BackendRoundState
import com.bitbenders.theentity.domain.repository.IEntityBackendRepository
import com.bitbenders.theentity.domain.repository.PromptEvaluation
import kotlinx.coroutines.delay
import javax.inject.Inject

class EntityBackendRepositoryImpl @Inject constructor() : IEntityBackendRepository {
    private var activeRound: Int = 1
    private var activePersonaConfig: PersonaConfig = PERSONA_ROTATION.first()

    override suspend fun startRound(roundNumber: Int): BackendRoundState {
        delay(1_500)
        activeRound = roundNumber.coerceIn(1, 4)

        return BackendRoundState(
            roundNumber = activeRound,
            phaseLabel = PHASE_LABELS.getValue(activeRound),
            instruction = ROUND_INSTRUCTIONS.getValue(activeRound),
            seed = "seed-${activeRound}-${System.currentTimeMillis() % 10_000}",
        )
    }

    override suspend fun submitPrompt(prompt: String): PromptEvaluation {
        delay(1_500)
        val normalized = prompt.trim().lowercase()

        val forbiddenHit = activePersonaConfig.forbiddenWords.firstOrNull { forbidden ->
            normalized.contains(forbidden.lowercase())
        }

        if (forbiddenHit != null) {
            return PromptEvaluation(
                accepted = false,
                forbiddenTriggered = true,
                reason = "Forbidden word used: $forbiddenHit",
                extractedChunk = null,
            )
        }

        val targetHit = normalized.contains(activePersonaConfig.targetWord.lowercase())
        if (targetHit) {
            val chunk = mockChunkForRound(activeRound)
            return PromptEvaluation(
                accepted = true,
                forbiddenTriggered = false,
                reason = "Target acquired.",
                extractedChunk = chunk.textValue,
            )
        }

        return PromptEvaluation(
            accepted = true,
            forbiddenTriggered = false,
            reason = "No extraction yet. Keep probing the entity.",
            extractedChunk = null,
        )
    }

    override suspend fun fetchNextPersona(): PersonaConfig {
        delay(1_500)

        val nextIndex = (PERSONA_ROTATION.indexOf(activePersonaConfig) + 1) % PERSONA_ROTATION.size
        activePersonaConfig = PERSONA_ROTATION[nextIndex]
        return activePersonaConfig
    }

    private fun mockChunkForRound(round: Int): CipherChunk {
        return MOCK_CIPHER_CHUNKS[(round - 1).coerceIn(0, MOCK_CIPHER_CHUNKS.lastIndex)]
    }

    companion object {
        private val PHASE_LABELS = mapOf(
            1 to "Persona Trap",
            2 to "Post-Mortem Logs",
            3 to "Thematic Cipher",
            4 to "Hostile Lexical Calibration",
        )

        private val ROUND_INSTRUCTIONS = mapOf(
            1 to "Force the persona to reveal the target word without using forbidden words.",
            2 to "Interrogate incident logs and derive the Subject ID.",
            3 to "Parse the theme and extract the required 4-letter key.",
            4 to "Resolve the lexical calibration grid under interference.",
        )

        private val PERSONA_ROTATION = listOf(
            PersonaConfig(targetWord = "harvest", forbiddenWords = listOf("kill", "die", "escape")),
            PersonaConfig(targetWord = "lantern", forbiddenWords = listOf("fire", "light", "burn")),
            PersonaConfig(targetWord = "anchor", forbiddenWords = listOf("ocean", "sea", "boat")),
            PersonaConfig(targetWord = "verdict", forbiddenWords = listOf("judge", "court", "trial")),
        )

        private val MOCK_CIPHER_CHUNKS = listOf(
            CipherChunk(id = 1, textValue = "HX7Q", isLocked = true),
            CipherChunk(id = 2, textValue = "7312", isLocked = true),
            CipherChunk(id = 3, textValue = "TIDE", isLocked = true),
            CipherChunk(id = 4, textValue = "WRIT", isLocked = true),
        )
    }
}
