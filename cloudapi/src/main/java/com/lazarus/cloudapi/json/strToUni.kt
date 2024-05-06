package com.lazarus.cloudapi.json

import android.util.Log

fun strToUni(input: String?):String{
    val unicodeBuilder = StringBuilder()
    //Log.d("json wash", str!!)
    if (input != null){
        Log.d("json wash", input!!)
        for (char in input.toCharArray()) {
            unicodeBuilder.append("\\u").append(String.format("%04x", char.code))
        }
    }
    return unicodeBuilder.toString()
}