package com.dedany.secretgift.di

import com.dedany.secretgift.data.dataSources.games.remote.api.SecretGiftApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient
            .Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl("https://secret-gift-backend.vercel.app")
            .client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build()

    }

    @Provides
    @Singleton
    fun providesSecretGiftApi(retrofit: Retrofit): SecretGiftApi {
        return retrofit.create(SecretGiftApi::class.java)
    }

    @Provides
    @Singleton
    fun providesUsersApi(userApi: SecretGiftApi): SecretGiftRepository {
        return SecretGiftRepository(secretGiftApi)
    }

}