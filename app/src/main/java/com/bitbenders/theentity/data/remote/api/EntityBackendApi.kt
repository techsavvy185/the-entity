package com.bitbenders.theentity.data.remote.api

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.POST

/**
 * Retrofit service interface for SpacetimeDB Maincloud reducers.
 *
 * Base URL is injected by the
 * [com.bitbenders.theentity.di.NetworkModule].
 */
interface EntityBackendApi {
    @POST("call/{reducerName}")
    suspend fun callReducer(
        @Path("reducerName") reducerName: String,
        @Header("Authorization") authorization: String?,
        @Body args: JsonArray,
    ): Response<JsonElement>

    @POST("sql")
    suspend fun querySql(
        @Header("Authorization") authorization: String?,
        @Body sql: String,
    ): Response<JsonElement>
}
