package com.lazarus.cloudapi

import android.util.Log
import com.lazarus.cloudapi.json.GPTResponse
import com.lazarus.cloudapi.json.strToUni
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun sendText(textContent:String, chat_id:Long?, lang:String, newChat:Boolean?, gptListener: GPTListener, authToken:String){
    //val stringToReverse = URLEncoder.encode(, "UTF-8")
    val textPayload = TextPayload(
        text = textContent,
        chat_id = chat_id,
        lang = lang,
        newChat = newChat
    )

    val txtPyldJsonStr = Json.encodeToString(textPayload)

    val url = URL(getGPTUrl())
    val connection = url.openConnection() as HttpURLConnection
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Authorization", "Bearer $authToken")
    connection.requestMethod = "POST"
    connection.doInput = true;
    connection.setDoOutput(true)
    val out = OutputStreamWriter(
        connection.outputStream
    )
    Log.d("GPT Input:", txtPyldJsonStr)
    out.write(txtPyldJsonStr)
    out.close()
    // Check the HTTP response code
    val responseCode = connection.responseCode

// Check if the response code is in the successful range (200 - 299)
    if (responseCode in 200 until 300) {
        val inputStream: InputStream = connection.inputStream

        // Create a reader for the input stream
        val reader = InputStreamReader(inputStream)

        // Use a buffer to read the input stream
        val buffer = CharArray(8192)
        var bytesRead: Int

        val pattern = Pattern.compile("\n")

        // Read the input stream and print the content
        while (reader.read(buffer).also { bytesRead = it } != -1) {
            val decodedString = String(buffer, 0, bytesRead)
            val jsonObjects = pattern.split(decodedString)
            Log.d("GPT Response:", jsonObjects[0].toString())
            for (jsonObject in jsonObjects){
                if (jsonObject == "Waiting...") {
                    gptListener.onWaiting(jsonObject)
                } else if (Json.decodeFromString<GPTResponse>(jsonObject).content != null) {
                    val gptResponse = Json.decodeFromString<GPTResponse>(jsonObject);
                    gptListener.onReceived(gptResponse.content);
                } else {
                    val gptResponse = Json.decodeFromString<GPTResponse>(jsonObject);
                    gptListener.onChat_Id(gptResponse.chat_id!!)
                }
            }
        }
        // Close the input stream and connection
        inputStream.close()
        gptListener.onSuccessFinish()
    } else if (responseCode == 401) {
        gptListener.onUnauthorisedAccess()
    } else {
        // Handle the error based on the response code
        Log.e("HTTP Error", "Received HTTP error response: $responseCode")
        // You might want to inform the listener about the error, e.g., gptListener.onError(responseCode)
    }
    connection.disconnect()
}

@Serializable
data class TextPayload(
    val lang: String,
    val text: String,
    @SerialName("newChat")
    val newChat: Boolean? = null,
    @SerialName("chat_id")
    val chat_id: Long? = null
)
