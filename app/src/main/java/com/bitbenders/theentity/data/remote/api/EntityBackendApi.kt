package com.bitbenders.theentity.data.remote.api

import com.bitbenders.theentity.data.remote.dto.ArmorIqVerifyRequestDto
import com.bitbenders.theentity.data.remote.dto.ArmorIqVerifyResponseDto
import com.bitbenders.theentity.data.remote.dto.ClueGeneratorRequestDto
import com.bitbenders.theentity.data.remote.dto.ClueGeneratorResponseDto
import com.bitbenders.theentity.data.remote.dto.HealthResponseDto
import com.bitbenders.theentity.data.remote.dto.TerminalValidatorRequestDto
import com.bitbenders.theentity.data.remote.dto.TerminalValidatorResponseDto
import com.bitbenders.theentity.data.remote.dto.VillainSpeechRequestDto
import com.bitbenders.theentity.data.remote.dto.VillainSpeechResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit service interface for the Entity localhost relay (server.js).
 *
 * All endpoints target the local HTTP relay that sits in front of
 * ArmorIQ, Gemini, and ElevenLabs. Base URL is injected by the
 * [com.bitbenders.theentity.di.NetworkModule].
 */
interface EntityBackendApi {

    // ─── Health ──────────────────────────────────────────────────────────────

    /**
     * GET /health
     *
     * Quick health check. Returns relay status, mock-mode flag,
     * and the list of supported routes.
     */
    @GET("health")
    suspend fun getHealth(): Response<HealthResponseDto>

    // ─── ArmorIQ ─────────────────────────────────────────────────────────────

    /**
     * POST /api/armoriq/verify
     *
     * Validates Player 1's terminal input against ArmorIQ guardrails.
     * In mock mode the relay returns a local simulated decision;
     * in real mode it forwards to the configured ArmorIQ upstream.
     */
    @POST("api/armoriq/verify")
    suspend fun verifyArmorIq(
        @Body request: ArmorIqVerifyRequestDto,
    ): Response<ArmorIqVerifyResponseDto>

    // ─── Gemini – Clue Generator ─────────────────────────────────────────────

    /**
     * POST /api/gemini/clue-generator
     *
     * Generates the complete multi-round game package:
     * game title, setting summary, shared manual intro,
     * and per-round data (round_1 … round_4_native_brief).
     */
    @POST("api/gemini/clue-generator")
    suspend fun generateClues(
        @Body request: ClueGeneratorRequestDto,
    ): Response<ClueGeneratorResponseDto>

    // ─── Gemini – Terminal Validator ──────────────────────────────────────────

    /**
     * POST /api/gemini/terminal-validator
     *
     * Strict kill-phrase validator. Returns [success] and a
     * deliberately non-leaky [reason].
     */
    @POST("api/gemini/terminal-validator")
    suspend fun validateTerminal(
        @Body request: TerminalValidatorRequestDto,
    ): Response<TerminalValidatorResponseDto>

    // ─── Villain Speech ──────────────────────────────────────────────────────

    /**
     * POST /api/villain/speech
     *
     * Generates villain voice cues aligned to clue reveals,
     * picks one for TTS, and optionally synthesizes it via ElevenLabs.
     */
    @POST("api/villain/speech")
    suspend fun generateVillainSpeech(
        @Body request: VillainSpeechRequestDto,
    ): Response<VillainSpeechResponseDto>
}
