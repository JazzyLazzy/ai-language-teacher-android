package com.example.lingo_ai.Screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lingo_ai.Viewmodel.Question
import com.example.lingo_ai.Viewmodel.QuestionScreen
import com.example.lingo_ai.Viewmodel.QuestionViewModel


@Composable
fun QuestionScreen(
    language:String,
    questionViewModel: QuestionViewModel,
    navController: NavController,
    authToken: String
){

    val questionScreenUiState = questionViewModel.questionScreenUiState
    val questionSceenPage = questionScreenUiState.value.questionScreenPage
    val question = Question(language)

    when (questionSceenPage){
        QuestionScreen.ZERO ->
            QuestionZero(
                question,
                questionViewModel,
                navController,
                authToken
            )
        QuestionScreen.EXPERIENCE ->
            QuestionExperience(
                question,
                questionViewModel
            )

        QuestionScreen.WHY ->
            QuestionWhy(
                question,
                questionViewModel,
                navController,
                authToken
            )
    }
}

@Composable
fun QuestionZero(
    question: Question,
    questionViewModel: QuestionViewModel,
    navController: NavController,
    authToken: String
){

    Column(

    ){
        Text(
            text = question.questionZero
        )
        FilledTonalButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            onClick = {
                println("qscreen clicked")
                questionViewModel.moveForward()
            }
        ) {
            Text(text = "Let's begin!")
        }
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            onClick = {
                navController.navigate(Screen.LanguageScreen.withArgs(authToken))
            }
        ) {
            Text(text = "Select another language")
        }
    }
}

@Composable
fun QuestionExperience(
    question: Question,
    questionViewModel: QuestionViewModel,
){

    val level = arrayOf("Absolute Beginner", "Simple Phrases", "Intermediate", "Advanced", "Fluent", "Approaching Mastery")

    Column(){
        Text(text = question.questionExperience)

        level.forEach {
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                onClick = { questionViewModel.moveForward() }
            ) {
                Text(text = it)
            }
        }

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            onClick = {questionViewModel.moveBackward()}
        ) {
            Text(text = "Go back")
        }
    }

}

@Composable
fun QuestionWhy(
    question: Question,
    questionViewModel: QuestionViewModel,
    navController: NavController,
    authToken: String
){

    val reason = arrayOf("Business/Work", "Family", "Travel", "Just for fun/other")

    Column(){
        Text(text = question.questionWhy)

        reason.forEach {
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                onClick = { navController.navigate(Screen.LearningScreen.withArgs(authToken, question.language)) }
            ) {
                Text(text = it)
            }
        }

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            onClick = { questionViewModel.moveBackward() }
        ) {
            Text(text = "Go back")
        }
    }

}