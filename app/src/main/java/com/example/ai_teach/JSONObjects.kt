package com.example.ai_teach

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Message(
    @SerialName("role") val role: String,
    @SerialName("content") val content: String
)


@Serializable
data class GPTResponse(
    @SerialName("index") val index: Int,
    @SerialName("message") val message: Message,
    @SerialName("finish_reason") val finishReason: String
)

