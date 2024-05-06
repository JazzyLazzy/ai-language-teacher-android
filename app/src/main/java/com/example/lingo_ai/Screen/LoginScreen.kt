package com.example.lingo_ai.Screen


import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lingo_ai.Viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(
    mainViewModel: MainViewModel,
    navController: NavController
){
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var backPressHandled by remember { mutableStateOf(false) }
    val yOffsetAnimatable = remember { Animatable(0f) }
    val alphaLogin = remember { Animatable(0f) }
    val loginScreenState = mainViewModel.loginScreenUiState
    val welcomeScreenShown = loginScreenState.value.welcomeScreenShown
    val loginScreenShown = loginScreenState.value.loginScreenShown
    val registerScreenShown = loginScreenState.value.registerScreenShown
    val snackBarShown = loginScreenState.value.snackBarShown
    val snackBarMessage = loginScreenState.value.snackBarMessage
    val backpressScope = rememberCoroutineScope()
    /*BackHandler(enabled = !backPressHandled) {
        println("back pressed")
        backPressHandled = true
        backpressScope.launch {
            awaitFrame()
            Log.d("Back press", loginScreenShown.toString())
            if (loginScreenShown || registerScreenShown){
                alphaLogin.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutLinearInEasing
                    )
                )
                if (loginScreenShown){
                    mainViewModel.hideLoginScreen()
                }
                yOffsetAnimatable.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 3000,
                        easing = FastOutLinearInEasing
                    )
                )
            }else{
                onBackPressedDispatcher?.onBackPressed()
            }
            backPressHandled = false
        }
    }*/
    var snackbarScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Log.d("login state", loginScreenState.value.successLogin.toString())
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ){
        paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            if (welcomeScreenShown){
                WelcomeScreenContent(yOffsetAnimatable, mainViewModel)
            }else if (loginScreenShown){
                LoginScreenContent(alphaLogin, mainViewModel, snackbarHostState) {
                    register_loginButtonClickHandler(
                        navController = navController,
                        loginScreenState = loginScreenState
                    )
                }
            }else if (registerScreenShown){
                RegisterScreenContent(alphaLogin, mainViewModel, snackbarHostState) {
                    register_loginButtonClickHandler(
                        navController = navController,
                        loginScreenState = loginScreenState
                    )
                }
            }
            if (snackBarShown){
                snackbarScope.launch {
                    snackbarHostState.showSnackbar(
                        message = snackBarMessage
                    )
                }
            }
        }
    }
}

@Composable
internal fun WelcomeScreenContent(
    yOffsetAnimatable:Animatable<Float, AnimationVector1D>,
    mainViewModel: MainViewModel
){
    val scope = rememberCoroutineScope()
    Box(
        contentAlignment = Alignment.BottomCenter, // you apply alignment to all children
        modifier = Modifier
            .fillMaxSize()
            .offset(y = yOffsetAnimatable.value.dp)
    ) {
        Column(
            modifier = Modifier.width(IntrinsicSize.Min)
        ) {
            FilledTonalButton(
                onClick = {
                    scope.launch {
                        val targetOffset = -50f
                        yOffsetAnimatable.animateTo(
                            targetValue = targetOffset,
                            animationSpec = tween(
                                durationMillis = 3000,
                                easing = FastOutLinearInEasing
                            )
                        )
                        if (yOffsetAnimatable.value == targetOffset) {
                            mainViewModel.showLoginScreen()
                        }
                    }
                }
            ) {
                Text(
                    text = "Sign In",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            OutlinedButton(
                onClick = {
                    scope.launch {
                        val targetOffset = -50f
                        yOffsetAnimatable.animateTo(
                            targetValue = targetOffset,
                            animationSpec = tween(
                                durationMillis = 3000,
                                easing = FastOutLinearInEasing
                            )
                        )
                        if (yOffsetAnimatable.value == targetOffset) {
                            mainViewModel.showRegisterScreen()
                        }
                    }
                },
                modifier = Modifier.width(IntrinsicSize.Max)
            ) {
                Text(
                    text = "Create Account",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
internal fun LoginScreenContent(
    alphaLogin:Animatable<Float, AnimationVector1D>,
    mainViewModel: MainViewModel,
    snackbarHostState: SnackbarHostState,
    onButtonClick: () -> Unit
){
    val scope = rememberCoroutineScope()
    scope.launch {
        alphaLogin.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutLinearInEasing
            )
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(alpha = alphaLogin.value)
            .padding(horizontal = 16.dp, vertical = 32.dp) // Padding around the box
    ) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Column {
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    snackbarHostState.currentSnackbarData?.dismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Username") },
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    snackbarHostState.currentSnackbarData?.dismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )

            FilledTonalButton(
                onClick = {
                    runBlocking {
                        mainViewModel.onLoginClick(username, password)
                    }
                    onButtonClick()
                }
            ){
                Text(
                    text = "Sign In",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
internal fun RegisterScreenContent(
    alphaLogin:Animatable<Float, AnimationVector1D>,
    mainViewModel: MainViewModel,
    snackbarHostState: SnackbarHostState,
    onButtonClick: () -> Unit
){
    val scope = rememberCoroutineScope()
    scope.launch {
        alphaLogin.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutLinearInEasing
            )
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(alpha = alphaLogin.value)
            .padding(horizontal = 16.dp, vertical = 32.dp) // Padding around the box
    ) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var repassword by remember { mutableStateOf("") }

        Column {
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    snackbarHostState.currentSnackbarData?.dismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Username") }
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    snackbarHostState.currentSnackbarData?.dismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )

            OutlinedTextField(
                value = repassword,
                onValueChange = {
                    repassword = it
                    snackbarHostState.currentSnackbarData?.dismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Retype Password") },
                visualTransformation = PasswordVisualTransformation()
            )

            FilledTonalButton(
                onClick = {
                    if (password != repassword){
                        println("no match")
                        mainViewModel.onPasswordNoMatch()
                    }else{
                        runBlocking {
                            mainViewModel.onRegisterClick(username, password)
                        }
                        onButtonClick()
                    }
                }
            ){
                Text(
                    text = "Register Account",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


internal fun register_loginButtonClickHandler(
    navController: NavController,
    loginScreenState:MutableState<LoginScreenUiState>
){
    val successLogin = loginScreenState.value.successLogin
    val authToken = loginScreenState.value.authToken
    if (successLogin){
        navController.navigate(Screen.LanguageScreen.withArgs(authToken!!))
    }
}