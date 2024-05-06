package com.lazarus.cloudapi

import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import kotlinx.serialization.encodeToString
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.URL
import java.util.regex.Pattern

fun getChatList(authToken:String, language:String):List<ReceivedChatListJSON>{
    val chatListEndpoint = URL(getGetChatListUrl());
    val connection = chatListEndpoint.openConnection() as HttpURLConnection
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Authorization", "Bearer $authToken")
    connection.requestMethod = "POST"
    connection.doInput = true;
    connection.setDoOutput(true)
    val out = OutputStreamWriter(
        connection.outputStream
    )

    val getChatListJSONButReallyOnlyLangField = GetChatListJSONButReallyOnlyLangField(language)

    val lang_input = Json.encodeToString(getChatListJSONButReallyOnlyLangField)
    Log.d("Language Hist to get Input:",  lang_input)
    out.write(lang_input)
    out.close()

    val responseCode = connection.responseCode

    when(responseCode) {
        in 200 until 300 -> {
            val inputStream: InputStream = connection.inputStream;
            // Create a reader for the input stream
            val reader = InputStreamReader(inputStream)

            // Use a buffer to read the input stream
            val buffer = CharArray(8192)
            var bytesRead: Int

            val stringBuilder = StringBuilder()
            while (reader.read(buffer).also { bytesRead = it } != -1) {
                stringBuilder.appendRange(buffer, 0, bytesRead)
            }

            val chat_idString = stringBuilder.toString()
            val chat_id = Json.decodeFromString<List<ReceivedChatListJSON>>(chat_idString);

            return chat_id;
        }
    }
    return emptyList()
}

@Serializable
data class GetChatListJSONButReallyOnlyLangField (
    val lang:String
)

@Serializable
data class ReceivedChatListJSON (
    val chat_id:Long,
    val name:String? = null
)