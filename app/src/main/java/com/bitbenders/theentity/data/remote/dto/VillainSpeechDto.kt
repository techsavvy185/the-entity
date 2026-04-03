package com.bitbenders.theentity.data.remote.dto

import com.google.gson.annotations.SerializedName

// ─── POST /api/villain/speech ────────────────────────────────────────────────

/**
 * Request body for generating villain speech lines + TTS audio.
 */
data class VillainSpeechRequestDto(
    @SerializedName("villain_name") val villainName: String,
    @SerializedName("scene") val scene: String,
    @SerializedName("tone") val tone: String,
    @SerializedName("selected_cue_id") val selectedCueId: String? = null,
    @SerializedName("voice_id") val voiceId: String? = null,
)

/**
 * Response body containing villain speech cues and optional TTS audio.
 */
data class VillainSpeechResponseDto(
    @SerializedName("speech_cues") val speechCues: List<SpeechCueDto>,
    @SerializedName("selected_cue_id") val selectedCueId: String,
    @SerializedName("audio_base64") val audioBase64: String?,
    @SerializedName("mime_type") val mimeType: String?,
    @SerializedName("tts_provider") val ttsProvider: String?,
)

data class SpeechCueDto(
    @SerializedName("cue_id") val cueId: String,
    @SerializedName("text") val text: String,
)
