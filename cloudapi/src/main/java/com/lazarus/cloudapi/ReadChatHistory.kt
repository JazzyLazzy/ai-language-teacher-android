package com.lazarus.cloudapi

import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.Serial
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

fun readChatHistory(authToken:String, chat_id:Long):HistoryName{

    val chpayload = ChatHistoryJSONinput(chat_id)

    val url = URL(getReadCHUrl())
    val connection = url.openConnection() as HttpURLConnection
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Authorization", "Bearer $authToken")
    connection.requestMethod = "POST"
    connection.doInput = true;
    connection.setDoOutput(true)
    val out = OutputStreamWriter(
        connection.outputStream
    )
    val chpayloadjson = Json.encodeToString(chpayload)
    Log.d("CH input:", chpayloadjson)
    out.write(chpayloadjson)
    out.close()

    val responseCode = connection.responseCode;

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

            val chatHistoryStr = stringBuilder.toString()
            val historyName = Json.decodeFromString<HistoryName>(chatHistoryStr);

            return historyName;
        }
    }
    return HistoryName();
}

@Serializable
data class ChatHistoryJSONinput (
    val chat_id: Long
)

@Serializable
data class HistoryName (
    val content:List<ChatHistoryObj> = emptyList(),
    val name:String? = null
)

@Serializable
data class ChatHistoryObj (
    val role:String,
    val content:String
)