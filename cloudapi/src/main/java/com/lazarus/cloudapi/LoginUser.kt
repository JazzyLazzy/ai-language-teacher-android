package com.lazarus.cloudapi

import android.content.Context
import android.util.Log
import com.lazarus.cloudapi.loginUtils.jwtFileManage
import com.lazarus.cloudapi.loginUtils.recieveJWT
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

fun loginUser(email:String, password:String, context: Context):String{

    // Handle creation/refreshing JWT file
    val file = jwtFileManage(context)

    val loginURL = URL(getLoginUrl());
    val loginConnection = loginURL.openConnection() as HttpURLConnection

    loginConnection.setRequestProperty("Content-Type", "application/json")
    loginConnection.requestMethod = "POST"
    loginConnection.doInput = true;
    loginConnection.setDoOutput(true)
    val loginOut = OutputStreamWriter(
        loginConnection.outputStream
    )
    loginOut.write("{\"email\":\"$email\", \"password\": \"$password\"}")
    loginOut.close()

    val reponseCode = loginConnection.responseCode

    when (reponseCode) {
        in 200 until 300 -> {
            //write the jwt to file and return it
            return recieveJWT(loginConnection, file)
        }
        401 -> {
            return "401";
        }
        503 -> {
            return "503";
        }
        else -> {
            throw Error();
        }
    }
}