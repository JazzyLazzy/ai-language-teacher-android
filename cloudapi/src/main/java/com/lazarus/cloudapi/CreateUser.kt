package com.lazarus.cloudapi

import android.content.Context
import android.util.Log
import com.lazarus.cloudapi.loginUtils.jwtFileManage
import com.lazarus.cloudapi.loginUtils.recieveJWT
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

fun createUser(email: String, password:String, context: Context):String{

    // Handle creation/refreshing JWT file
    val file = jwtFileManage(context)

    val createURL = URL(getCreateUrl());
    val createConnection = createURL.openConnection() as HttpURLConnection

    createConnection.setRequestProperty("Content-Type", "application/json")
    createConnection.requestMethod = "POST"
    createConnection.doInput = true;
    createConnection.setDoOutput(true)
    val createOut = OutputStreamWriter(
        createConnection.outputStream
    )
    createOut.write("{\"email\":\"$email\", \"password\": \"$password\"}")
    createOut.close()

    val reponseCode = createConnection.responseCode

    when (reponseCode) {
        in 200 until 300 -> {
            //write the jwt to file and return it
            return recieveJWT(createConnection, file)
        }
        403 -> {
            return "403";
        }
        503 -> {
            return "503";
        }
        else -> {
            throw Error();
        }
    }

}