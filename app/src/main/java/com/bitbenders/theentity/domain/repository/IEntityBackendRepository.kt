package com.bitbenders.theentity.domain.repository

import com.bitbenders.theentity.domain.models.PersonaConfig

/**
 * Abstraction over the Entity local relay (server.js) HTTP endpoints.
 *
 * Implementations of this interface should delegate to [EntityBackendApi]
 * via Retrofit and map DTOs to clean domain types.
 */
interface IEntityBackendRepository {

    // ─── Health ──────────────────────────────────────────────────────────────

    suspend fun checkHealth(): HealthStatus

    // ─── Room Lifecycle ─────────────────────────────────────────────────────

    suspend fun initiateRoom(seedLabel: String = "The Entity"): RoomSession
    suspend fun joinRoom(roomId: String): RoomSession

    // ─── ArmorIQ ─────────────────────────────────────────────────────────────

    suspend fun verifyPlayerInput(
        playerInput: String,
        hiddenAnswer: String,
    ): ArmorIqResult

    // ─── Gemini – Clue Generator ─────────────────────────────────────────────

    suspend fun generateClues(
        setting: String,
        difficulty: String,
        theme: String,
        villainName: String,
        objective: String,
    ): GamePackage

    // ─── Gemini – Terminal Validator ──────────────────────────────────────────

    suspend fun validateTerminal(
        playerInput: String,
        hiddenAnswer: String,
    ): TerminalValidation

    // ─── Villain Speech ──────────────────────────────────────────────────────

    suspend fun generateVillainSpeech(
        villainName: String,
        scene: String,
        tone: String,
        selectedCueId: String? = null,
        voiceId: String? = null,
    ): VillainSpeechResult
}

// ─── Domain result types ─────────────────────────────────────────────────────

data class HealthStatus(
    val isUp: Boolean,
    val mockMode: Boolean,
    val supportedRoutes: List<String>,
)

data class RoomSession(
    val roomId: String,
)

data class ArmorIqResult(
    val allowed: Boolean,
    val blockReason: String?,
)

data class GamePackage(
    val gameTitle: String,
    val settingSummary: String,
    val sharedManualIntro: String,
    val round1: Round1Data,
    val round2: Round2Data,
    val round3: Round3Data,
    val round4NativeBrief: Round4NativeBriefData,
)

data class Round1Data(
    val persona: String,
    val targetWord: String,
    val forbiddenWords: List<String>,
    val dialogue: String,
)

data class Round2Data(
    val incidentLogs: List<IncidentLog>,
    val subjectId: String,
)

data class IncidentLog(
    val victimName: String,
    val causeOfDeath: String,
    val logText: String,
)

data class Round3Data(
    val theme: String,
    val cipherText: String,
    val decodingRule: String,
    val answer: String,
)

data class Round4NativeBriefData(
    val homophones: List<String>,
    val calibrationKey: String,
    val correctWord: String,
)

data class TerminalValidation(
    val success: Boolean,
    val reason: String,
)

data class VillainSpeechResult(
    val speechCues: List<SpeechCue>,
    val selectedCueId: String,
    val audioBase64: String?,
    val mimeType: String?,
    val ttsProvider: String?,
)

data class SpeechCue(
    val cueId: String,
    val text: String,
)

// ─── Legacy types (kept for backward compat) ─────────────────────────────────

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
