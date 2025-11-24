package com.example.streakfreeapp.data.models


import java.util.concurrent.TimeUnit

/**
 * Modelo de datos para las Adicciones
 */
data class Addiction(
    var id: Int = 0,
    var userId: Int,
    var name: String,
    var icon: String = "🎯",
    var currentStreak: Int = 0,
    var bestStreak: Int = 0,
    var lastUpdate: Long = System.currentTimeMillis(),
    var isActive: Boolean = true
) {
    /**
     * Verifica si la racha sigue activa (actualizada en las últimas 24 horas)
     */
    fun isStreakActive(): Boolean {
        val currentTime = System.currentTimeMillis()
        val diffInMillis = currentTime - lastUpdate
        val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        return diffInHours <= 24
    }

    /**
     * Incrementa la racha actual
     */
    fun incrementStreak() {
        currentStreak++
        if (currentStreak > bestStreak) {
            bestStreak = currentStreak
        }
        lastUpdate = System.currentTimeMillis()
    }

    /**
     * Resetea la racha actual (recaída)
     */
    fun resetStreak() {
        currentStreak = 0
        lastUpdate = System.currentTimeMillis()
    }

    /**
     * Obtiene el número de días desde la última actualización
     */
    fun getDaysSinceLastUpdate(): Long {
        val currentTime = System.currentTimeMillis()
        val diffInMillis = currentTime - lastUpdate
        return TimeUnit.MILLISECONDS.toDays(diffInMillis)
    }
}