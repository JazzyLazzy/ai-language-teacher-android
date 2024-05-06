package com.lazarus.cloudapi

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.util.Log
import com.lazarus.cloudapi.json.strToUni
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.experimental.and

@Throws(IOException::class)
private fun writeWavHeader(output: FileOutputStream, dataSize: Long) {
    val channels = 1
    val sampleRate = 16000
    val bitsPerSample = 16
    val byteRate = sampleRate * channels * bitsPerSample / 8
    val blockAlign = channels * bitsPerSample / 8

    // Write WAV header
    output.write("RIFF".toByteArray())
    output.write(intToByteArray((36 + dataSize).toInt()), 0, 4)
    output.write("WAVE".toByteArray())
    output.write("fmt ".toByteArray())
    output.write(intToByteArray(16), 0, 4) // Sub-chunk size (16 for PCM)
    output.write(shortToByteArray(1), 0, 2) // Audio format (1 for PCM)
    output.write(shortToByteArray(channels.toShort()), 0, 2)
    output.write(intToByteArray(sampleRate), 0, 4)
    output.write(intToByteArray(byteRate), 0, 4)
    output.write(shortToByteArray(blockAlign.toShort()), 0, 2)
    output.write(shortToByteArray(bitsPerSample.toShort()), 0, 2)
    output.write("data".toByteArray())
    output.write(intToByteArray(dataSize.toInt()), 0, 4)
}

private fun intToByteArray(value: Int): ByteArray {
    val byteArray = ByteArray(4)
    byteArray[0] = (value and 0xFF).toByte()
    byteArray[1] = (value shr 8 and 0xFF).toByte()
    byteArray[2] = (value shr 16 and 0xFF).toByte()
    byteArray[3] = (value shr 24 and 0xFF).toByte()
    return byteArray
}

private fun shortToByteArray(value: Short): ByteArray {
    val byteArray = ByteArray(2)
    byteArray[0] = (value and 0xFF).toByte()
    byteArray[1] = (value.toInt() shr 8 and 0xFF).toByte()
    return byteArray
}

@Throws(Exception::class)
fun listen(str: String, lang:String, context: Context, listenRate:String, authToken:String, mediaPlayer: MediaPlayer, fileName: String) {

    val outputFile = File(context.filesDir.absolutePath, fileName)

    //Create new file, otherwise, reuse existing.
    if (!outputFile.exists()) {
        Log.d("json wash", str)
        val text = strToUni(str);

        val audioURL = URL(getTtsUrl())
        val audioConnection = audioURL.openConnection() as HttpURLConnection

        audioConnection.setRequestProperty("Content-Type", "application/json")
        audioConnection.setRequestProperty("Authorization", "Bearer $authToken")
        audioConnection.requestMethod = "POST"
        audioConnection.doInput = true;
        audioConnection.setDoOutput(true)
        val audioOut = OutputStreamWriter(
            audioConnection.outputStream
        )
        Log.d("tts:", "$text")
        audioOut.write("{\"text\":\"$text\", \"lang\": \"$lang\", \"prosody-rate\":\"$listenRate\"}")
        audioOut.close()

        // Read the response as a byte array
        val inputStream = audioConnection.inputStream
        val buffer = ByteArray(8)
        var bytesRead: Int
        val byteArrayOutputStream = ByteArrayOutputStream()
        val audioData: ByteArray = byteArrayOutputStream.toByteArray()

        val responseCode = audioConnection.responseCode

        outputFile.createNewFile()
        // Check if the response code is in the successful range (200 - 299)
        if (responseCode in 200 until 300) {
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead)
            }

            try {
                val outputStream = FileOutputStream(outputFile)
                outputStream.write(byteArrayOutputStream.toByteArray())
                outputStream.close()
                Log.d("tts:", "success creating mp3")
            } catch (e: Exception) {
                Log.d("tts:", "problem creating mp3")
                e.printStackTrace()
            }

            inputStream.close()
        }
        audioConnection.disconnect()
    }

    try {
        mediaPlayer.setDataSource(outputFile.absolutePath)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }catch(e:Exception){
        Log.d("tts:", "Error in media player")
    }
}