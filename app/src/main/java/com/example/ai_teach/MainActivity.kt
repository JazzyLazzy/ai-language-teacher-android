package com.example.ai_teach

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ai_teach.databinding.ActivityMainBinding
import com.example.ai_teach.ui.theme.Ai_teachTheme
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var submitButton: AppCompatImageButton
    private lateinit var humanText: EditText
    private lateinit var aiText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        submitButton = binding.submit
        humanText = binding.humanText;
        aiText = binding.aiText;
        submitButton.setOnClickListener {
            val url = "https://azTest.azurewebsites.net/api/sendText"
            val jsonBody = """{"text": "${humanText.text}"}"""

            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val requestBody = jsonBody.toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val client = OkHttpClient()
            Thread {
                val response: Response = client.newCall(request).execute()
                Handler(Looper.getMainLooper()).post {
                    val gptResponse = Json.decodeFromString<GPTResponse>(response.body!!.string())
                    aiText.text = gptResponse.message.content
                    response.close()
                }
            }.start()

            // Close the response body
        }
        /*setContent {
            Ai_teachTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }*/
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Ai_teachTheme {
        Greeting("Android")
    }
}