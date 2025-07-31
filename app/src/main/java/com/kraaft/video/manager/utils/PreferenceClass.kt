package com.kraaft.video.manager.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceClass @Inject constructor(@ApplicationContext context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app.PhoneCallPref", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    private val gson = Gson()

    // Generic getter and setter methods
    fun getBoolean(key: String, default: Boolean = false): Boolean =
        sharedPreferences.getBoolean(key, default)

    private fun setBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun getInteger(key: String, default: Int = 0): Int =
        sharedPreferences.getInt(key, default)

    fun setInteger(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    fun getString(key: String, default: String = ""): String =
        sharedPreferences.getString(key, default) ?: ""

    fun setString(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    fun getFloat(key: String, default: Float = 0f): Float =
        sharedPreferences.getFloat(key, default)

    fun setFloat(key: String, value: Float) {
        editor.putFloat(key, value).apply()
    }

    fun clearAll() {
        editor.clear().apply()
    }

}