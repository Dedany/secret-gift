package com.dedany.secretgift.di

import com.dedany.secretgift.data.dataSources.games.remote.GameRemoteDataSource
import com.dedany.secretgift.data.dataSources.games.remote.GameRemoteDataSourceImpl
import com.dedany.secretgift.data.dataSources.games.remote.api.SecretGiftApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideGameRemoteDataSource(secretGiftApi: SecretGiftApi): GameRemoteDataSource {
        return GameRemoteDataSourceImpl(secretGiftApi)
    }

}
