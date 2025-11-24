package com.example.streakfreeapp.utils


import android.content.Context
import android.content.SharedPreferences
import com.example.streakfreeapp.R


class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREFS_NAME,
        Context.MODE_PRIVATE
    )

    // ==================== USUARIO ====================

    fun saveUserId(userId: Int) {
        prefs.edit().putInt(Constants.PREF_USER_ID, userId).apply()
    }

    fun getUserId(): Int {
        return prefs.getInt(Constants.PREF_USER_ID, -1)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(Constants.PREF_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(Constants.PREF_IS_LOGGED_IN, false)
    }

    // ==================== TEMA ====================

    fun saveTheme(themeId: Int) {
        prefs.edit().putInt(Constants.PREF_THEME, themeId).apply()
    }

    fun getTheme(): Int {
        return prefs.getInt(Constants.PREF_THEME, Constants.Themes.PURPLE)
    }

    fun getThemeDrawable(): Int {
        return when (getTheme()) {
            Constants.Themes.BLUE -> R.drawable.gradient_blue
            Constants.Themes.GREEN -> R.drawable.gradient_green
            Constants.Themes.ORANGE -> R.drawable.gradient_orange
            Constants.Themes.PINK -> R.drawable.gradient_pink
            else -> R.drawable.gradient_purple
        }
    }

    // ==================== GENERAL ====================

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    fun saveString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }

    fun saveBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }
}