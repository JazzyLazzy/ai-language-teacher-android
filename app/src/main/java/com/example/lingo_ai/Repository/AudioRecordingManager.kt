package com.example.lingo_ai.Repository

import android.content.Context
import android.media.AudioRecord
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.lazarus.cloudapi.GetChatListJSONButReallyOnlyLangField
import com.lazarus.cloudapi.initialiseWebSocket
import com.lazarus.cloudapi.startAudioRecord
import com.lazarus.cloudapi.stopAudioRecord
import com.lazarus.cloudapi.usineAudioRecord
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AudioRecordingManagerModule {

    @Provides
    @Singleton
    fun provideAudioRecordingManager(@ApplicationContext context: Context):AudioRecordingManager{
        return AudioRecordingManager(context)
    }

}

class AudioRecordingManager @Inject constructor (private val context: Context) {

    lateinit var webSocket: WebSocket;
    private lateinit var audioRecord: AudioRecord;
    private var completeText = "";

    suspend fun recordAudio(webSocketListener: WebSocketListener){
        println("begin audio recording")
        CoroutineScope(Dispatchers.IO).launch {
            //outputStreamHttpURLConnection = generateOutputStream()
            audioRecord = usineAudioRecord()
            completeText = "";
            initialiseWebSocket(webSocketListener)
        }
    }

    suspend fun startAudioRecordStream(webSocket: WebSocket,
            langJsonStr:GetChatListJSONButReallyOnlyLangField, chat_id:Long?, szie:Int){
        val outputFileName = "${chat_id}_$szie.mp3";
        println("audio index record $szie")
        val outputFile = File(context.filesDir.absolutePath, outputFileName)
        startAudioRecord(audioRecord, webSocket, langJsonStr, outputFile)
    }

    suspend fun endAudioRecord(){
        CoroutineScope(Dispatchers.IO).launch {
            stopAudioRecord(audioRecord, webSocket)
        }
    }

    fun appendText(bytes:ByteString): String {
        completeText += bytes.toString();
        return completeText;
    }

    fun appendText(text:String):String {
        completeText += text;
        return completeText;
    }
}