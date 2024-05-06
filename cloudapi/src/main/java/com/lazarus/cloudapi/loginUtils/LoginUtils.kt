package com.lazarus.cloudapi.loginUtils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection

fun jwtFileManage(context:Context):File{
    val directoryPath = context.filesDir.absolutePath + "/tokens"

    // Check if the directory exists, create it if not
    val directory = File(directoryPath)
    if (!directory.exists()) {
        directory.mkdir()
    }

    val filePath = "$directoryPath/jwt.txt"
    val file = File(filePath)

    // Check if the file exists, delete it if so
    if (file.exists()) {
        file.delete()
    }

    return file
}


fun recieveJWT(connection:HttpURLConnection, file:File):String{
    val inputStream = connection.inputStream
    val reader = InputStreamReader(inputStream);
    val buffer = CharArray(8192)
    var bytesRead: Int
    val stringBuilder = StringBuilder()
    while (reader.read(buffer).also { bytesRead = it } != -1) {
        stringBuilder.appendRange(buffer, 0, bytesRead)
    }
    val jwt = stringBuilder.toString()

    // Write the JWT to a file
    try {
        FileWriter(file).use { writer ->
            writer.write(jwt)
        }
    } catch (e: IOException) {
        Log.d("Error writing JWT to file", "${e.message}")
    }
    Log.d("reponsejwt", jwt)
    return jwt;
}
