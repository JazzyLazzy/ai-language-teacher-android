package com.example.lingo_ai

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.lingo_ai.ChatFragment.ChatFragment
import com.example.lingo_ai.LanguageFragment.LanguageFragment
import com.example.lingo_ai.Login.LoginFragment
import com.example.lingo_ai.Login.startLearning
import com.example.lingo_ai.Ui.PandaTheme
import com.example.lingo_ai.databinding.ActivityMainBinding
import com.here.oksse.OkSse
import com.lazarus.cloudapi.GPTListener
import com.lazarus.cloudapi.listen
import com.lazarus.cloudapi.quickConnect
import com.lazarus.cloudapi.sendText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContent {
            PandaTheme {
                Navigation()
            }
        }
        //setContentView(binding.root)

        /*CoroutineScope(Dispatchers.IO).launch {
            val reponse = quickConnect(this@MainActivity.applicationContext)
            Log.d("reponse", reponse!!)
            withContext(Dispatchers.Main) {
                when (reponse) {
                    "401" -> {
                        supportFragmentManager.beginTransaction()
                            .add(R.id.fragment_container, LoginFragment(), "LOGIN_FRAGMENT")
                            .commit()
                    }

                    else -> {
                        startLearning(supportFragmentManager, reponse!!)
                    }
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (fragment is ChatFragment) {
                    // The fragment has consumed the back button press
                    isEnabled = false
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, LanguageFragment())
                        .commit()
                    return
                }
                isEnabled = true
            }
        })*/
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}