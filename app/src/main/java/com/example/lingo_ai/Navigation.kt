package com.example.lingo_ai

import android.content.Context
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lingo_ai.Screen.LoginScreen
import com.example.lingo_ai.Screen.Screen
import com.example.lingo_ai.Viewmodel.MainViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.lingo_ai.Screen.LanguageScreen
import com.example.lingo_ai.Screen.LearningScreen
import com.example.lingo_ai.Screen.QuestionScreen
import com.example.lingo_ai.Viewmodel.LanguageViewModel
import com.example.lingo_ai.Viewmodel.LearningViewModel
import com.example.lingo_ai.Viewmodel.QuestionViewModel
import com.example.lingo_ai.Viewmodel.QuickConnectViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val quickConnectViewModel = hiltViewModel<QuickConnectViewModel>()
    NavHost(navController = navController, startDestination = Screen.LandingScreen.route) {
        composable(Screen.LandingScreen.route) {
            val quickConnectState = quickConnectViewModel.quickConnectUiState
            val quickConnectSuccess = quickConnectState.value.quickConnectSuccess
            val quickConnectFinished = quickConnectState.value.quickConnectFinished
            quickConnectViewModel.quickConnect()
            if (quickConnectFinished) {
                Log.d("QC", "navigation")
                if (quickConnectSuccess) {
                    println("QC navigation")
                    val authToken = quickConnectState.value.authToken
                    navController.navigate(Screen.LanguageScreen.withArgs(authToken!!))
                } else {
                    println("QC navigation")
                    navController.navigate("auth")
                }
            }
        }
        navigation(
            startDestination = Screen.LoginScreen.route,
            route = "auth"
        ){
            composable(Screen.LoginScreen.route){
                val mainViewModel = hiltViewModel<MainViewModel>()
                LoginScreen(
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }
        }
        composable(
            route = Screen.LanguageScreen.route + "/{authToken}",
            arguments = listOf(
                navArgument("authToken"){
                    type = NavType.StringType
                    nullable = false
                }
            )
        ){
            val authToken = it.arguments?.getString("authToken")
            val languageViewModel = hiltViewModel<LanguageViewModel>()
            if (authToken != null) {
                languageViewModel.authToken = authToken
            }else{
                navController.navigate("auth")
            }
            LanguageScreen(
                languageViewModel = languageViewModel,
                navController = navController
            )
        }
        composable(
            route = Screen.QuestionScreen.route + "/{authToken}/{language}",
            arguments = listOf(
                navArgument("authToken"){
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("language"){
                    type = NavType.StringType
                    nullable = false
                }
            )
        ){
            val language = it.arguments?.getString("language")
            val authToken = it.arguments?.getString("authToken")
            val questionViewModel = hiltViewModel<QuestionViewModel>()
            QuestionScreen(
                language = language!!,
                questionViewModel = questionViewModel,
                navController = navController,
                authToken = authToken!!
            )
        }
        navigation(
            startDestination = Screen.LearningScreen.route + "/{authToken}/{language}",
            route = "learn"
        ){
            composable(
                route = Screen.LearningScreen.route + "/{authToken}/{language}",
                arguments = listOf(
                    navArgument("authToken"){
                        type = NavType.StringType
                        nullable = false
                    },
                    navArgument("language"){
                        type = NavType.StringType
                        nullable = false
                    }
                )
            ){
                val learningViewModel = hiltViewModel<LearningViewModel>()
                learningViewModel.authToken = it.arguments?.getString("authToken").toString()
                learningViewModel.onChangingLanguage(it.arguments?.getString("language").toString());
                val item = listOf(
                    Pair("Home", Icons.Default.Home),
                    Pair("Speaking", Icons.Default.Call),
                    Pair("Listening", Icons.Default.Person),
                    Pair("Scenarios", Icons.Default.Face)
                )
                var selectedItem by remember { mutableIntStateOf(0) }
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            item.forEachIndexed { index, item ->
                                NavigationBarItem (
                                    icon = { Icon(item.second, contentDescription = item.first) },
                                    label = { Text(item.first) },
                                    selected = selectedItem == index,
                                    onClick = { selectedItem = index }
                                )
                            }
                        }
                    }
                ){ contentPadding ->
                    when (selectedItem) {
                        0 ->
                            LearningScreen(
                                learningViewModel = learningViewModel,
                                contentPadding = contentPadding
                            )
                    }
                }
            }
        }
    }
}