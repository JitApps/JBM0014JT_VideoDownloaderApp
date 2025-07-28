package com.kraaft.video.manager.utils

import androidx.appcompat.app.AppCompatDelegate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeUtils @Inject constructor() {
    
    fun applyThemeMode(themeMode: String) {
        val nightMode = when (themeMode) {
            PreferenceClass.THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            PreferenceClass.THEME_NIGHT -> AppCompatDelegate.MODE_NIGHT_YES
            PreferenceClass.THEME_SYSTEM_DEFAULT -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
    
    fun getThemeModeDisplayName(themeMode: String): String {
        return when (themeMode) {
            PreferenceClass.THEME_LIGHT -> "Light mode"
            PreferenceClass.THEME_NIGHT -> "Night mode"
            PreferenceClass.THEME_SYSTEM_DEFAULT -> "System default"
            else -> "System default"
        }
    }
} 