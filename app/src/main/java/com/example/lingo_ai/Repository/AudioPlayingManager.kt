package com.example.lingo_ai.Repository

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.lazarus.cloudapi.ChatHistoryObj
import com.lazarus.cloudapi.listen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AudioPlayingManagerModule {

    @Provides
    @Singleton
    fun provideAudioPlayingManager(@ApplicationContext context: Context):AudioPlayingManager{
        return AudioPlayingManager(context)
    }

}

class AudioPlayingManager @Inject constructor(private val context: Context) {

    suspend fun startListening(
        chatHistoryObj: ChatHistoryObj, lang:String, listenRate:String, authToken:String, mediaPlayer: MediaPlayer, chat_id:Long, index:Int){
        val fileName = "${chat_id}_${index}_$listenRate.mp3"
        CoroutineScope(Dispatchers.IO).launch{
            listen(chatHistoryObj.content, lang, context, listenRate, authToken, mediaPlayer, fileName)
        }
    }

    suspend fun iHateMyVoice(mediaPlayer: MediaPlayer, chat_id:Long, index:Int){
        val fileName = "${chat_id}_${index}.mp3";
        val audioFile = File(context.filesDir.absolutePath, fileName)

        try {
            mediaPlayer.setDataSource(audioFile.absolutePath)
            mediaPlayer.prepare()
            mediaPlayer.start()
        }catch(e:Exception){
            Log.d("tts:", "Error in media player")
        }

    }

    fun userAudioFileExist(chat_id: Long, index: Int): Boolean {
        val fileName = "${chat_id}_${index}.mp3";
        val audioFile = File(context.filesDir.absolutePath, fileName)
        return audioFile.exists()
    }

}