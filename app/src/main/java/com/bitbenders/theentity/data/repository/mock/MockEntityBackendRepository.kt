package com.bitbenders.theentity.data.repository.mock

import com.bitbenders.theentity.data.round1.RoundOneCatalog
import com.bitbenders.theentity.data.round1.WordPuzzleEntry
import com.bitbenders.theentity.data.round2.RoundTwoCatalog
import com.bitbenders.theentity.domain.repository.ArmorIqResult
import com.bitbenders.theentity.domain.repository.GamePackage
import com.bitbenders.theentity.domain.repository.HealthStatus
import com.bitbenders.theentity.domain.repository.IEntityBackendRepository
import com.bitbenders.theentity.domain.repository.IncidentLog
import com.bitbenders.theentity.domain.repository.Round1Data
import com.bitbenders.theentity.domain.repository.RoundOneSelection
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
    private var selectedPersona: String? = null
    private var selectedPuzzle: WordPuzzleEntry? = null

    // ─── Health ──────────────────────────────────────────────────────────────
    override suspend fun checkHealth(): HealthStatus {
        delay(500)  // Simulate network latency
        return HealthStatus(
            isUp = true,
            mockMode = true,
            supportedRoutes = listOf(
                "initiate_room",
                "join_room",
                "terminate_room",
                "submit_terminal",
                "submit_terminal_for_room",
                "generate_clue_manual_for_room",
                "generate_villain_speech_for_room",
                "configure_integrations",
                "configure_voice_integrations",
                "set_hidden_answer",
                "set_hidden_answer_for_room",
                "configure_local_dev_integrations",
            )
        )
    }

    override fun peekRoundOneSelection(): RoundOneSelection? {
        ensureRoundOneSelection()
        val persona = selectedPersona ?: return null
        val puzzle = selectedPuzzle ?: return null
        return RoundOneSelection(
            persona = persona,
            targetWord = puzzle.targetWord,
            forbiddenWords = puzzle.forbiddenWords,
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
        ensureRoundOneSelection()
        val persona = selectedPersona ?: RoundOneCatalog.selectPersona(activeRoomId)
        val puzzle = selectedPuzzle ?: RoundOneCatalog.selectPuzzle(activeRoomId)

        return GamePackage(
            gameTitle = "The Entity Protocol",
            settingSummary = "A classified research facility where an anomalous entity is contained.",
            sharedManualIntro = "You have 5 minutes to extract critical information from the entity.",
            round1 = Round1Data(
                persona = persona,
                targetWord = puzzle.targetWord,
                forbiddenWords = puzzle.forbiddenWords,
                dialogue = "A tense monologue bleeds through the channel. The voice carries distinct era-specific cues."
            ),
            round2 = Round2Data(
                incidentLogs = listOf(RoundTwoCatalog.questionOneIncidentLog),
                subjectId = RoundTwoCatalog.questionOneCode
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
        ensureRoundOneSelection()
        val persona = selectedPersona ?: RoundOneCatalog.selectPersona(activeRoomId)
        val puzzle = selectedPuzzle ?: RoundOneCatalog.selectPuzzle(activeRoomId)

        val speeches = listOf(
            "$villainName: your $persona mask is transparent. Say ${puzzle.targetWord}.",
            "$villainName: I will redact every hint except ${puzzle.targetWord}.",
            "$villainName: your nerves betray you; the key remains ${puzzle.targetWord}.",
            "$villainName: the protocol converges on ${puzzle.targetWord}."
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

    private fun ensureRoundOneSelection() {
        if (selectedPersona == null) {
            selectedPersona = RoundOneCatalog.selectPersona(activeRoomId)
        }
        if (selectedPuzzle == null) {
            selectedPuzzle = RoundOneCatalog.selectPuzzle(activeRoomId)
        }
    }
}
