package com.example.lingo_ai.Repository


import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.lingo_ai.R
import javax.inject.Inject

class LanguagesRepository @Inject constructor() {

    val languages = listOf(
        Language(
            "Cantonese",
            "zh-HK",
            R.drawable.flag_of_hong_kong
        ),
        Language(
            "Mandarin",
            "zh-TW",
            R.drawable.flag_of_the_republic_of_china
        )
    )

}

data class Language(
    val name: String,
    val short_name: String,
    val flag_drawable: Int = R.drawable.ic_launcher_background,
    val contentDescription:String? = null
)
