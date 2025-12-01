package com.example.streakfreeapp.ui.activities

import com.example.streakfreeapp.R
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class splash_screen : AppCompatActivity() {

    private var hasSplashShown = false
    private val SPLASH_DELAY: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        //Animacion del logo
        animateLogo()

        //Que el Login aparezca después del delay
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserSession()
        }, SPLASH_DELAY)
    }

    private fun checkUserSession() {
        // Aquí verificarías si el usuario ya tiene sesión
        val hasSession = false // Cambiar por tu lógica real

        if (hasSession) {
            // Si ya inició sesión, ir directo al MainActivity
            val intent = Intent(this, main::class.java)
            startActivity(intent)
        } else {
            goToLogin()
        }
        finish()
    }


    private fun animateLogo() {
        val logo = findViewById<android.view.View>(R.id.splashLogo)
        val subtitle = findViewById<android.view.View>(R.id.appSubtitle)

        // Fade in animation
        logo.alpha = 0f
        subtitle.alpha = 0f

        logo.animate()
            .alpha(1f)
            .setDuration(1000)
            .start()

        subtitle.animate()
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(600)
            .start()
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java) // Cambia "Login" por el nombre de tu LoginActivity
        startActivity(intent)
        finish() // Cerrar el splash_screen
    }
}