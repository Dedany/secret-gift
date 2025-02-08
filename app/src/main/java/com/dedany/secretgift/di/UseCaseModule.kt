package com.dedany.secretgift.di

import com.dedany.secretgift.domain.repositories.GamesRepository
import com.dedany.secretgift.domain.usecases.games.GamesUseCase
import com.dedany.secretgift.domain.usecases.games.GamesUseCaseImpl
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

}
