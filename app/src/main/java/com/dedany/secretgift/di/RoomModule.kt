package com.dedany.secretgift.di

import android.app.Application
import com.dedany.secretgift.data.dataSources.games.local.GamesDao
import com.dedany.secretgift.data.dataSources.games.users.RoomDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideLocalDataSource(application: Application): RoomDb {
        return RoomDb.invoke(application)

    }

    @Provides
    @Singleton
    fun provideGamesDao(database: RoomDb): GamesDao {
        return database.gamesDao()
    }

}
