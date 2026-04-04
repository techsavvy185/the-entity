package com.bitbenders.theentity.data.repository

import com.bitbenders.theentity.data.remote.api.EntityBackendApi
import com.bitbenders.theentity.data.round1.RoundOneCatalog
import com.bitbenders.theentity.data.round1.WordPuzzleEntry
import com.bitbenders.theentity.data.round2.RoundTwoCatalog
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
    private var identityHex: String? = null
    private var activeRoomId: String? = null
    private var lastTerminalSnapshot: TerminalSnapshot? = null
    private var selectedPersona: String? = null
    private var selectedPuzzle: WordPuzzleEntry? = null
    private var localDevIntegrationsConfigured: Boolean = false
    private var lastConfiguredHiddenAnswer: String? = null

    // ─── Health ──────────────────────────────────────────────────────────────

    override suspend fun checkHealth(): HealthStatus {
        val isUp = runCatching {
            queryRows("select * from game_room")
        }.isSuccess

        return HealthStatus(
            isUp = isUp,
            mockMode = false,
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
            ),
        )
    }

    override suspend fun initiateRoom(seedLabel: String): RoomSession {
        val response = api.initiateRoom(
            authorization = null,
            args = JsonArray().apply { add(gson.toJsonTree(mapOf("some" to seedLabel))) },
        )
        captureIdentity(response)

        if (!response.isSuccessful) {
            // Fallback option payload accepted by reducer for optional fields.
            val fallback = api.initiateRoom(
                authorization = null,
                args = JsonArray().apply { add(gson.toJsonTree(mapOf("none" to emptyList<Any>()))) },
            )
            captureIdentity(fallback)
            if (!fallback.isSuccessful) {
                throw ApiException(
                    fallback.code(),
                    "Reducer call failed: initiate_room ${fallback.errorBody()?.string().orEmpty()}",
                )
            }
        }

        val resolved = fetchRoomIdFromMyRoomInfo()
            ?: run {
                // Fallback path for older deployments without get_my_room_info.
                val roomTicket = awaitSingleRow(
                    "select * from room_ticket",
                    timeoutMs = 5_000L,
                )
                roomTicket?.readString("room_id", "roomId", "ticket", "code", "c1")
            }
            ?: throw ApiException(500, "Failed to resolve room id from get_my_room_info/room_ticket")

        activeRoomId = resolved
        return RoomSession(roomId = resolved)
    }

    override suspend fun joinRoom(roomId: String): RoomSession {
        val args = JsonArray().apply { add(gson.toJsonTree(roomId)) }
        val response = api.joinRoom(
            authorization = null,
            args = args,
        )
        captureIdentity(response)

        if (!response.isSuccessful) {
            throw ApiException(
                response.code(),
                "Reducer call failed: join_room ${response.errorBody()?.string().orEmpty()}",
            )
        }

        activeRoomId = roomId
        return RoomSession(roomId = roomId)
    }

    // ─── ArmorIQ ─────────────────────────────────────────────────────────────

    override suspend fun verifyPlayerInput(
        playerInput: String,
        hiddenAnswer: String,
    ): ArmorIqResult {
        val roomId = ensureRoom()
        val args = JsonArray().apply {
            add(gson.toJsonTree(roomId))
            add(gson.toJsonTree(playerInput))
        }
        var response = api.submitTerminalForRoom(
            authorization = authHeader(),
            args = args,
        )
        captureIdentity(response)

        if (!response.isSuccessful) {
            val firstError = response.errorBody()?.string().orEmpty()
            if (response.code() == 530 || firstError.contains("not configured for game", ignoreCase = true)) {
                configureTerminalForRoomIfNeeded(roomId = roomId, hiddenAnswer = hiddenAnswer)
                response = api.submitTerminalForRoom(
                    authorization = authHeader(),
                    args = args,
                )
                captureIdentity(response)
                if (!response.isSuccessful) {
                    throw ApiException(
                        response.code(),
                        "Reducer call failed: submit_terminal_for_room ${response.errorBody()?.string().orEmpty()}",
                    )
                }
            } else {
                throw ApiException(
                    response.code(),
                    "Reducer call failed: submit_terminal_for_room $firstError",
                )
            }
        }

        val row = awaitGameStateRow(roomId)

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

    private suspend fun configureTerminalForRoomIfNeeded(roomId: String, hiddenAnswer: String) {
        if (!localDevIntegrationsConfigured) {
            val configureArgs = JsonArray().apply {
                add(gson.toJsonTree("http://localhost"))
                add(gson.toJsonTree("mock-key"))
            }
            val configureResponse = api.configureLocalDevIntegrations(
                authorization = authHeader(),
                args = configureArgs,
            )
            captureIdentity(configureResponse)

            if (!configureResponse.isSuccessful) {
                throw ApiException(
                    configureResponse.code(),
                    "Reducer call failed: configure_local_dev_integrations ${configureResponse.errorBody()?.string().orEmpty()}",
                )
            }
            localDevIntegrationsConfigured = true
        }

        if (lastConfiguredHiddenAnswer == hiddenAnswer) return

        val setHiddenArgs = JsonArray().apply {
            add(gson.toJsonTree(roomId))
            add(gson.toJsonTree(hiddenAnswer))
        }
        val setHiddenResponse = api.setHiddenAnswerForRoom(
            authorization = authHeader(),
            args = setHiddenArgs,
        )
        captureIdentity(setHiddenResponse)

        if (!setHiddenResponse.isSuccessful) {
            throw ApiException(
                setHiddenResponse.code(),
                "Reducer call failed: set_hidden_answer_for_room ${setHiddenResponse.errorBody()?.string().orEmpty()}",
            )
        }

        lastConfiguredHiddenAnswer = hiddenAnswer
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
        ensureRoundOneSelection(roomId)

        // Round-1 only: persona and puzzle are selected locally (no Gemini selection).
        val localPersona = selectedPersona ?: RoundOneCatalog.selectPersona(roomId)
        val localPuzzle = selectedPuzzle ?: RoundOneCatalog.selectPuzzle(roomId)
        val requestPayload = gson.toJson(
            mapOf(
                "requested_persona" to localPersona,
                "target_word" to localPuzzle.targetWord,
                "forbidden_words" to localPuzzle.forbiddenWords,
            ),
        )
        val args = JsonArray().apply {
            add(gson.toJsonTree(roomId))
            add(gson.toJsonTree("round_1"))
            add(gson.toJsonTree(requestPayload))
            add(gson.toJsonTree(""))
        }
        val response = api.generateClueManualForRoom(
            authorization = authHeader(),
            args = args,
        )
        captureIdentity(response)

        if (!response.isSuccessful) {
            throw ApiException(
                response.code(),
                "Reducer call failed: generate_clue_manual_for_room ${response.errorBody()?.string().orEmpty()}",
            )
        }

        val row = awaitSingleRow(
            "select * from round_content_artifact where room_id = '$roomId' and round_key = 'round_1'",
            timeoutMs = 15_000L,
        )

        val payload = row?.readPayloadJson("response_json", "content_json", "artifact_json")

        val round1 = Round1Data(
            persona = localPersona,
            targetWord = localPuzzle.targetWord,
            forbiddenWords = localPuzzle.forbiddenWords,
            dialogue = payload?.readString("dialogue")
                ?: payload?.readString("paragraph")
                ?: payload?.readString("voiceover_sentence")
                ?: "$localPersona mutters in panic, circling the word ${localPuzzle.targetWord}.",
        )

        // Placeholder data for rounds 2-4 until those reducers are integrated.
        return GamePackage(
            gameTitle = payload?.readString("game_title") ?: "The Entity",
            settingSummary = payload?.readString("setting_summary") ?: setting,
            sharedManualIntro = payload?.readString("shared_manual_intro") ?: objective,
            round1 = round1,
            round2 = Round2Data(
                incidentLogs = listOf(RoundTwoCatalog.questionOneIncidentLog),
                subjectId = RoundTwoCatalog.questionOneCode,
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
        ensureRoundOneSelection(roomId)
        val localPersona = selectedPersona ?: RoundOneCatalog.selectPersona(roomId)
        val localPuzzle = selectedPuzzle ?: RoundOneCatalog.selectPuzzle(roomId)

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
                    mapOf("cue_id" to "r1_c1", "clue_text" to "Persona=$localPersona Target=${localPuzzle.targetWord}"),
                ),
                "round_output" to mapOf(
                    "persona_name" to localPersona,
                    "target_word" to localPuzzle.targetWord,
                    "forbidden_words" to localPuzzle.forbiddenWords,
                ),
            ),
        )

        val args = JsonArray().apply {
            add(gson.toJsonTree(roomId))
            add(gson.toJsonTree(payload))
        }
        val response = api.generateVillainSpeechForRoom(
            authorization = authHeader(),
            args = args,
        )
        captureIdentity(response)

        if (!response.isSuccessful) {
            throw ApiException(
                response.code(),
                "Reducer call failed: generate_villain_speech_for_room ${response.errorBody()?.string().orEmpty()}",
            )
        }

        val row = awaitSingleRow(
            "select * from villain_speech_artifact where room_id = '$roomId'",
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

    private fun ensureRoundOneSelection(roomId: String) {
        if (selectedPersona == null) {
            selectedPersona = RoundOneCatalog.selectPersona(roomId)
        }
        if (selectedPuzzle == null) {
            selectedPuzzle = RoundOneCatalog.selectPuzzle(roomId)
        }
    }

    private suspend fun fetchRoomIdFromMyRoomInfo(): String? {
        val response = api.getMyRoomInfo(
            authorization = authHeader(),
            args = JsonArray(),
        )
        captureIdentity(response)

        if (!response.isSuccessful) return null

        val raw = response.body()?.string().orEmpty()
        if (raw.isBlank()) return null

        val parsed = runCatching { gson.fromJson(raw, JsonElement::class.java) }.getOrNull() ?: return null
        return parseRoomIdFromInfo(parsed)
    }

    private fun parseRoomIdFromInfo(data: JsonElement): String? {
        if (data is JsonObject) {
            val roomValue = data.get("room_id")
            if (roomValue != null) {
                if (roomValue.isJsonPrimitive) return roomValue.asString
                if (roomValue is JsonObject && roomValue.get("some")?.isJsonPrimitive == true) {
                    return roomValue.get("some").asString
                }
                if (roomValue is JsonArray && roomValue.size() >= 2 && roomValue[0].isJsonPrimitive && roomValue[0].asInt == 0) {
                    return roomValue[1].takeIf { it.isJsonPrimitive }?.asString
                }
            }
        }
        if (data is JsonArray) {
            data.forEach { element ->
                val nested = parseRoomIdFromInfo(element)
                if (!nested.isNullOrBlank()) return nested
            }
        }
        return null
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
            throw ApiException(
                response.code(),
                "Reducer call failed: $reducerName ${response.errorBody()?.string().orEmpty()}",
            )
        }

        return JsonNull.INSTANCE
    }

    private suspend fun queryRows(sql: String): List<JsonObject> {
        val response = api.querySql(authHeader(), sql)
        captureIdentity(response)

        if (!response.isSuccessful) {
            throw ApiException(response.code(), "SQL query failed ${response.errorBody()?.string().orEmpty()}")
        }

        return (response.body() ?: JsonNull.INSTANCE).extractRows()
    }

    private suspend fun awaitSingleRow(sql: String, timeoutMs: Long): JsonObject? {
        val started = System.currentTimeMillis()
        while (System.currentTimeMillis() - started < timeoutMs) {
            val rows = queryRows(sql)
            val row = rows.lastForCurrentIdentityOrNull() ?: rows.lastOrNull()
            if (row != null) {
                val status = row.readString("status", "gemini_status", "tts_status")?.lowercase()
                if (status == null || status !in PENDING) return row
            }
            delay(400)
        }
        val rows = queryRows(sql)
        return rows.lastForCurrentIdentityOrNull() ?: rows.lastOrNull()
    }

    private suspend fun awaitGameStateRow(roomId: String): JsonObject? {
        return try {
            awaitSingleRow(
                "select * from game_state where room_id = '$roomId'",
                timeoutMs = 8_000L,
            )
        } catch (e: ApiException) {
            val roomScopeMissing = e.httpCode == 400 && e.message.orEmpty().contains("room_id not in scope", ignoreCase = true)
            if (!roomScopeMissing) throw e

            // Some deployments expose game_state without room_id; use latest rows and identity filtering.
            awaitSingleRow(
                "select * from game_state order by updated_at desc limit 20",
                timeoutMs = 8_000L,
            )
        }
    }

    private fun authHeader(): String? = identityToken?.let { "Bearer $it" }

    private fun captureIdentity(response: Response<*>) {
        val token = response.headers()[IDENTITY_HEADER]
        if (!token.isNullOrBlank()) identityToken = token

        val hex = response.headers()[IDENTITY_HEX_HEADER]
        if (!hex.isNullOrBlank()) identityHex = hex.removePrefix("0x").lowercase()
    }

    private fun containsForbiddenLexicon(input: String): Boolean {
        return listOf("kill", "destroy", "murder").any { input.contains(it, ignoreCase = true) }
    }

    private fun JsonElement.extractRows(): List<JsonObject> {
        if (this is JsonArray) {
            // SQL endpoint commonly returns: [{"schema": [...], "rows": [[...], ...]}]
            return flatMap { element ->
                when (element) {
                    is JsonObject -> element.extractRows()
                    else -> emptyList()
                }
            }
        }
        if (this !is JsonObject) return emptyList()

        val sqlRows = get("rows") as? JsonArray
        if (sqlRows != null) {
            val schemaNames = readSchemaNames(this)
            return sqlRows.mapNotNull { rowElement ->
                when (rowElement) {
                    is JsonObject -> rowElement
                    is JsonArray -> rowArrayToObject(rowElement, schemaNames)
                    else -> null
                }
            }
        }

        val candidates = listOf("data", "result", "records")
        val rows = candidates.firstNotNullOfOrNull { key -> get(key) as? JsonArray }
        return rows?.mapNotNull { it as? JsonObject }.orEmpty()
    }

    private fun readSchemaNames(container: JsonObject): List<String> {
        val schemaElement = container.get("schema")
        val schemaItems: JsonArray = when (schemaElement) {
            is JsonArray -> schemaElement
            is JsonObject -> (schemaElement.get("elements") as? JsonArray) ?: JsonArray()
            else -> JsonArray()
        }

        return schemaItems.mapIndexed { idx, entry ->
            when {
                entry is JsonObject && entry.get("name") is JsonObject -> {
                    val nameObj = entry.get("name").asJsonObject
                    nameObj.get("some")?.takeIf { it.isJsonPrimitive }?.asString ?: "c$idx"
                }
                entry is JsonObject && entry.get("name")?.isJsonPrimitive == true -> entry.get("name").asString
                entry is JsonArray && entry.size() > 0 && entry[0].isJsonPrimitive -> entry[0].asString
                else -> "c$idx"
            }
        }
    }

    private fun rowArrayToObject(row: JsonArray, schemaNames: List<String>): JsonObject {
        val obj = JsonObject()
        row.forEachIndexed { index, value ->
            val key = schemaNames.getOrNull(index) ?: "c$index"
            obj.add(key, value)
        }
        return obj
    }

    private fun JsonObject.readString(vararg keys: String): String? {
        keys.forEach { key ->
            val value = get(key) ?: return@forEach
            if (!value.isJsonNull && value.isJsonPrimitive) return value.asString

            // Option/Sum encoding common in SpacetimeDB SQL rows: [0, <value>] == some(<value>)
            if (value is JsonArray && value.size() >= 2) {
                val tag = value[0]
                val payload = value[1]
                if (tag.isJsonPrimitive && tag.asInt == 0 && payload.isJsonPrimitive) {
                    return payload.asString
                }
            }
        }
        return null
    }

    private fun List<JsonObject>.lastForCurrentIdentityOrNull(): JsonObject? {
        val target = identityHex ?: return null
        return lastOrNull { row ->
            val owner = row.readIdentityHex("owner_identity", "host_identity")
            owner != null && owner == target
        }
    }

    private fun JsonObject.readIdentityHex(vararg keys: String): String? {
        keys.forEach { key ->
            val value = get(key) ?: return@forEach

            // Encoded as ["0xc2..."]
            if (value is JsonArray && value.size() > 0 && value[0].isJsonPrimitive) {
                return value[0].asString.removePrefix("0x").lowercase()
            }

            if (value.isJsonPrimitive) {
                return value.asString.removePrefix("0x").lowercase()
            }
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
        private const val IDENTITY_HEX_HEADER = "spacetime-identity"
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
