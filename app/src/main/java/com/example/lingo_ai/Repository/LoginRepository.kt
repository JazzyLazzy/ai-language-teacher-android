package com.example.lingo_ai.Repository

import android.content.Context
import android.util.Log
import androidx.navigation.ActivityNavigatorExtras
import com.example.lingo_ai.Login.CredIncDialogue
import com.example.lingo_ai.Login.OopsDialogue
import com.example.lingo_ai.Login.startLearning
import com.lazarus.cloudapi.createUser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object LoginRepositoryModule {

    @Provides
    @Singleton
    fun provideLoginRepository(@ApplicationContext context:Context):LoginRepository{
        return LoginRepository(context)
    }

}

class LoginRepository(private val context: Context) {

    fun loginUser(username:String, password:String):String{
        return com.lazarus.cloudapi.loginUser(username, password, this.context)
    }

    fun registerUser(username: String, password: String):String{
        return createUser(username, password, this.context)
    }

    fun quickConnect():String?{
        return com.lazarus.cloudapi.quickConnect(context)
    }

}