package com.bitbenders.theentity.data.remote.dto

import com.google.gson.annotations.SerializedName

// ─── POST /api/gemini/clue-generator ─────────────────────────────────────────

/**
 * Request body sent to the Gemini clue-generator endpoint.
 */
data class ClueGeneratorRequestDto(
    @SerializedName("setting") val setting: String,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("theme") val theme: String,
    @SerializedName("villain_name") val villainName: String,
    @SerializedName("objective") val objective: String,
)

/**
 * The full game package returned by the clue-generator.
 */
data class ClueGeneratorResponseDto(
    @SerializedName("game_title") val gameTitle: String,
    @SerializedName("setting_summary") val settingSummary: String,
    @SerializedName("shared_manual_intro") val sharedManualIntro: String,
    @SerializedName("round_1") val round1: Round1Dto,
    @SerializedName("round_2") val round2: Round2Dto,
    @SerializedName("round_3_native_brief") val round3NativeBrief: Round3NativeBriefDto,
)

// ─── Round sub-objects ───────────────────────────────────────────────────────

/**
 * Round 1 – The Persona Trap.
 * Contains the persona the AI imitates, the target word P1 must elicit,
 * and the list of words P1 must avoid.
 */
data class Round1Dto(
    @SerializedName("persona") val persona: String,
    @SerializedName("target_word") val targetWord: String,
    @SerializedName("forbidden_words") val forbiddenWords: List<String>,
    @SerializedName("dialogue") val dialogue: String,
)

/**
 * Round 2 – The Post-Mortem Logs.
 * Contains corrupted incident logs from previous victims.
 */
data class Round2Dto(
    @SerializedName("incident_logs") val incidentLogs: List<IncidentLogDto>,
    @SerializedName("subject_id") val subjectId: String,
)

data class IncidentLogDto(
    @SerializedName("victim_name") val victimName: String,
    @SerializedName("cause_of_death") val causeOfDeath: String,
    @SerializedName("log_text") val logText: String,
)

/**
 * Round 3 – Hostile Lexical Calibration (native brief).
 * Contains the homophone grid entries and calibration key.
 */
data class Round3NativeBriefDto(
    @SerializedName("homophones") val homophones: List<String>,
    @SerializedName("calibration_key") val calibrationKey: String,
    @SerializedName("correct_word") val correctWord: String,
)
