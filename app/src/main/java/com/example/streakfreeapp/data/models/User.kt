package com.example.streakfreeapp.data.models


data class User(
    var id: Int = 0,
    var username: String,
    var password: String,
    var email: String? = null,
    var createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Valida si el usuario tiene datos completos
     */
    fun isValid(): Boolean {
        return username.isNotEmpty() && password.isNotEmpty()
    }

    /**
     * Retorna el usuario sin la contraseña para seguridad
     */
    fun toSafeUser(): User {
        return this.copy(password = "***")
    }
}