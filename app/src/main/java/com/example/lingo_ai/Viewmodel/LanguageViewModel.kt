package com.example.lingo_ai.Viewmodel

import androidx.lifecycle.ViewModel
import com.example.lingo_ai.Repository.Language
import com.example.lingo_ai.Repository.LanguagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val languagesRepository: LanguagesRepository
) : ViewModel() {

    val languages:List<Language> = languagesRepository.languages
    var authToken:String = ""

}