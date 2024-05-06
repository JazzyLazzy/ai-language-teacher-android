package com.example.lingo_ai.Viewmodel

import android.content.Context
import android.os.Bundle
import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingo_ai.LanguageFragment.LanguageFragment
import com.example.lingo_ai.Login.CredIncDialogue
import com.example.lingo_ai.Login.ExistDialogue
import com.example.lingo_ai.Login.OopsDialogue
import com.example.lingo_ai.Login.startLearning
import com.example.lingo_ai.R
import com.example.lingo_ai.Repository.LoginRepository
import com.example.lingo_ai.Screen.LoginScreenUiState
import com.lazarus.cloudapi.loginUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loginRepository: LoginRepository
): ViewModel() {

    val loginScreenUiState = mutableStateOf(LoginScreenUiState())
    private var authToken:String? = null

    fun showLoginScreen(){
        viewModelScope.launch {
            loginScreenUiState.value = loginScreenUiState.value
                .hideWelcomeScreen()
                .showLoginScreen()
        }
    }

    fun hideLoginScreen(){
        viewModelScope.launch {
            loginScreenUiState.value = loginScreenUiState.value
                .showWelcomeScreen()
                .hideLoginScreen()
        }
    }

    fun showRegisterScreen(){
        viewModelScope.launch {
            loginScreenUiState.value = loginScreenUiState.value
                .hideWelcomeScreen()
                .showRegisterScreen()
        }
    }

    suspend fun onLoginClick(username: String, password:String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                authToken = try{
                    loginRepository.loginUser(username, password)
                } catch (error:Error){
                    null
                }
                // back to UI thread
                withContext(Dispatchers.Main){
                    handleAuthToken(loginScreenUiState, authToken)
                }
            }
        }
    }

    suspend fun onRegisterClick(username: String, password:String){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                authToken = try {
                    loginRepository.registerUser(username, password)
                } catch (error: Error) {
                    null
                }
                // back to UI thread
                withContext(Dispatchers.Main) {
                    handleAuthToken(loginScreenUiState, authToken)
                }
            }
        }
    }

    fun onPasswordNoMatch(){
        val message = "Passwords do not match."
        loginScreenUiState.value = loginScreenUiState.value.showSnackBar(message)
    }

    private fun handleAuthToken(loginScreenUiState: MutableState<LoginScreenUiState>, authToken:String?){
        when (authToken){
            "401" -> {
                val message = "Username or Password incorrect. Please try again."
                loginScreenUiState.value = loginScreenUiState.value.showSnackBar(message)
            }
            "403" -> {
                val message = "That username is already taken. Please try again."
                loginScreenUiState.value = loginScreenUiState.value.showSnackBar(message)
            }
            "503" -> {
                val message = "Sorry for the inconvenience, we are currently experiencing server side issues. Please try again later."
                loginScreenUiState.value = loginScreenUiState.value.showSnackBar(message)
            }
            null -> {
                val message = "Oops, looks like something broke. Please try again."
                loginScreenUiState.value = loginScreenUiState.value.showSnackBar(message)
            }
            else -> {
                println("success login")
                loginScreenUiState.value = loginScreenUiState.value.onSuccessLogin(authToken)
            }
        }
    }
}