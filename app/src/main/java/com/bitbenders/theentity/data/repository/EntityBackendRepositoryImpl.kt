package com.bitbenders.theentity.data.repository

import com.bitbenders.theentity.data.remote.api.EntityBackendApi
import com.bitbenders.theentity.data.remote.dto.ArmorIqContextDto
import com.bitbenders.theentity.data.remote.dto.ArmorIqVerifyRequestDto
import com.bitbenders.theentity.data.remote.dto.ClueGeneratorRequestDto
import com.bitbenders.theentity.data.remote.dto.TerminalValidatorRequestDto
import com.bitbenders.theentity.data.remote.dto.VillainSpeechRequestDto
import com.bitbenders.theentity.domain.repository.ArmorIqResult
import com.bitbenders.theentity.domain.repository.GamePackage
import com.bitbenders.theentity.domain.repository.HealthStatus
import com.bitbenders.theentity.domain.repository.IEntityBackendRepository
import com.bitbenders.theentity.domain.repository.IncidentLog
import com.bitbenders.theentity.domain.repository.Round1Data
import com.bitbenders.theentity.domain.repository.Round2Data
import com.bitbenders.theentity.domain.repository.Round3Data
import com.bitbenders.theentity.domain.repository.Round4NativeBriefData
import com.bitbenders.theentity.domain.repository.SpeechCue
import com.bitbenders.theentity.domain.repository.TerminalValidation
import com.bitbenders.theentity.domain.repository.VillainSpeechResult
import javax.inject.Inject

/**
 * Production implementation of [IEntityBackendRepository].
 *
 * Delegates every call to the Retrofit [EntityBackendApi] and maps
 * DTOs → domain models. Throws on HTTP errors so callers can
 * handle failures in the ViewModel / UseCase layer.
 */
class EntityBackendRepositoryImpl @Inject constructor(
    private val api: EntityBackendApi,
) : IEntityBackendRepository {

    // ─── Health ──────────────────────────────────────────────────────────────

    override suspend fun checkHealth(): HealthStatus {
        val response = api.getHealth()
        val body = response.body()
            ?: throw ApiException(response.code(), "Health check failed: empty body")

        if (!response.isSuccessful) {
            throw ApiException(response.code(), "Health check failed")
        }

        return HealthStatus(
            isUp = true,
            mockMode = body.mockMode,
            supportedRoutes = body.supportedRoutes,
        )
    }

    // ─── ArmorIQ ─────────────────────────────────────────────────────────────

    override suspend fun verifyPlayerInput(
        playerInput: String,
        hiddenAnswer: String,
    ): ArmorIqResult {
        val request = ArmorIqVerifyRequestDto(
            playerInput = playerInput,
            context = ArmorIqContextDto(hiddenAnswer = hiddenAnswer),
        )

        val response = api.verifyArmorIq(request)
        val body = response.body()
            ?: throw ApiException(response.code(), "ArmorIQ verify failed: empty body")

        if (!response.isSuccessful) {
            throw ApiException(response.code(), "ArmorIQ verify failed")
        }

        return ArmorIqResult(
            allowed = body.allowed,
            blockReason = body.blockReason,
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
        val request = ClueGeneratorRequestDto(
            setting = setting,
            difficulty = difficulty,
            theme = theme,
            villainName = villainName,
            objective = objective,
        )

        val response = api.generateClues(request)
        val body = response.body()
            ?: throw ApiException(response.code(), "Clue generation failed: empty body")

        if (!response.isSuccessful) {
            throw ApiException(response.code(), "Clue generation failed")
        }

        return GamePackage(
            gameTitle = body.gameTitle,
            settingSummary = body.settingSummary,
            sharedManualIntro = body.sharedManualIntro,
            round1 = Round1Data(
                persona = body.round1.persona,
                targetWord = body.round1.targetWord,
                forbiddenWords = body.round1.forbiddenWords,
                dialogue = body.round1.dialogue,
            ),
            round2 = Round2Data(
                incidentLogs = body.round2.incidentLogs.map { log ->
                    IncidentLog(
                        victimName = log.victimName,
                        causeOfDeath = log.causeOfDeath,
                        logText = log.logText,
                    )
                },
                subjectId = body.round2.subjectId,
            ),
            round3 = Round3Data(
                theme = body.round3.theme,
                cipherText = body.round3.cipherText,
                decodingRule = body.round3.decodingRule,
                answer = body.round3.answer,
            ),
            round4NativeBrief = Round4NativeBriefData(
                homophones = body.round4NativeBrief.homophones,
                calibrationKey = body.round4NativeBrief.calibrationKey,
                correctWord = body.round4NativeBrief.correctWord,
            ),
        )
    }

    // ─── Gemini – Terminal Validator ──────────────────────────────────────────

    override suspend fun validateTerminal(
        playerInput: String,
        hiddenAnswer: String,
    ): TerminalValidation {
        val request = TerminalValidatorRequestDto(
            playerInput = playerInput,
            hiddenAnswer = hiddenAnswer,
        )

        val response = api.validateTerminal(request)
        val body = response.body()
            ?: throw ApiException(response.code(), "Terminal validation failed: empty body")

        if (!response.isSuccessful) {
            throw ApiException(response.code(), "Terminal validation failed")
        }

        return TerminalValidation(
            success = body.success,
            reason = body.reason,
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
        val request = VillainSpeechRequestDto(
            villainName = villainName,
            scene = scene,
            tone = tone,
            selectedCueId = selectedCueId,
            voiceId = voiceId,
        )

        val response = api.generateVillainSpeech(request)
        val body = response.body()
            ?: throw ApiException(response.code(), "Villain speech generation failed: empty body")

        if (!response.isSuccessful) {
            throw ApiException(response.code(), "Villain speech generation failed")
        }

        return VillainSpeechResult(
            speechCues = body.speechCues.map { cue ->
                SpeechCue(cueId = cue.cueId, text = cue.text)
            },
            selectedCueId = body.selectedCueId,
            audioBase64 = body.audioBase64,
            mimeType = body.mimeType,
            ttsProvider = body.ttsProvider,
        )
    }
}

/**
 * Thrown when the relay returns a non-successful HTTP status.
 */
class ApiException(val httpCode: Int, message: String) : Exception("[$httpCode] $message")
