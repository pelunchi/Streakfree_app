package com.example.streakfreeapp.utils


object Constants {

    // SharedPreferences
    const val PREFS_NAME = "streak_prefs"
    const val PREF_USER_ID = "current_user_id"
    const val PREF_THEME = "theme_gradient"
    const val PREF_IS_LOGGED_IN = "is_logged_in"

    // Base de datos
    const val DATABASE_NAME = "StreakFree.db"
    const val DATABASE_VERSION = 1

    // Temas disponibles
    object Themes {
        const val PURPLE = 0
        const val BLUE = 1
        const val GREEN = 2
        const val ORANGE = 3
        const val PINK = 4
    }

    // Iconos predeterminados para adicciones
    val DEFAULT_ICONS = arrayOf(
        "🎮", "🍺", "🚬", "📱", "🍔",
        "🎲", "☕", "🍰", "🛒", "💻",
        "📺", "🎯", "🏃", "📚", "🎵"
    )

    // Nombres de adicciones comunes (para sugerencias)
    val COMMON_ADDICTIONS = arrayOf(
        "Videojuegos",
        "Alcohol",
        "Tabaco",
        "Redes Sociales",
        "Comida Chatarra",
        "Apuestas",
        "Cafeína",
        "Compras",
        "Procrastinación"
    )

    // Milisegundos en un día
    const val MILLIS_PER_DAY = 86400000L

    // Duración de animaciones
    const val ANIMATION_DURATION = 300L
}