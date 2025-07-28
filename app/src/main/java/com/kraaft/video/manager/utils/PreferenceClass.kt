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
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
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

    private fun getString(key: String, default: String? = null): String? =
        sharedPreferences.getString(key, default)

    private fun setString(key: String, value: String?) {
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

    fun getSelectedLanguage(): String = getString(SELECTED_LANGUAGE, "--") ?: "--"
    fun setSelectedLanguage(language: String) = setString(SELECTED_LANGUAGE, language)

    fun setLastSystemLanguage(language: String?) = setString(LAST_SYSTEM_LANGUAGE, language)
    fun setLanguageShown(shown: Boolean) = setBoolean(SHOW_LANGUAGE, shown)

    // List<String> preferences
    fun setStringList(key: String, list: List<String>) {
        val json = gson.toJson(list)
        setString(key, json)
    }

    fun getStringList(key: String): List<String> {
        val json = getString(key, null)
        return if (json != null) gson.fromJson(json, Array<String>::class.java).toList() else emptyList()
    }

    // Theme mode preferences
    fun getThemeMode(): String = getString(THEME_MODE, THEME_SYSTEM_DEFAULT) ?: THEME_SYSTEM_DEFAULT
    fun setThemeMode(themeMode: String) = setString(THEME_MODE, themeMode)

    companion object {
        const val PREF_NAME: String = "app.PhoneCallPref"
        const val SHOW_LANGUAGE = "show_language"
        const val SELECTED_LANGUAGE = "selected_language"
        const val LAST_SYSTEM_LANGUAGE = "last_system_language"


        // Theme mode constants
        const val THEME_MODE = "theme_mode"
        const val THEME_LIGHT = "light"
        const val THEME_NIGHT = "night"
        const val THEME_SYSTEM_DEFAULT = "system_default"
    }
}