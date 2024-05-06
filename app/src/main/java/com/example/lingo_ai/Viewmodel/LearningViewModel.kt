package com.example.lingo_ai.Viewmodel

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingo_ai.Repository.AudioPlayingManager
import com.example.lingo_ai.Repository.AudioRecordingManager
import com.example.lingo_ai.Repository.LearningRepository
import com.lazarus.cloudapi.ChatHistoryObj
import com.lazarus.cloudapi.GPTListener
import com.lazarus.cloudapi.GetChatListJSONButReallyOnlyLangField
import com.lazarus.cloudapi.HistoryName
import com.lazarus.cloudapi.ReceivedChatListJSON
import com.lazarus.cloudapi.startAudioRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import javax.inject.Inject

@HiltViewModel
class LearningViewModel @Inject constructor(
    val learningRepository: LearningRepository,
    private val audioRecordingManager: AudioRecordingManager,
    private val audioPlayingManager: AudioPlayingManager
) : ViewModel() {

    val learningScreenUiState = mutableStateOf(LearningScreenUiState());
    var authToken:String = "";
    private val _liveChatList = MutableLiveData<List<ReceivedChatListJSON>>();
    val liveChatList:LiveData<List<ReceivedChatListJSON>> = _liveChatList;
    private val _liveGPTResponse = MutableLiveData<String>();
    val liveGPTResponse:LiveData<String> = _liveGPTResponse;
    private val _currentChat_id = MutableLiveData<Long>();
    val currentChat_id:LiveData<Long> = _currentChat_id;
    private val _messageHistory = MutableLiveData<List<ChatHistoryObj>>();
    private val _messageName = MutableLiveData<String>();
    val messageHistory:LiveData<List<ChatHistoryObj>> = _messageHistory;
    val messageName:MutableLiveData<String> = _messageName;
    private val _userVoixMessage = MutableLiveData<String>();
    val userVoixMessage:LiveData<String> = _userVoixMessage;

    suspend fun onSendMessage(message:String){
        viewModelScope.launch {
            val chatHistoryObj_user = ChatHistoryObj("user", message);
            var chatHistoryObj = ChatHistoryObj("assistant", "");
            var start = true;
            val currentList_uesr = _messageHistory.value ?: emptyList()
            val newList = currentList_uesr.toMutableList().apply { add(chatHistoryObj_user) }
            /*val con  = newList[newList.lastIndex]
            println("last indy ${con.content}")*/
            _messageHistory.postValue(newList);
            var recievedMsg = "";
            withContext(Dispatchers.IO){
                learningRepository.sendMessage(
                    message, authToken, learningScreenUiState.value.language,
                    learningScreenUiState.value.chat_id, learningScreenUiState.value.new_chat, object : GPTListener {

                        private var isNewChat:Boolean = false

                        override var waiting: Boolean
                            get() = TODO("Not yet implemented")
                            set(value) {}

                        override fun onReceived(decodedString: String?) {
                            if (decodedString != null){
                                CoroutineScope(Dispatchers.Main).launch{
                                    /*chatHistoryObj = chatHistoryObj.copy(content = chatHistoryObj.content + decodedString)
                                    val currentList = _messageHistory.value ?: emptyList()
                                    if (start){
                                        val newList_ass = currentList.toMutableList().apply {
                                            add(lastIndex, chatHistoryObj.copy( // Prepend updated copy at the beginning
                                                content = chatHistoryObj.content // Update specific field (replace with your logic)
                                            ))
                                        }
                                        val con  = newList_ass[newList_ass.lastIndex]
                                        println("last indy ${con.content}")
                                        _messageHistory.postValue(newList_ass);
                                        start = false;
                                    } else {
                                        val newList_ass = currentList.toMutableList().apply {
                                            removeAt(lastIndex) // Remove last element
                                            add(lastIndex, chatHistoryObj.copy( // Prepend updated copy at the beginning
                                                content = chatHistoryObj.content // Update specific field (replace with your logic)
                                            ))
                                        }
                                        val con  = newList_ass[newList_ass.lastIndex]
                                        println("last indy ${con.content}")
                                        _messageHistory.postValue(newList_ass);
                                    }*/
                                    recievedMsg += decodedString;
                                }
                            }
                        }

                        override fun onWaiting(decodedString: String) {

                        }

                        override fun onSuccessFinish() {
                            CoroutineScope(Dispatchers.Main).launch {
                                val chatHistoryObj_ass = ChatHistoryObj("assistant", recievedMsg);
                                val currentList_ass = _messageHistory.value ?: emptyList()
                                val newList = currentList_ass.toMutableList().apply { add(chatHistoryObj_ass) }
                                _messageHistory.postValue(newList)
                                println(liveChatList.value!!.last().chat_id.toString());
                                if (isNewChat){
                                    learningScreenUiState.value = learningScreenUiState.value.copy(
                                        chat_id = liveChatList.value!!.last().chat_id, selectedIndex = liveChatList.value!!.size);
                                    isNewChat = false
                                }
                            }
                        }

                        override fun onUnauthorisedAccess() {

                        }

                        override fun onChat_Id(chat_id: Long) {
                            CoroutineScope(Dispatchers.Main).launch{
                                isNewChat = true;
                                println("new chat id $chat_id")
                                val newChat = ReceivedChatListJSON(chat_id, null);
                                val currentHist = _liveChatList.value ?: emptyList();
                                val newHist = currentHist.toMutableList().apply { add(newChat) };
                                _liveChatList.postValue(newHist);
                            }
                        }
                    }
                )
            }
        }
    }

    suspend fun getChatList(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _liveChatList.postValue(learningRepository.obtainChatList(authToken, learningScreenUiState.value.language));
            }
        }
    }

    suspend fun readChatHistory(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val historyName = learningRepository.getChatHistory(authToken, learningScreenUiState.value.chat_id);
                _messageHistory.postValue(historyName.content);
                _messageName.postValue(historyName.name);
            }
        }
    }

    fun onChangingLanguage(new_language:String){
        viewModelScope.launch {
            learningScreenUiState.value = learningScreenUiState.value.copy(language = new_language);
        }
    }

    /* Also called when receive new Chat Id from backend*/
    fun onChangingChat(newSelectedIndex: Int, new_chat_id: Long?){
        viewModelScope.launch {
            if (new_chat_id == null) {
                learningScreenUiState.value = learningScreenUiState.value.copy(
                    selectedIndex = newSelectedIndex, new_chat = true);
                _messageHistory.postValue(emptyList());
            } else {
                learningScreenUiState.value = learningScreenUiState.value.copy(
                    chat_id = new_chat_id, selectedIndex = newSelectedIndex, new_chat = false)
            }
        }
    }

    suspend fun onRecordVoice(szie:Int){
        viewModelScope.launch {
            audioRecordingManager.recordAudio(object: WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    audioRecordingManager.webSocket = webSocket;
                    val langJsonString = GetChatListJSONButReallyOnlyLangField(learningScreenUiState.value.language)
                    CoroutineScope(Dispatchers.IO).launch {
                        println("starting stream")
                        audioRecordingManager.startAudioRecordStream(webSocket, langJsonString,
                            learningScreenUiState.value.chat_id, szie);
                    }
                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    super.onMessage(webSocket, bytes)
                    Log.d("Stt:", "message:" + bytes.toString());
                    CoroutineScope(Dispatchers.Main).launch {
                        _userVoixMessage.postValue(audioRecordingManager.appendText(bytes))
                    }
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    Log.d("Stt:", "message:" + text)
                    CoroutineScope(Dispatchers.Main).launch {
                        _userVoixMessage.postValue(audioRecordingManager.appendText(text));
                    }
                }
            })
        }
    }

    suspend fun stopAudioRecord(){
        viewModelScope.launch {
            audioRecordingManager.endAudioRecord();
            onSendMessage(userVoixMessage.value!!);
        }
    }

    fun userAudioFileExist(index:Int):Boolean{
        return audioPlayingManager.userAudioFileExist(learningScreenUiState.value.chat_id!!, index);
    }

    suspend fun startListening(chatHistoryObj: ChatHistoryObj, index:Int){
        viewModelScope.launch {
            val mediaPlayer = prepareMediaPlayer()
            audioPlayingManager.startListening(
                chatHistoryObj, learningScreenUiState.value.language, "+2%", authToken, mediaPlayer,
                learningScreenUiState.value.chat_id!!, index)
            learningScreenUiState.value = learningScreenUiState.value.setSoundPlaying(index)
        }
    }

    suspend fun tortureYourselfWithYourOwnVoice(index:Int){
        viewModelScope.launch {
            val mediaPlayer = prepareMediaPlayer();
            learningScreenUiState.value = learningScreenUiState.value.setSoundPlaying(index)
            audioPlayingManager.iHateMyVoice(mediaPlayer, learningScreenUiState.value.chat_id!!, learningScreenUiState.value.audioPlayingIndex!!)
        }
    }

    fun getLatestChat(){
        learningScreenUiState.value = learningScreenUiState.value.copy(
            selectedIndex = liveChatList.value!!.size
        )
    }

    private fun prepareMediaPlayer():MediaPlayer{
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener {
            // Perform actions when playback is completed (if needed)
            Log.d("tts:", "Success in media player")
            learningScreenUiState.value = learningScreenUiState.value.copy(isPlayingSound = false, audioPlayingIndex = null)
            mediaPlayer.release()
        }
        return mediaPlayer
    }

    fun onDeleteChat(index: Int, chat_id: Long) {
        viewModelScope.launch {
            CoroutineScope(Dispatchers.IO).launch {
                val ridof = learningRepository.getRidOfChat(authToken, chat_id)
                println("ridof" + ridof)
                if (ridof){
                    CoroutineScope(Dispatchers.Main).launch{
                        val currentList = _liveChatList.value ?: emptyList()

                        // 3. Remove the chat at the specified index from the in-memory list:
                        val updatedList = currentList.toMutableList().apply {
                            removeAt(index)
                        }

                        // 4. Update the LiveData with the modified list:
                        _liveChatList.postValue(updatedList)
                    }
                }
            }

        }
    }
}

data class LearningScreenUiState(
    val userMessage: String = "",
    val language:String = "",
    val chat_id:Long? = null,
    val new_chat:Boolean = true,
    val selectedIndex:Int = 0,
    val isPlayingSound:Boolean = false,
    val chatHistoryState:List<ChatHistoryObj> = emptyList(),
    val audioPlayingIndex:Int? = null
) {

    fun setSoundPlaying(index:Int):LearningScreenUiState{
        return this.copy(isPlayingSound = true, audioPlayingIndex = index)
    }

}