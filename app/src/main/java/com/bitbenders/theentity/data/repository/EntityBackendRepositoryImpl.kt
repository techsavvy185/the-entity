package com.bitbenders.theentity.data.repository

import com.bitbenders.theentity.data.remote.api.EntityBackendApi
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
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import javax.inject.Inject
import kotlinx.coroutines.delay
import retrofit2.Response

/**
 * Production repository using SpacetimeDB reducers.
 *
 * Scope intentionally limited to round 1 real reducers for now.
 */
class EntityBackendRepositoryImpl @Inject constructor(
    private val api: EntityBackendApi,
) : IEntityBackendRepository {

    private val gson = Gson()
    private var identityToken: String? = null
    private var activeRoomId: String? = null
    private var lastTerminalSnapshot: TerminalSnapshot? = null

    // ─── Health ──────────────────────────────────────────────────────────────

    override suspend fun checkHealth(): HealthStatus {
        val isUp = runCatching {
            queryRows("select * from game_room limit 1")
        }.isSuccess

        return HealthStatus(
            isUp = isUp,
            mockMode = false,
            supportedRoutes = listOf(
                "initiate_room",
                "join_room",
                "terminate_room",
                "submit_terminal_for_room",
                "generate_clue_manual_for_room",
                "generate_villain_speech_for_room",
            ),
        )
    }

    override suspend fun initiateRoom(seedLabel: String): RoomSession {
        callReducer("initiate_room", listOf(mapOf("some" to seedLabel)))

        val roomTicket = awaitSingleRow(
            "select * from room_ticket order by id desc limit 1",
            timeoutMs = 5_000L,
        )

        val resolved = roomTicket?.readString("room_id", "roomId", "ticket", "code")
            ?: throw ApiException(500, "Failed to resolve room id from room_ticket")

        activeRoomId = resolved
        return RoomSession(roomId = resolved)
    }

    override suspend fun joinRoom(roomId: String): RoomSession {
        callReducer("join_room", listOf(roomId))
        activeRoomId = roomId
        return RoomSession(roomId = roomId)
    }

    // ─── ArmorIQ ─────────────────────────────────────────────────────────────

    override suspend fun verifyPlayerInput(
        playerInput: String,
        hiddenAnswer: String,
    ): ArmorIqResult {
        val roomId = ensureRoom()
        callReducer("submit_terminal_for_room", listOf(roomId, playerInput))

        val row = awaitSingleRow(
            "select * from game_state where room_id = '$roomId' order by id desc limit 1",
            timeoutMs = 8_000L,
        )

        val allowed = row?.readBoolean("armoriq_allowed", "allowed", "input_allowed")
            ?: !containsForbiddenLexicon(playerInput)

        val success = row?.readBoolean("validator_success", "terminal_success", "gemini_success")
            ?: playerInput.contains(hiddenAnswer, ignoreCase = true)

        val reason = row?.readString(
            "validator_reason",
            "terminal_reason",
            "block_reason",
            "status_message",
        ) ?: if (success) "Input accepted" else "Target pattern not detected"

        lastTerminalSnapshot = TerminalSnapshot(
            input = playerInput,
            roomId = roomId,
            success = success,
            reason = reason,
        )

        return ArmorIqResult(
            allowed = allowed,
            blockReason = if (allowed) null else reason,
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
        val roomId = ensureRoom()

        // Round-1 only real reducer implementation.
        val requestPayload = gson.toJson(mapOf("requested_persona" to "1920s Detective"))
        callReducer(
            "generate_clue_manual_for_room",
            listOf(roomId, "round_1", requestPayload, ""),
        )

        val row = awaitSingleRow(
            "select * from round_content_artifact where room_id = '$roomId' and round_key = 'round_1' order by id desc limit 1",
            timeoutMs = 15_000L,
        )

        val payload = row?.readPayloadJson("response_json", "content_json", "artifact_json")

        val round1 = Round1Data(
            persona = payload?.readString("persona") ?: "1920s Detective",
            targetWord = payload?.readString("target_word") ?: "harvest",
            forbiddenWords = payload?.readStringList("forbidden_words").orEmpty().ifEmpty {
                listOf("kill", "destroy")
            },
            dialogue = payload?.readString("dialogue") ?: "Clockwork lies unravel in static.",
        )

        // Placeholder data for rounds 2-4 until those reducers are integrated.
        return GamePackage(
            gameTitle = payload?.readString("game_title") ?: "The Entity",
            settingSummary = payload?.readString("setting_summary") ?: setting,
            sharedManualIntro = payload?.readString("shared_manual_intro") ?: objective,
            round1 = round1,
            round2 = Round2Data(
                incidentLogs = listOf(
                    IncidentLog(
                        victimName = "Unknown",
                        causeOfDeath = "Classified",
                        logText = "Round 2 reducer not integrated yet.",
                    ),
                ),
                subjectId = "0000",
            ),
            round3 = Round3Data(
                theme = theme,
                cipherText = "N/A",
                decodingRule = "Round 3 reducer not integrated yet.",
                answer = "N/A",
            ),
            round4NativeBrief = Round4NativeBriefData(
                homophones = listOf("WAIT", "WEIGHT", "RIGHT", "WRITE", "HOLE", "WHOLE"),
                calibrationKey = "C7",
                correctWord = "WRITE",
            ),
        )
    }

    // ─── Gemini – Terminal Validator ──────────────────────────────────────────

    override suspend fun validateTerminal(
        playerInput: String,
        hiddenAnswer: String,
    ): TerminalValidation {
        val roomId = ensureRoom()
        val cached = lastTerminalSnapshot
        if (cached != null && cached.input == playerInput && cached.roomId == roomId) {
            return TerminalValidation(success = cached.success, reason = cached.reason)
        }

        // Fallback if verify was skipped.
        val allowed = verifyPlayerInput(playerInput, hiddenAnswer).allowed
        val snapshot = lastTerminalSnapshot
        if (!allowed) {
            return TerminalValidation(success = false, reason = snapshot?.reason ?: "Blocked by ArmorIQ")
        }

        return TerminalValidation(
            success = snapshot?.success ?: playerInput.contains(hiddenAnswer, ignoreCase = true),
            reason = snapshot?.reason ?: "Validation completed",
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
        val roomId = ensureRoom()

        val payload = gson.toJson(
            mapOf(
                "round_key" to "round_1",
                "villain_name" to villainName,
                "scene" to scene,
                "tone" to tone,
                "selected_cue_id" to selectedCueId,
                "voice_id" to voiceId,
                "synthesize_audio" to true,
                "clue_contexts" to listOf(
                    mapOf("cue_id" to "r1_c1", "clue_text" to "first clue"),
                ),
            ),
        )

        callReducer("generate_villain_speech_for_room", listOf(roomId, payload))

        val row = awaitSingleRow(
            "select * from villain_speech_artifact where room_id = '$roomId' order by id desc limit 1",
            timeoutMs = 15_000L,
        )
        val data = row?.readPayloadJson("response_json", "payload_json", "artifact_json")

        val cues = data?.readObjectArray("speech_cues")
            ?.map { cue -> SpeechCue(cue.readString("cue_id") ?: "cue_1", cue.readString("text") ?: "...") }
            .orEmpty()

        return VillainSpeechResult(
            speechCues = cues.ifEmpty {
                listOf(SpeechCue("cue_1", "The Entity watches in silence."))
            },
            selectedCueId = data?.readString("selected_cue_id") ?: selectedCueId ?: "cue_1",
            audioBase64 = data?.readString("audio_base64"),
            mimeType = data?.readString("mime_type"),
            ttsProvider = "elevenlabs",
        )
    }

    private suspend fun ensureRoom(): String {
        activeRoomId?.let { return it }
        return initiateRoom().roomId
    }

    private suspend fun callReducer(reducerName: String, args: List<Any?>): JsonElement {
        val jsonArgs = JsonArray().apply {
            args.forEach { add(gson.toJsonTree(it)) }
        }

        val response = api.callReducer(
            reducerName = reducerName,
            authorization = authHeader(),
            args = jsonArgs,
        )
        captureIdentity(response)

        if (!response.isSuccessful) {
            throw ApiException(response.code(), "Reducer call failed: $reducerName")
        }

        return response.body() ?: JsonNull.INSTANCE
    }

    private suspend fun queryRows(sql: String): List<JsonObject> {
        val response = api.querySql(authHeader(), sql)
        captureIdentity(response)

        if (!response.isSuccessful) {
            throw ApiException(response.code(), "SQL query failed")
        }

        return (response.body() ?: JsonNull.INSTANCE).extractRows()
    }

    private suspend fun awaitSingleRow(sql: String, timeoutMs: Long): JsonObject? {
        val started = System.currentTimeMillis()
        while (System.currentTimeMillis() - started < timeoutMs) {
            val row = queryRows(sql).firstOrNull()
            if (row != null) {
                val status = row.readString("status", "gemini_status", "tts_status")?.lowercase()
                if (status == null || status !in PENDING) return row
            }
            delay(400)
        }
        return queryRows(sql).firstOrNull()
    }

    private fun authHeader(): String? = identityToken?.let { "Bearer $it" }

    private fun captureIdentity(response: Response<*>) {
        val token = response.headers()[IDENTITY_HEADER]
        if (!token.isNullOrBlank()) identityToken = token
    }

    private fun containsForbiddenLexicon(input: String): Boolean {
        return listOf("kill", "destroy", "murder").any { input.contains(it, ignoreCase = true) }
    }

    private fun JsonElement.extractRows(): List<JsonObject> {
        if (this is JsonArray) return mapNotNull { it as? JsonObject }
        if (this !is JsonObject) return emptyList()

        val candidates = listOf("rows", "data", "result", "records")
        val rows = candidates.firstNotNullOfOrNull { key -> get(key) }
        return if (rows is JsonArray) rows.mapNotNull { it as? JsonObject } else emptyList()
    }

    private fun JsonObject.readString(vararg keys: String): String? {
        keys.forEach { key ->
            val value = get(key) ?: return@forEach
            if (!value.isJsonNull && value.isJsonPrimitive) return value.asString
        }
        return null
    }

    private fun JsonObject.readBoolean(vararg keys: String): Boolean? {
        keys.forEach { key ->
            val value = get(key) ?: return@forEach
            if (!value.isJsonNull && value.isJsonPrimitive) {
                return runCatching { value.asBoolean }.getOrNull()
            }
        }
        return null
    }

    private fun JsonObject.readStringList(key: String): List<String>? {
        val arr = get(key) as? JsonArray ?: return null
        return arr.mapNotNull { if (it.isJsonPrimitive) it.asString else null }
    }

    private fun JsonObject.readObjectArray(key: String): List<JsonObject>? {
        val arr = get(key) as? JsonArray ?: return null
        return arr.mapNotNull { it as? JsonObject }
    }

    private fun JsonObject.readPayloadJson(vararg keys: String): JsonObject? {
        keys.forEach { key ->
            val value = get(key) ?: return@forEach
            if (value is JsonObject) return value
            if (value.isJsonPrimitive && value.asJsonPrimitive.isString) {
                runCatching { gson.fromJson(value.asString, JsonObject::class.java) }
                    .getOrNull()
                    ?.let { return it }
            }
        }
        return null
    }

    companion object {
        private const val IDENTITY_HEADER = "spacetime-identity-token"
        private val PENDING = setOf("pending", "pendinggemini", "pendingtts")
    }
}

private data class TerminalSnapshot(
    val input: String,
    val roomId: String,
    val success: Boolean,
    val reason: String,
)

class ApiException(val httpCode: Int, message: String) : Exception("[$httpCode] $message")
