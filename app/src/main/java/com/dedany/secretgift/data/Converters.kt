package com.dedany.secretgift.data

import androidx.room.TypeConverter
import com.dedany.secretgift.data.dataSources.games.local.gameDbo.PlayerDbo
import com.dedany.secretgift.data.dataSources.games.local.gameDbo.RuleDbo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {

    private val gson = Gson()


    @TypeConverter
    fun fromPlayersList(players: List<PlayerDbo>): String {
        return gson.toJson(players) // Convertir la lista de jugadores a JSON
    }

    @TypeConverter
    fun toPlayersList(playersJson: String): List<PlayerDbo> {
        if (playersJson.isEmpty()) return emptyList()
        val type = object : TypeToken<List<PlayerDbo>>() {}.type
        return gson.fromJson(playersJson, type) // Convertir JSON de vuelta a lista de PlayerDbo
    }

    @TypeConverter
    fun fromRulesList(players: List<RuleDbo>): String {
        return gson.toJson(players) // Convertir la lista de jugadores a JSON
    }

    @TypeConverter
    fun toRulesList(playersJson: String): List<RuleDbo> {
        if (playersJson.isEmpty()) return emptyList()
        val type = object : TypeToken<List<RuleDbo>>() {}.type
        return gson.fromJson(playersJson, type) // Convertir JSON de vuelta a lista de PlayerDbo
    }

    @TypeConverter
    fun dateToString(date: Date): String {
        return gson.toJson(date)
    }

    @TypeConverter
    fun stringToDate(value: String): Date {
        return gson.fromJson(value, Date::class.java)
    }

}
