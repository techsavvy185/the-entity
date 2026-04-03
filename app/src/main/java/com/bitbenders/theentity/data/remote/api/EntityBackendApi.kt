package com.bitbenders.theentity.data.remote.api

import com.bitbenders.theentity.data.remote.dto.BackendRoundStateDto
import com.bitbenders.theentity.data.remote.dto.PersonaConfigDto
import com.bitbenders.theentity.data.remote.dto.PromptEvaluationDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class PromptRequestDto(
    val prompt: String,
)

interface EntityBackendApi {
    @POST("rounds/{roundNumber}/start")
    suspend fun startRound(
        @Path("roundNumber") roundNumber: Int,
    ): BackendRoundStateDto

    @POST("prompts/submit")
    suspend fun submitPrompt(
        @Body request: PromptRequestDto,
    ): PromptEvaluationDto

    @GET("persona/next")
    suspend fun fetchNextPersona(): PersonaConfigDto
}

