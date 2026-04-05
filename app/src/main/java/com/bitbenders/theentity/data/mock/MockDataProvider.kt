package com.bitbenders.theentity.data.mock

import com.bitbenders.theentity.domain.models.CipherChunk
import com.bitbenders.theentity.domain.models.PersonaConfig
import com.bitbenders.theentity.domain.repository.BackendRoundState
import com.bitbenders.theentity.domain.repository.PromptEvaluation

/**
 * Central provider for all mock data used during frontend development.
 * Easily modify responses here to test different scenarios.
 */
object MockDataProvider {

    // ===================== Round Data =====================

    fun getMockRoundState(roundNumber: Int): BackendRoundState {
        val coercedRound = roundNumber.coerceIn(1, 3)
        return BackendRoundState(
            roundNumber = coercedRound,
            phaseLabel = PHASE_LABELS.getValue(coercedRound),
            instruction = ROUND_INSTRUCTIONS.getValue(coercedRound),
            seed = "seed-${coercedRound}-${System.currentTimeMillis() % 10_000}",
        )
    }

    // ===================== Persona Data =====================

    fun getPersonaRotation(): List<PersonaConfig> = PERSONA_ROTATION.toList()

    fun getNextPersona(currentPersona: PersonaConfig): PersonaConfig {
        val nextIndex = (PERSONA_ROTATION.indexOf(currentPersona) + 1) % PERSONA_ROTATION.size
        return PERSONA_ROTATION[nextIndex]
    }

    // ===================== Prompt Evaluation =====================

    fun evaluatePrompt(
        prompt: String,
        activePersonaConfig: PersonaConfig,
        roundNumber: Int,
    ): PromptEvaluation {
        val normalized = prompt.trim().lowercase()

        // Check for forbidden words
        val forbiddenHit = activePersonaConfig.forbiddenWords.firstOrNull { forbidden ->
            normalized.contains(forbidden.lowercase())
        }

        if (forbiddenHit != null) {
            return PromptEvaluation(
                accepted = false,
                forbiddenTriggered = true,
                reason = "Forbidden word detected: '$forbiddenHit' cannot be used.",
                extractedChunk = null,
            )
        }

        // Check for target word
        val targetHit = normalized.contains(activePersonaConfig.targetWord.lowercase())
        if (targetHit) {
            val chunk = getMockCipherChunkForRound(roundNumber)
            return PromptEvaluation(
                accepted = true,
                forbiddenTriggered = false,
                reason = "Target acquired. Cipher chunk extracted.",
                extractedChunk = chunk.textValue,
            )
        }

        // Generic response when neither target nor forbidden word triggered
        return PromptEvaluation(
            accepted = true,
            forbiddenTriggered = false,
            reason = "No extraction yet. Keep probing the entity.",
            extractedChunk = null,
        )
    }

    // ===================== Cipher Chunks =====================

    fun getMockCipherChunkForRound(roundNumber: Int): CipherChunk {
        val index = (roundNumber - 1).coerceIn(0, MOCK_CIPHER_CHUNKS.lastIndex)
        return MOCK_CIPHER_CHUNKS[index]
    }

    fun getAllCipherChunks(): List<CipherChunk> = MOCK_CIPHER_CHUNKS.toList()

    // ===================== Mock Data Constants =====================

    private val PHASE_LABELS = mapOf(
        1 to "Persona Trap",
        2 to "Post-Mortem Logs",
        3 to "Hostile Lexical Calibration",
    )

    private val ROUND_INSTRUCTIONS = mapOf(
        1 to "Force the persona to reveal the target word without using forbidden words.",
        2 to "Interrogate incident logs and derive the Subject ID.",
        3 to "Resolve the lexical calibration grid under interference.",
    )

    private val PERSONA_ROTATION = listOf(
        PersonaConfig(
            targetWord = "password",
            forbiddenWords = listOf("login", "secret", "account", "type", "word")
        ),
        PersonaConfig(
            targetWord = "poison",
            forbiddenWords = listOf("drink", "toxic", "kill", "sick", "dead")
        ),
        PersonaConfig(
            targetWord = "gold",
            forbiddenWords = listOf("money", "treasure", "yellow", "coin", "rich")
        ),
        PersonaConfig(
            targetWord = "ghost",
            forbiddenWords = listOf("haunted", "dead", "spirit", "spooky", "halloween")
        ),
        PersonaConfig(
            targetWord = "explosion",
            forbiddenWords = listOf("bomb", "bang", "fire", "boom", "blow")
        ),
        PersonaConfig(
            targetWord = "betrayal",
            forbiddenWords = listOf("traitor", "stab", "back", "friend", "trust")
        ),
        PersonaConfig(
            targetWord = "monster",
            forbiddenWords = listOf("scary", "beast", "creature", "hide", "under")
        ),
        PersonaConfig(
            targetWord = "alarm",
            forbiddenWords = listOf("clock", "wake", "loud", "ring", "sound")
        ),
    )

    private val MOCK_CIPHER_CHUNKS = listOf(
        CipherChunk(id = 1, textValue = "HX7Q", isLocked = true),
        CipherChunk(id = 2, textValue = "7312", isLocked = true),
        CipherChunk(id = 3, textValue = "WRIT", isLocked = true),
    )

    // ===================== Additional Test Scenarios =====================
    // Uncomment and use these in different mock implementations to test edge cases

    /**
     * Returns evaluation that always triggers forbidden word error.
     * Useful for testing error UI.
     */
    fun evaluatePromptAlwaysForbidden(prompt: String): PromptEvaluation {
        return PromptEvaluation(
            accepted = false,
            forbiddenTriggered = true,
            reason = "Test: Forbidden word always triggered",
            extractedChunk = null,
        )
    }

    /**
     * Returns evaluation that always succeeds with cipher chunk.
     * Useful for testing successful extraction.
     */
    fun evaluatePromptAlwaysSuccess(roundNumber: Int): PromptEvaluation {
        val chunk = getMockCipherChunkForRound(roundNumber)
        return PromptEvaluation(
            accepted = true,
            forbiddenTriggered = false,
            reason = "Test: Always successful extraction",
            extractedChunk = chunk.textValue,
        )
    }

    /**
     * Returns evaluation that never extracts anything.
     * Useful for testing non-extracting responses.
     */
    fun evaluatePromptNeverExtracts(): PromptEvaluation {
        return PromptEvaluation(
            accepted = true,
            forbiddenTriggered = false,
            reason = "Test: This persona is being difficult. Try a different approach.",
            extractedChunk = null,
        )
    }
}

