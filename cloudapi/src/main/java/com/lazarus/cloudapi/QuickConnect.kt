package com.lazarus.cloudapi

import android.content.Context
import android.util.Log
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

fun quickConnect(context:Context):String?{
    val directoryPath = context.filesDir.absolutePath + "/tokens"

    Log.d("QC", "actiivated")
    val directory = File(directoryPath)
    if (!directory.exists()) {
        return null
    }

    val filePath = "$directoryPath/jwt.txt"
    val file = File(filePath)

    if (file.exists()) {
        val fileContents: String = try {
            file.readText()
        } catch (e: IOException) {
            Log.d("Could not read", "ioexception")
            return null
        }

        val quickURL = URL(getQuickUrl());
        val quickConnection = quickURL.openConnection() as HttpURLConnection

        Log.d("file content", fileContents)
        val authToken = "Bearer $fileContents"
        quickConnection.setRequestProperty("Content-Type", "application/json")
        quickConnection.setRequestProperty("Authorization", authToken)
        quickConnection.requestMethod = "POST"
        quickConnection.doInput = true;
        quickConnection.setDoOutput(true)
        val quickOut = OutputStreamWriter(
            quickConnection.outputStream
        )
        quickOut.close()

        val responseCode = quickConnection.responseCode
        return when (responseCode) {
            200 -> {
                //write the jwt to file and return it
                fileContents
            }

            401 -> {
                "401"
            }

            else -> {
                throw Error()
            }
        }


    } else {
        return null
    }
}