package com.example.lingo_ai.ChatFragment

import android.media.AudioRecord
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.example.lingo_ai.Login.LoginFragment
import com.example.lingo_ai.R
import com.example.lingo_ai.databinding.FragmentChatBinding
import com.lazarus.cloudapi.Conversation
import com.lazarus.cloudapi.GPTListener
import com.lazarus.cloudapi.OutputStreamHttpURLConnection
import com.lazarus.cloudapi.TextListener
import com.lazarus.cloudapi.generateOutputStream
import com.lazarus.cloudapi.getTextResult
import com.lazarus.cloudapi.initialiseWebSocket
import com.lazarus.cloudapi.listen
import com.lazarus.cloudapi.sendText
import com.lazarus.cloudapi.startAudioRecord
import com.lazarus.cloudapi.startAudioRecording
import com.lazarus.cloudapi.stopAudioRecord
import com.lazarus.cloudapi.stopAudioRecording
import com.lazarus.cloudapi.usineAudioRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.io.File

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding;
    private var outputStreamHttpURLConnection: OutputStreamHttpURLConnection? = null
    private lateinit var submitButton: AppCompatImageButton
    private lateinit var humanText: EditText
    private lateinit var aiText: TextView
    private lateinit var listenButton: AppCompatButton;
    private lateinit var fastButton: AppCompatButton;
    private lateinit var slowButton: AppCompatButton;
    private lateinit var learningLang: String
    private lateinit var speakButton: AppCompatButton;
    private val conversation: ArrayList<Conversation> = ArrayList();
    private var incomingText:Boolean = false
    private lateinit var stopButton: AppCompatButton;
    private var isRecording:Boolean = false
    private lateinit var audioRecord: AudioRecord;
    private lateinit var langJsonString: String;
    private var authToken: String? = null
    private var mWebSocket:WebSocket? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = arguments
        if (arguments != null) {
            learningLang = arguments.getString("lang").toString()
            langJsonString = """{"lang":"$learningLang"}""";
            authToken = arguments.getString("authToken").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        submitButton = binding.submit
        listenButton = binding.listen
        slowButton = binding.slow
        fastButton = binding.fast
        humanText = binding.humanText;
        aiText = binding.aiText;
        speakButton = binding.speakButton;
        stopButton = binding.stopButton;
        aiText.text = "Waiting for your answer."

        /*speakButton.setOnClickListener {
            if (authToken == null){
                returnToLogin(parentFragmentManager)
            }
            speakButton.visibility = View.GONE
            stopButton.visibility = View.VISIBLE
            if (!isRecording){
                isRecording = true
                CoroutineScope(Dispatchers.IO).launch {
                    //outputStreamHttpURLConnection = generateOutputStream()
                    audioRecord = usineAudioRecord()
                    var completeText = "";
                    initialiseWebSocket(object: WebSocketListener() {
                        override fun onOpen(webSocket: WebSocket, response: Response) {
                            super.onOpen(webSocket, response)
                            setStopButtonClickListener(webSocket);
                            CoroutineScope(Dispatchers.IO).launch {
                                startAudioRecord(audioRecord, webSocket, langJsonString);
                            }
                        }

                        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                            super.onMessage(webSocket, bytes)
                            Log.d("Stt:", "message:" + bytes.toString())
                            completeText += bytes.toString();
                            lifecycleScope.launch {
                                humanText.text = bytes.utf8().toEditable()
                            }
                        }

                        override fun onMessage(webSocket: WebSocket, text: String) {
                            super.onMessage(webSocket, text)
                            Log.d("Stt:", "message:" + text)
                            completeText += text;
                            lifecycleScope.launch {
                                humanText.text = completeText.toEditable()
                            }
                        }
                    })
                }
            }
        }*/

        submitButton.setOnClickListener {
            if (authToken == null){
                returnToLogin(parentFragmentManager)
            }
            if (!incomingText){
                incomingText = true
                CoroutineScope(Dispatchers.IO).launch {
                    val message = Conversation(humanText.text.toString(), null)
                    conversation.add(message)
                    //Log.d("json wash", humanText.text.toString())
                    Log.d("json wash", conversation[0].user!!)
                }
            }
        }

        /*listenButton.setOnClickListener{
            onListenButtonClick("+2%")
        }

        slowButton.setOnClickListener{
            onListenButtonClick("-20%");
        }

        fastButton.setOnClickListener{
            onListenButtonClick("+35%");
        }*/
    }

    /*private fun onListenButtonClick(listenRate:String){
        if (authToken == null){
            returnToLogin(parentFragmentManager)
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                listen(aiText.text.toString(), learningLang, this@ChatFragment.requireContext(), listenRate, authToken!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }*/

    private fun setStopButtonClickListener(webSocket: WebSocket){
        stopButton.setOnClickListener{
            stopButton.visibility = View.GONE
            speakButton.visibility = View.VISIBLE
            isRecording = false
            CoroutineScope(Dispatchers.IO).launch {
                stopAudioRecord(audioRecord, webSocket)
                /*result.let {
                    lifecycleScope.launch {
                        Log.d("Stt:", it)
                        humanText.text = Editable.Factory.getInstance().newEditable(it)
                    }
                }*/
            }
        }
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}

fun returnToLogin(fragmentManager: FragmentManager){
    fragmentManager.beginTransaction()
        .replace(R.id.fragment_container, LoginFragment())
        .addToBackStack(null)
        .commit()
}