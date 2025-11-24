package com.example.streakfreeapp.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.streakfreeapp.R
import com.example.streakfreeapp.ui.fragments.HomeFragment
import com.example.streakfreeapp.ui.fragments.ProfileFragment
import com.example.streakfreeapp.utils.PreferencesManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class main : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var prefsManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ 1. Inicializa PreferencesManager PRIMERO
        prefsManager = PreferencesManager(this)

        // ✅ 2. Carga el tema guardado ANTES de setContentView
        loadSavedTheme()

        // ✅ 3. Infla el layout
        setContentView(R.layout.frame_layout)

        // ✅ 4. Encuentra las vistas
        bottomNav = findViewById(R.id.bottomNav)

        // Cargar fragment inicial
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        setupBottomNavigation()
    }

    private fun loadSavedTheme() {
        // ✅ Usa setBackgroundDrawableResource (más eficiente)
        val themeResId = prefsManager.getThemeDrawable()
        window.setBackgroundDrawableResource(themeResId)
    }

    private fun setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}