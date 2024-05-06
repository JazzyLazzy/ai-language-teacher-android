package com.lazarus.cloudapi

import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

fun deleteChat(authToken:String, chat_id:Long):Boolean{

    val jsonpyld = JSONJustMakesEveryoneLifesEasier(chat_id);
    val jsontostrpyld = Json.encodeToString(jsonpyld)

    val url = URL(deleteChatUrl);
    val connection = url.openConnection() as HttpURLConnection
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Authorization", "Bearer $authToken")
    connection.requestMethod = "POST"
    connection.doInput = true;
    connection.setDoOutput(true)
    val out = OutputStreamWriter(
        connection.outputStream
    )
    out.write(jsontostrpyld)
    out.close()

    val responseCode = connection.responseCode

    return responseCode in 200 until 300
}

@Serializable
internal data class JSONJustMakesEveryoneLifesEasier(
    val chat_id: Long
)