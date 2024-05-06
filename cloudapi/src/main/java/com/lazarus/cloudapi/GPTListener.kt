package com.lazarus.cloudapi

interface GPTListener {

    var waiting: Boolean;

    //Called everytime response is streamed in
    fun onReceived(decodedString: String?)

    //Called when not 503 server error
    fun onWaiting(decodedString: String)

    fun onSuccessFinish()

    fun onUnauthorisedAccess()

    fun onChat_Id(chat_id:Long)
}