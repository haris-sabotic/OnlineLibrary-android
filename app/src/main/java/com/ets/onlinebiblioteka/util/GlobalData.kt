package com.ets.onlinebiblioteka.util

import android.content.Context
import android.content.SharedPreferences

object GlobalData {
    private lateinit var sharedPreferences: SharedPreferences

    private var authToken: String? = null
    fun getToken(): String? {
        return if (authToken == null) {
            sharedPreferences.getString("authToken", null)
        } else {
            authToken
        }
    }
    fun setToken(token: String, saveToSharedPrefs: Boolean) {
        authToken = token
        if (saveToSharedPrefs) {
            with (sharedPreferences.edit()) {
                putString("authToken", authToken)
                commit()
            }
        }
    }
    fun clearToken() {
        authToken = null
        with (sharedPreferences.edit()) {
            remove("authToken")
            commit()
        }
    }

    fun loadSharedPreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences("OnlineLibrary", Context.MODE_PRIVATE)
    }

    fun getSharedPreferences(): SharedPreferences {
        return sharedPreferences
    }

    fun getImageUrl(name: String): String {
        return "https://tim7.ictcortex.me/storage/image/${name}"
    }
}