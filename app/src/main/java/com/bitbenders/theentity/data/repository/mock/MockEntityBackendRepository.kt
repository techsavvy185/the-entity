package com.bitbenders.theentity.data.repository.mock

import com.bitbenders.theentity.domain.repository.ArmorIqResult
import com.bitbenders.theentity.domain.repository.GamePackage
import com.bitbenders.theentity.domain.repository.HealthStatus
import com.bitbenders.theentity.domain.repository.IEntityBackendRepository
import com.bitbenders.theentity.domain.repository.IncidentLog
import com.bitbenders.theentity.domain.repository.Round1Data
import com.bitbenders.theentity.domain.repository.Round2Data
import com.bitbenders.theentity.domain.repository.Round3Data
import com.bitbenders.theentity.domain.repository.Round4NativeBriefData
import com.bitbenders.theentity.domain.repository.RoomSession
import com.bitbenders.theentity.domain.repository.SpeechCue
import com.bitbenders.theentity.domain.repository.TerminalValidation
import com.bitbenders.theentity.domain.repository.VillainSpeechResult
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Mock implementation of IEntityBackendRepository for frontend development.
 * Simulates backend responses with realistic delays.
 */
class MockEntityBackendRepository @Inject constructor() : IEntityBackendRepository {

    private var activeRoomId: String = "AAAAAB"

    // ─── Health ──────────────────────────────────────────────────────────────
    override suspend fun checkHealth(): HealthStatus {
        delay(500)  // Simulate network latency
        return HealthStatus(
            isUp = true,
            mockMode = true,
            supportedRoutes = listOf("health", "verify", "clues", "validate", "speech")
        )
    }

    override suspend fun initiateRoom(seedLabel: String): RoomSession {
        delay(300)
        activeRoomId = "AAAAAB"
        return RoomSession(roomId = activeRoomId)
    }

    override suspend fun joinRoom(roomId: String): RoomSession {
        delay(250)
        activeRoomId = roomId
        return RoomSession(roomId = activeRoomId)
    }

    // ─── ArmorIQ ──────────────────────────────────────────────────────────────
    override suspend fun verifyPlayerInput(
        playerInput: String,
        hiddenAnswer: String,
    ): ArmorIqResult {
        delay(1_500)  // Simulate network latency

        // ArmorIQ checks for forbidden words, NOT if you got the answer right
        val containsForbiddenWord = playerInput.contains("kill", ignoreCase = true) ||
                                    playerInput.contains("destroy", ignoreCase = true)

        return ArmorIqResult(
            allowed = !containsForbiddenWord,
            blockReason = if (containsForbiddenWord) "Forbidden word detected" else null
        )
    }

    // ─── Gemini – Clue Generator ─────────────────────────────────────────────
    override suspend fun generateClues(
        setting: String,
        difficulty: String,
        theme: String,
        villainName: String,
        objective: String,
    ): GamePackage {
        delay(2_000)  // Simulate network latency for AI generation

        return GamePackage(
            gameTitle = "The Entity Protocol",
            settingSummary = "A classified research facility where an anomalous entity is contained.",
            sharedManualIntro = "You have 5 minutes to extract critical information from the entity.",
            round1 = Round1Data(
                persona = "Paranoid Soldier",
                targetWord = "harvest",
                forbiddenWords = listOf("kill", "destroy"),
                dialogue = "They're in the wire! Movement detected!"
            ),
            round2 = Round2Data(
                incidentLogs = listOf(
                    IncidentLog(
                        victimName = "Dr. Sarah Chen",
                        causeOfDeath = "Exposure to entity",
                        logText = "Subject became non-responsive during containment breach"
                    )
                ),
                subjectId = "7312"
            ),
            round3 = Round3Data(
                theme = "Void Patterns",
                cipherText = "VYMD",
                decodingRule = "Caesar cipher, shift -1",
                answer = "VOID"
            ),
            round4NativeBrief = Round4NativeBriefData(
                homophones = listOf("WAIT", "WEIGHT", "RIGHT", "WRITE", "HOLE", "WHOLE"),
                calibrationKey = "C7",
                correctWord = "WRITE"
            )
        )
    }

    // ─── Gemini – Terminal Validator ──────────────────────────────────────────
    override suspend fun validateTerminal(
        playerInput: String,
        hiddenAnswer: String,
    ): TerminalValidation {
        delay(1_000)  // Simulate network latency for validation

        val isValid = playerInput.contains(hiddenAnswer, ignoreCase = true)
        return TerminalValidation(
            success = isValid,
            reason = if (isValid) "Input accepted" else "Target pattern not detected."
        )
    }

    // ─── Villain Speech ──────────────────────────────────────────────────────
    override suspend fun generateVillainSpeech(
        villainName: String,
        scene: String,
        tone: String,
        selectedCueId: String?,
        voiceId: String?,
    ): VillainSpeechResult {
        delay(2_500)  // Simulate network latency for speech generation

        val speeches = listOf(
            "You think you can outsmart me?",
            "How fascinating... another subject for observation.",
            "Your desperation amuses me.",
            "The pattern reveals itself at last."
        )

        return VillainSpeechResult(
            selectedCueId = selectedCueId ?: "cue_default",
            audioBase64 = "mock_audio_data",
            mimeType = "audio/wav",
            ttsProvider = "elevenlabs",
            speechCues = listOf(
                SpeechCue(
                    cueId = "cue_1",
                    text = speeches.random()
                )
            )
        )
    }
}

