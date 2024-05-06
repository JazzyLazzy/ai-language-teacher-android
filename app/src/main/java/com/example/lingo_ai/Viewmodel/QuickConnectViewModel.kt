package com.example.lingo_ai.Viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingo_ai.Repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuickConnectViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private var authToken:String? = null
    val quickConnectUiState = mutableStateOf(QuickConnectUiState())

    fun quickConnect(){
        viewModelScope.launch {
            Log.d("QC", quickConnectUiState.value.quickConnectFinished.toString())
            withContext(Dispatchers.IO){
                authToken = try {
                    loginRepository.quickConnect()
                } catch (error:Error) {
                    null
                }
                Log.d("QC", "recieved aut")
                //back to UI thread
                withContext(Dispatchers.Main){
                    if (authToken != null){
                        when (authToken) {
                            "401" -> {

                            }
                            else -> {
                                quickConnectUiState.value = quickConnectUiState.value
                                    .copy(quickConnectSuccess = true, authToken = authToken)
                            }
                        }
                    }
                    quickConnectUiState.value = quickConnectUiState.value.copy(quickConnectFinished = true)
                    Log.d("QC", quickConnectUiState.value.quickConnectFinished.toString())
                }
            }
        }
    }
}

data class QuickConnectUiState(
    val quickConnectFinished:Boolean = false,
    val quickConnectSuccess:Boolean = false,
    val authToken:String? = null
)