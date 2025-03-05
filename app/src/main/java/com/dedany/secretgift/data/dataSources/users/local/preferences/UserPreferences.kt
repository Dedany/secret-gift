package com.dedany.secretgift.data.dataSources.users.local.preferences

import android.content.SharedPreferences
import javax.inject.Inject

private const val USER_EMAIL = "userEmail"

class UserPreferences @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    fun clear() = sharedPreferences.edit()?.clear()?.apply()

    fun getUserEmail(): String {
        return sharedPreferences.getString(USER_EMAIL, "").orEmpty()
    }

    fun setUserEmail(userEmail: String) {
        sharedPreferences.edit()?.putString(USER_EMAIL, userEmail)?.apply()
    }
}
