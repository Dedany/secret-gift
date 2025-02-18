package com.dedany.secretgift.data.dataSources.games

import androidx.room.TypeConverter
import com.dedany.secretgift.data.dataSources.games.users.local.UserDbo.UserDbo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun userToString(list: ArrayList<UserDbo>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun stringToUsers(value: String): ArrayList<UserDbo> {
        val listType: Type = object : TypeToken<ArrayList<UserDbo>>() {}.type // import preguntar
        val gson = Gson()
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun dateToString(date: LocalDate): String {
        val gson = Gson()
        return gson.toJson(date)
    }

    @TypeConverter
    fun stringToDate(value: String): LocalDate {
        val gson = Gson()
        return gson.fromJson(value, LocalDate::class.java)
    }

}

