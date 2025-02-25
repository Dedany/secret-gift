package com.dedany.secretgift.di

import com.dedany.secretgift.data.dataSources.auth.remote.AuthRemoteDataSource
import com.dedany.secretgift.data.dataSources.games.local.GamesDao
import com.dedany.secretgift.data.dataSources.games.remote.GameRemoteDataSource
import com.dedany.secretgift.data.respositories.AuthRepositoryImpl
import com.dedany.secretgift.data.respositories.GameRepositoryImpl
import com.dedany.secretgift.domain.repositories.AuthRepository
import com.dedany.secretgift.domain.repositories.GamesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideGamesRepository(
        remoteGamesDataSource: GameRemoteDataSource,
        localGameDataSource: GamesDao
    ): GamesRepository {
        return GameRepositoryImpl(
            remoteGamesDataSource, localGameDataSource
        )
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authRemoteDataSource: AuthRemoteDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(authRemoteDataSource)
    }
}
