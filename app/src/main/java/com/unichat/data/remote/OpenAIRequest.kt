package com.unichat.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIRequest(
    val model: String = "local",
    val messages: List<Message>,
    val temperature: Double = 0.7,
    @SerialName("max_tokens")
    val maxTokens: Int = 2048,
    val stream: Boolean = false
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class OpenAIResponse(
    val id: String? = null,
    val model: String? = null,
    val choices: List<Choice>? = null,
    val usage: Usage? = null
)

@Serializable
data class Choice(
    val index: Int? = null,
    val message: Message? = null,
    @SerialName("finish_reason")
    val finishReason: String? = null
)

@Serializable
data class Usage(
    @SerialName("prompt_tokens")
    val promptTokens: Int? = null,
    @SerialName("completion_tokens")
    val completionTokens: Int? = null,
    @SerialName("total_tokens")
    val totalTokens: Int? = null
)
