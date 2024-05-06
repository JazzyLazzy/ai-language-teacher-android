package com.example.lingo_ai.Viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.enums.EnumEntries

@HiltViewModel
class QuestionViewModel @Inject constructor() : ViewModel() {

    val questionScreenUiState = mutableStateOf(QuestionScreenUiState())
    private val size = QuestionScreen.entries.size
    private val iterator = QuestionScreenIterator(QuestionScreen.entries)

    fun moveForward() {
        viewModelScope.launch {
            val currentPage = questionScreenUiState.value.questionScreenPage.ordinal
            iterator.currentIndex = currentPage
            Log.d("qscreen", "moveforwar")
            try {
                questionScreenUiState.value = questionScreenUiState.value
                    .copy(questionScreenPage = iterator.next())
                println("qscreen, ${questionScreenUiState.value}")
            } catch (_:Error){
                println("qscreen error")
            }
        }
    }

    fun moveBackward() {
        viewModelScope.launch {
            val currentPage = questionScreenUiState.value.questionScreenPage.ordinal
            iterator.currentIndex = currentPage
            try {
                questionScreenUiState.value = questionScreenUiState.value
                    .copy(questionScreenPage = iterator.back())
            } catch (_:Error){

            }
        }
    }
}

data class QuestionScreenUiState(
    val questionScreenPage: QuestionScreen = QuestionScreen.ZERO
) {

}

enum class QuestionScreen {
    ZERO, EXPERIENCE, WHY
}

internal class QuestionScreenIterator(private val values: EnumEntries<QuestionScreen>) : Iterator<QuestionScreen> {
    var currentIndex = 0

    override fun hasNext(): Boolean {
        return currentIndex < values.size - 1
    }

    override fun next(): QuestionScreen {
        if (!hasNext()) {
            throw NoSuchElementException()
        }
        //println("qscreen ${values[currentIndex++]}")
        currentIndex++
        return values[currentIndex]
    }

    fun hasPrevious(): Boolean {
        return currentIndex > 0
    }

    fun back(): QuestionScreen {
        if (!hasPrevious()) {
            throw NoSuchElementException()
        }
        currentIndex--
        return values[currentIndex]
    }
}

class Question(val language: String){
    val questionZero = "Before we get started, let's get to know you, and your journey learning $language, to customise your experience with our AI teachers."
    val questionExperience = "How proficient are you in $language?"
    val questionWhy = "Is there any particular reason you are learning $language?"
}