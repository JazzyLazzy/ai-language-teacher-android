package com.lazarus.cloudapi

internal val ip = "http://10.0.0.231:3000"

fun getTtsUrl():String{
    return "$ip/api/tts"
}

fun getGPTUrl():String{
    return "$ip/api/sendText"
}

fun getSpeechUrl():String{
    return "ws://10.0.0.231:3000/api/speechInput"
}

fun getLoginUrl():String{
    return "$ip/loginUser"
}

fun getCreateUrl():String{
    return "$ip/createUser"
}

fun getQuickUrl():String{
    return "$ip/quickConnect"
}

fun getGetChatListUrl():String{
    return "$ip/api/getChatList"
}

fun getReadCHUrl():String{
    return "$ip/api/readChatHistory"
}

val deleteChatUrl = "$ip/api/deleteChat"