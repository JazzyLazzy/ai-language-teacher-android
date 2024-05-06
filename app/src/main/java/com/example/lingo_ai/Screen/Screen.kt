package com.example.lingo_ai.Screen

sealed class Screen(val route:String) {
    data object LandingScreen : Screen("landing_screen")
    data object LoginScreen : Screen("login_screen")
    data object LanguageScreen : Screen("language_screen")
    data object LearningScreen : Screen("learning_screen")
    data object QuestionScreen : Screen("question_screen")

    fun withArgs(vararg args:String):String{
        return buildString {
            append(route)
            args.forEach {
                append("/$it")
            }
        }
    }
}