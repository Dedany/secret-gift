package com.dedany.secretgift.data.dataSources.users.local.preferences

import android.content.SharedPreferences
import javax.inject.Inject

private const val USER_ID = "userId"

class UserPreferences @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    fun clear() = sharedPreferences.edit()?.clear()?.apply()

    fun getUserId(): String {
        return sharedPreferences.getString(USER_ID, "").orEmpty()
    }

    fun setUserId(userId: String) {
        sharedPreferences.edit()?.putString(USER_ID, userId)?.apply()
    }
}
