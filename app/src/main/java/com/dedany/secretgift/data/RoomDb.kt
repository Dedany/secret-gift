package com.dedany.secretgift.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dedany.secretgift.data.dataSources.games.local.gameDbo.GameDbo
import com.dedany.secretgift.data.dataSources.games.local.GamesDao

@Database(
    entities = [GameDbo::class], // Entidades definidas correctamente
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class) // Converters si tienes tipos de datos personalizados
abstract class RoomDb : RoomDatabase() {

    abstract fun gamesDao(): GamesDao

    companion object {
        private const val DATABASE_NAME = "SecretGift" // Nombre de nuestra base de datos

        @Volatile
        private var instance: RoomDb? = null
        private val obj = Any()

        operator fun invoke(context: Context): RoomDb =
            instance ?: synchronized(obj) {
                instance ?: createDatabase(context).also { db ->
                    instance = db
                }
            }

        private fun createDatabase(context: Context): RoomDb {
            return Room.databaseBuilder(context, RoomDb::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration() // Si no hay migración, destruye y recrea la base de datos
                .build()
        }
    }
}
