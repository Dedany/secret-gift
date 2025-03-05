package com.dedany.secretgift.di

import com.dedany.secretgift.domain.repositories.AuthRepository
import com.dedany.secretgift.domain.repositories.GamesRepository
import com.dedany.secretgift.domain.repositories.UsersRepository
import com.dedany.secretgift.domain.usecases.auth.AuthUseCase
import com.dedany.secretgift.domain.usecases.auth.AuthUseCaseImpl
import com.dedany.secretgift.domain.usecases.games.GamesUseCase
import com.dedany.secretgift.domain.usecases.games.GamesUseCaseImpl
import com.dedany.secretgift.domain.usecases.users.UserUseCaseImpl
import com.dedany.secretgift.domain.usecases.users.UsersUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGamesUseCase(
        repository: GamesRepository
    ): GamesUseCase {
        return GamesUseCaseImpl(
            repository = repository
        )
    }

    @Provides
    @Singleton
    fun provideAuthUseCase(repository: AuthRepository): AuthUseCase {
        return AuthUseCaseImpl(repository)
    }

    @Provides
    @Singleton
    fun provideUsersUseCase(repository: UsersRepository): UsersUseCase {
        return UserUseCaseImpl(repository)
    }

}
