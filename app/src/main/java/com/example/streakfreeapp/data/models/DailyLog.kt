// data/models/DailyLog.kt
package com.example.streakfreeapp.data.models

import java.text.SimpleDateFormat
import java.util.*

data class DailyLog(
    var id: Int = 0,
    var addictionId: Int,
    var date: Long = System.currentTimeMillis(),
    var completed: Boolean = false,
    var failed: Boolean = false,
    var notes: String? = null
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(date))
    }

    fun getStatusText(): String {
        return when {
            completed -> "✅ Completado"
            failed -> "❌ Fallido"
            else -> "⏳ Pendiente"
        }
    }

    fun isToday(): Boolean {
        val today = Calendar.getInstance()
        val logDate = Calendar.getInstance().apply { timeInMillis = date }
        return today.get(Calendar.YEAR) == logDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == logDate.get(Calendar.DAY_OF_YEAR)
    }
}