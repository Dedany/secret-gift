package com.dedany.secretgift.data

import androidx.room.TypeConverter
import com.dedany.secretgift.data.dataSources.games.local.PlayerDbo
import com.dedany.secretgift.data.dataSources.users.local.room.UserDbo
import com.dedany.secretgift.domain.entities.RegisteredUser
import com.dedany.secretgift.domain.entities.Rule
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.Date

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromRegisteredUserList(value: List<RegisteredUser>?): String? {
        return gson.toJson(value) // Convertir la lista a JSON
    }

    @TypeConverter
    fun toRegisteredUserList(value: String?): List<RegisteredUser>? {
        val listType = object : TypeToken<List<RegisteredUser>>() {}.type
        return gson.fromJson(value, listType) // Convertir JSON de vuelta a lista de RegisteredUser
    }

    @TypeConverter
    fun fromPlayersList(players: List<PlayerDbo>?): String {
        return gson.toJson(players) // Convertir la lista de jugadores a JSON
    }

    @TypeConverter
    fun toPlayersList(playersJson: String?): List<PlayerDbo> {
        if (playersJson.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<PlayerDbo>>() {}.type
        return gson.fromJson(playersJson, type) // Convertir JSON de vuelta a lista de PlayerDbo
    }

    @TypeConverter
    fun userToString(list: ArrayList<UserDbo>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun stringToUsers(value: String): ArrayList<UserDbo> {
        val listType: Type = object : TypeToken<ArrayList<UserDbo>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun dateToString(date: Date): String {
        return gson.toJson(date)
    }

    @TypeConverter
    fun stringToDate(value: String): Date {
        return gson.fromJson(value, Date::class.java)
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromRuleList(value: List<Rule>?): String? {
        return gson.toJson(value) // Convertir la lista a JSON
    }

    @TypeConverter
    fun toRuleList(value: String?): List<Rule>? {
        val listType = object : TypeToken<List<Rule>>() {}.type
        return gson.fromJson(value, listType) // Convertir JSON de vuelta a lista de Rule
    }
}
