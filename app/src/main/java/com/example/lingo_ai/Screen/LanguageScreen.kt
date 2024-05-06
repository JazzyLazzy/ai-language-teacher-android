package com.example.lingo_ai.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lingo_ai.Repository.Language
import com.example.lingo_ai.Viewmodel.LanguageViewModel

@Composable
fun LanguageScreen(
    languageViewModel: LanguageViewModel,
    navController: NavController
){
    Column {
        val languages = languageViewModel.languages
        languages.forEach{
            LanguageButton(it) { languageButtonClick(languageViewModel, navController, it) }
        }
    }
}

@Composable
internal fun LanguageButton(
    language: Language,
    languageButtonClick: () -> Unit
){
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        onClick = { languageButtonClick() }
    ) {
        Row(
           verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = language.flag_drawable),
                contentDescription = language.contentDescription,
                modifier = Modifier
                    .weight(1f)
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(3f)
            ){
                Text(
                    text = language.name,
                )
            }

        }
    }
}

internal fun languageButtonClick(
    languageViewModel: LanguageViewModel,
    navController: NavController,
    language: Language
){
    navController.navigate(Screen.QuestionScreen.withArgs(languageViewModel.authToken, language.short_name))
}

@Preview
@Composable
fun previewlangbutton(){
    LanguageButton(language = Language("Cantonese", "zh-HK")) {}
}