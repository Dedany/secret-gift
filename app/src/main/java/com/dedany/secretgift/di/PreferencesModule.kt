package com.dedany.secretgift.di

import android.content.Context
import android.content.SharedPreferences
import com.dedany.secretgift.data.dataSources.users.local.preferences.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton


private const val PREFERENCES_NAME = "user_preferences"
private const val PREFERENCES_NAME_APP = "app_preferences"

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {


    @Singleton
    @Provides
    fun provideUserSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideUserPreferences(
        sharedPreferences: SharedPreferences
    ): UserPreferences {
        return UserPreferences(sharedPreferences)
    }
}



/*

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserPreferences



    @Singleton
    @Provides
    fun provideAppSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME_APP, Context.MODE_PRIVATE)
    }*/