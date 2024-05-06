package com.lazarus.cloudapi.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GPTResponse(
    @SerialName("content") val content: String? = null,
    @SerialName("pinyin") val pinyin:String? = null,
    @SerialName("chat_id") val chat_id:Long? = null
)
