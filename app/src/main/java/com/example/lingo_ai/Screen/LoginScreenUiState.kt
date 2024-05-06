package com.example.lingo_ai.Screen

data class LoginScreenUiState (
    val welcomeScreenShown:Boolean = true,
    val loginScreenShown:Boolean = false,
    val registerScreenShown:Boolean = false,
    val snackBarShown:Boolean = false,
    val snackBarMessage:String = "",
    val successLogin:Boolean = false,
    val authToken:String? = null
) {

    fun hideWelcomeScreen():LoginScreenUiState{
        return this.copy(welcomeScreenShown = false)
    }

    fun showLoginScreen():LoginScreenUiState{
        return this.copy(loginScreenShown = true)
    }

    fun showRegisterScreen():LoginScreenUiState{
        return this.copy(registerScreenShown = true)
    }

    fun showWelcomeScreen(): LoginScreenUiState {
        return this.copy(welcomeScreenShown = true)
    }

    fun hideLoginScreen(): LoginScreenUiState {
        return this.copy(loginScreenShown = false)
    }

    fun showSnackBar(message:String): LoginScreenUiState{
        return this.copy(snackBarShown = true, snackBarMessage = message)
    }

    fun onSuccessLogin(authToken: String?):LoginScreenUiState{
        return this.copy(authToken = authToken, successLogin = true)
    }

}