package com.lazarus.cloudapi

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.lazarus.cloudapi.json.GPTResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Request
import okhttp3.WebSocketListener
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import okio.ByteString
import okio.ByteString.Companion.encodeUtf8
import okio.ByteString.Companion.toByteString
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

private const val TAG = "AudioCaptureTask"
private const val SAMPLE_RATE = 16000
private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
private const val BIT_RATE = 128
//private val lameEncoder = LameEncoder(SAMPLE_RATE, CHANNEL_CONFIG, BIT_RATE)

data class OutputStreamHttpURLConnection(
    val outputStream: OutputStream?,
    val connection:HttpURLConnection?
)

fun initialiseWebSocket(webSocketListener: WebSocketListener) {
    //val endpoint = "ws://10.0.0.231:3000/speechInput" // Replace with your actual WebSocket endpoint
    val endpoint = "wss://aztest.azurewebsites.net/speechInput"
    val client = OkHttpClient.Builder()
        .readTimeout(20000, TimeUnit.MILLISECONDS) // Set your desired read timeout
        .build()

    val request = Request.Builder()
        .url(getSpeechUrl())
        .build()

    client.newWebSocket(request, webSocketListener)

}

suspend fun generateOutputStream():OutputStreamHttpURLConnection? = withContext(Dispatchers.IO){
    //val url = URL("https://aztest.azurewebsites.net/api/speechInput")
    val url = URL(getSpeechUrl())
    val connection = url.openConnection() as HttpURLConnection

    try {
        connection.doOutput = true
        connection.requestMethod = "POST"
        connection.setChunkedStreamingMode(1024); 
        connection.setRequestProperty("Content-Type", "application/octet-stream")

        OutputStreamHttpURLConnection(
            DataOutputStream(connection.outputStream),
            connection
        )
    } catch (error:Exception){
        Log.d("Stt:", error.message.toString())
        null;
    }
}

suspend fun startAudioRecord(audioRecord: AudioRecord, webSocket: WebSocket, jsString: GetChatListJSONButReallyOnlyLangField, outputFile: File) = withContext(Dispatchers.IO){
    Log.d("Stt:", "Audio Record Start")
    val jsonString = Json.encodeToString(jsString)
    try {
        val byteString = jsonString.encodeUtf8()
        webSocket.send(byteString)
        Log.d("Stt:", "Output stream Write Start")
        if (audioRecord.state == AudioRecord.STATE_INITIALIZED) {
            val outputStream = FileOutputStream(outputFile)
            audioRecord.startRecording()
            val buffer = ByteArray(BUFFER_SIZE)
            while (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                val bytesRead = audioRecord.read(buffer, 0, buffer.size)
                if (!webSocket.send(buffer.toByteString(0, bytesRead))) {
                    break
                }
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.close()
            Log.d("Stt:", "Audio Record Stopped");
            //AudioInputStream()
        } else {
            Log.e(TAG, "AudioRecord initialization failed")
            null
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error during audio capture: ${e.message}")
        null
    }
}

suspend fun startAudioRecording(audioRecord: AudioRecord, outputStream: OutputStream?, jsonString:String) = withContext(Dispatchers.IO) {
    Log.d("Stt:", "Audio Record Start")
    try {
        val byteArray = jsonString.toByteArray(Charsets.UTF_8)
        outputStream?.write(byteArray);
        Log.d("Stt:", "Output stream Write Start")
        if (audioRecord.state == AudioRecord.STATE_INITIALIZED) {
            audioRecord.startRecording()
            val buffer = ByteArray(BUFFER_SIZE)
            while (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                val bytesRead = audioRecord.read(buffer, 0, buffer.size)
                outputStream?.write(buffer, 0, bytesRead);
            }
            Log.d("Stt:", "Audio Record Stopped");
            //AudioInputStream()
        } else {
            Log.e(TAG, "AudioRecord initialization failed")
            null
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error during audio capture: ${e.message}")
        null
    }
}

suspend fun getTextResult(connection: HttpURLConnection?, textListener: TextListener) = withContext(Dispatchers.IO){
    Log.d("Stt:", "getting results");
    val inputStream = connection?.inputStream;
    Log.d("Stt:", "getting input stream");
    val reader = InputStreamReader(inputStream)
    val buffer = CharArray(1024)
    var bytesRead: Int

    val responseCode = connection?.responseCode
    Log.d("Stt:", responseCode.toString())
    Log.d("Stt:", connection?.responseMessage.toString())

    while (reader.read(buffer).also { bytesRead = it } != -1) {
        val decodedString = String(buffer, 0, bytesRead)
        textListener.onReceived(decodedString);
    }
}

suspend fun stopAudioRecord(audioRecord: AudioRecord, webSocket: WebSocket) = withContext(Dispatchers.IO){
    Log.d("Stt:", "Audio Record Stop2")
    audioRecord.apply {
        stop()
        release()
    }
    webSocket.send("RecordingStopped");
}

suspend fun stopAudioRecording(audioRecord: AudioRecord, outputStreamHttpURLConnection: OutputStreamHttpURLConnection?) = withContext(Dispatchers.IO) {
    Log.d("Stt:", "Audio Record Stop")
    val outputStream = outputStreamHttpURLConnection?.outputStream
    val connection = outputStreamHttpURLConnection?.connection
    audioRecord.apply {
        stop()
        release()
    }
    outputStream?.flush()
    outputStream?.close()
    val responseCode = connection?.responseCode
    Log.d("Stt:", responseCode.toString())
    Log.d("Stt:", connection?.responseMessage.toString())
    connection?.disconnect()
}

class AudioInputStream : InputStream() {
    private val outputStream = ByteArrayOutputStream()

    override fun read(): Int {
        return if (outputStream.toByteArray().isNotEmpty()) outputStream.toByteArray()[0].toInt() else -1
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        val data = outputStream.toByteArray()
        val bytesToRead = minOf(length, data.size)
        System.arraycopy(data, 0, buffer, offset, bytesToRead)
        outputStream.reset() // Clear the buffer after reading
        return bytesToRead
    }

    fun append(audioData: ByteArray) {
        outputStream.write(audioData, 0, audioData.size)
    }
}

@SuppressLint("MissingPermission")
fun usineAudioRecord():AudioRecord{
    return AudioRecord(
        MediaRecorder.AudioSource.MIC,
        SAMPLE_RATE,
        CHANNEL_CONFIG,
        AUDIO_FORMAT,
        BUFFER_SIZE
    )
}