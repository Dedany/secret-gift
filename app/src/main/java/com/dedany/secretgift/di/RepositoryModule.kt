package com.dedany.secretgift.di

import com.dedany.secretgift.data.dataSources.auth.remote.AuthRemoteDataSource
import com.dedany.secretgift.data.dataSources.games.local.GamesDao
import com.dedany.secretgift.data.dataSources.games.remote.GameRemoteDataSource
import com.dedany.secretgift.data.dataSources.users.local.preferences.UserPreferences
import com.dedany.secretgift.data.dataSources.users.remote.UsersRemoteDataSource
import com.dedany.secretgift.data.repositories.UsersRepositoryImpl
import com.dedany.secretgift.data.respositories.AuthRepositoryImpl
import com.dedany.secretgift.data.respositories.GameRepositoryImpl
import com.dedany.secretgift.domain.repositories.AuthRepository
import com.dedany.secretgift.domain.repositories.GamesRepository
import com.dedany.secretgift.domain.repositories.UsersRepository
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
        localGameDataSource: GamesDao,
        userPreferences: UserPreferences,
    ): GamesRepository {
        return GameRepositoryImpl(
            remoteGamesDataSource, localGameDataSource,userPreferences
        )
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authRemoteDataSource: AuthRemoteDataSource,
        userPreferences: UserPreferences,
        usersRemoteDataSource: UsersRemoteDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(authRemoteDataSource,userPreferences,usersRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideUsersRepository(usersRemoteDataSource: UsersRemoteDataSource,userPreferences: UserPreferences): UsersRepository {
        return UsersRepositoryImpl(usersRemoteDataSource,userPreferences)
    }
}
