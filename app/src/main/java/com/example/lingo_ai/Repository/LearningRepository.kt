package com.example.lingo_ai.Repository

import android.content.Context
import android.util.Log
import com.lazarus.cloudapi.GPTListener
import com.lazarus.cloudapi.HistoryName
import com.lazarus.cloudapi.ReceivedChatListJSON
import com.lazarus.cloudapi.deleteChat
import com.lazarus.cloudapi.getChatList
import com.lazarus.cloudapi.readChatHistory
import com.lazarus.cloudapi.sendText
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LearningRepositoryModule {

    @Provides
    @Singleton
    fun provideLearningRepository(@ApplicationContext context: Context):LearningRepository{
        return LearningRepository(context)
    }

}


class LearningRepository @Inject constructor(val context: Context) {

    fun sendMessage(message: String, authToken: String, language: String, chat_id: Long?, new_chat:Boolean, gptListener:GPTListener){

        sendText(message, chat_id, language, new_chat, gptListener, authToken)

        /*com.lazarus.cloudapi.sendText(conversation, learningLang, object : GPTListener {
            override var waiting: Boolean = false

            override fun onReceived(decodedString: String?) {
                lifecycleScope.launch {
                    if (waiting) {
                        aiText.text = "$decodedString"
                        conversation[conversation.size - 1].assistant = "$decodedString"
                        waiting = false
                    } else {
                        aiText.text = "${aiText.text}$decodedString"
                        conversation[conversation.size - 1].assistant =
                            "${conversation[conversation.size - 1].assistant}${decodedString}"
                    }
                }
            }

            override fun onWaiting(decodedString: String) {
                lifecycleScope.launch {
                    aiText.text = "Waiting..."
                    waiting = true
                    val rates = arrayOf("+2%", "+35%", "-20%");
                    for (i in 0 until 3) {
                        val file = File(
                            requireContext().filesDir.absolutePath,
                            "${aiText.text}_${rates[i]}.mp3"
                        );
                        if (file.exists()) {
                            file.delete()
                        }
                    }

                }
            }

            override fun onSuccessFinish() {
                incomingText = false
            }

            override fun onUnauthorisedAccess() {
                lifecycleScope.launch {
                    returnToLogin(parentFragmentManager)
                }
            }
        }, authToken!!)*/
    }

    fun obtainChatList(authToken: String, lang:String):List<ReceivedChatListJSON>{
        val chatList = getChatList(authToken, lang);
        return chatList;
    }

    fun getChatHistory(authToken: String, chat_id: Long?):HistoryName{
        if (chat_id == null){
            return HistoryName()
        }
        val historyName = readChatHistory(authToken, chat_id);
        return historyName;
    }

    fun getRidOfChat(authToken: String, chat_id: Long):Boolean{
        return if(deleteChat(authToken, chat_id)){
            deleteFiles(chat_id)
            true;
        } else {
            false
        }
    }

    private fun deleteFiles(chat_id: Long){
        val prefix = chat_id.toString()
        val contextDir = context.filesDir ?: return

        val filesToDelete = contextDir.listFiles { file ->
            file.isFile && file.nameWithoutExtension.startsWith(prefix)
        }?.toList() ?: emptyList()

        filesToDelete.forEach { file ->
            if (file.delete()) {
                Log.d("File Delete", "Deleted file: ${file.absolutePath}")
            } else {
                Log.w("File Delete", "Failed to delete file: ${file.absolutePath}")
            }
        }
    }
}