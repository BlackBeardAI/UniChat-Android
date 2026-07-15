package com.unichat.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LlmApiService {
    @GET("models")
    suspend fun getModels(): ModelListResponse

    @POST("chat/completions")
    suspend fun chatCompletion(@Body request: OpenAIRequest): OpenAIResponse
}

@kotlinx.serialization.Serializable
data class ModelListResponse(
    val `data`: List<ModelInfo>? = null
)

@kotlinx.serialization.Serializable
data class ModelInfo(
    val id: String,
    val `object`: String? = null
)
