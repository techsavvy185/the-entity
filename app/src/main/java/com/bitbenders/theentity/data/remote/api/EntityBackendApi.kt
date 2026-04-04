package com.bitbenders.theentity.data.remote.api

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.POST

/**
 * Retrofit service interface for SpacetimeDB Maincloud reducers.
 *
 * Base URL is injected by the
 * [com.bitbenders.theentity.di.NetworkModule].
 */
interface EntityBackendApi {
    // Generic reducer caller (kept for flexibility).
    @POST("call/{reducerName}")
    suspend fun callReducer(
        @Path("reducerName") reducerName: String,
        @Header("Authorization") authorization: String?,
        @Body args: JsonArray,
    ): Response<ResponseBody>

    // Public game/room reducers.
    @Headers("Content-Type: application/json")
    @POST("call/initiate_room")
    suspend fun initiateRoom(
        @Header("Authorization") authorization: String?,
        @Body args: JsonArray,
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("call/join_room")
    suspend fun joinRoom(
        @Header("Authorization") authorization: String?,
        @Body args: JsonArray,
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("call/get_my_room_info")
    suspend fun getMyRoomInfo(
        @Header("Authorization") authorization: String?,
        @Body args: JsonArray,
    ): Response<ResponseBody>

    @POST("call/terminate_room")
    suspend fun terminateRoom(
        @Header("Authorization") authorization: String?,
        @Body args: JsonArray,
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("call/terminate_room_for_testing")
    suspend fun terminateRoomForTesting(
        @Header("Authorization") authorization: String?,
        @Body args: JsonArray,
    ): Response<ResponseBody>

    @POST("call/submit_terminal")
    suspend fun submitTerminal(
        @Header("Authorization") authorization: String?,
        @Body args: JsonArray,
    ): Response<ResponseBody>

    @POST("call/submit_terminal_for_room")
    suspend fun submitTerminalForRoom(
        @Header("Authorization") authorization: String?,
        @Body args: JsonArray,
    ): Response<ResponseBody>

    // Generation reducers.
    @POST("call/generate_clue_manual_for_room")
    suspend fun generateClueManualForRoom(
        @Header("Authorization") authorization: String?,
        @Body args: JsonArray,
    ): Response<ResponseBody>

    @POST("call/generate_villain_speech_for_room")
    suspend fun generateVillainSpeechForRoom(
        @Header("Authorization") authorization: String?,
        @Body args: JsonArray,
    ): Response<ResponseBody>

    // Admin/setup reducers.
    @POST("call/configure_integrations")
    suspend fun configureIntegrations(
        @Header("Authorization") authorization: String?,
        @Body args: JsonArray,
    ): Response<ResponseBody>

    @POST("call/configure_voice_integrations")
    suspend fun configureVoiceIntegrations(
        @Header("Authorization") authorization: String?,
        @Body args: JsonArray,
    ): Response<ResponseBody>

    @POST("call/set_hidden_answer")
    suspend fun setHiddenAnswer(
        @Header("Authorization") authorization: String?,
        @Body args: JsonArray,
    ): Response<ResponseBody>

    @POST("call/set_hidden_answer_for_room")
    suspend fun setHiddenAnswerForRoom(
        @Header("Authorization") authorization: String?,
        @Body args: JsonArray,
    ): Response<ResponseBody>

    @POST("call/configure_local_dev_integrations")
    suspend fun configureLocalDevIntegrations(
        @Header("Authorization") authorization: String?,
        @Body args: JsonArray,
    ): Response<ResponseBody>

    @Headers("Content-Type: text/plain")
    @POST("sql")
    suspend fun querySql(
        @Header("Authorization") authorization: String?,
        @Body sql: String,
    ): Response<JsonElement>
}
