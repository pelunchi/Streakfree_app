package com.example.streakfreeapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.graphics.drawable.GradientDrawable
import com.example.streakfreeapp.R
import com.example.streakfreeapp.data.database.DatabaseHelper
import com.example.streakfreeapp.data.models.User
import com.example.streakfreeapp.utils.PreferencesManager

class LoginActivity : AppCompatActivity() {

    // Vistas del Login
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var imageViewShowPassword: ImageView
    private lateinit var textViewForgotPassword: TextView

    // Vistas del Registro
    private lateinit var editTextName: EditText
    private lateinit var editTextEmailRegister: EditText
    private lateinit var editTextPasswordRegister: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var buttonRegister: Button
    private lateinit var imageViewShowPasswordRegister: ImageView
    private lateinit var imageViewShowConfirmPassword: ImageView

    // Vistas compartidas
    private lateinit var buttonLoginGoogle: Button
    private lateinit var buttonLoginFacebook: Button
    private lateinit var textViewTerms: TextView
    private lateinit var imageViewLogo: ImageView

    // Pestañas
    private lateinit var btnTabLogin: Button
    private lateinit var btnTabRegister: Button
    private lateinit var layoutLogin: LinearLayout
    private lateinit var layoutRegister: LinearLayout

    // Base de datos
    private lateinit var dbHelper: DatabaseHelper

    private var isPasswordVisible = false
    private var isPasswordRegisterVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si ya hay sesión activa
        val prefsManager = PreferencesManager(this)
        if (prefsManager.isLoggedIn()) {
            navigateToHome()
            return
        }

        setContentView(R.layout.activity_login)

        // Inicializar base de datos
        dbHelper = DatabaseHelper(this)

        initViews()
        setupClickListeners()
        setupGradientBackground()

        // Mostrar pestaña de login por defecto
        showLoginTab()
    }

    private fun initViews() {
        // Logo
        imageViewLogo = findViewById(R.id.imageViewLogo)

        // Pestañas
        btnTabLogin = findViewById(R.id.btnTabLogin)
        btnTabRegister = findViewById(R.id.btnTabRegister)
        layoutLogin = findViewById(R.id.layoutLogin)
        layoutRegister = findViewById(R.id.layoutRegister)

        // Login
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        imageViewShowPassword = findViewById(R.id.imageViewShowPassword)
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword)

        // Registro
        editTextName = findViewById(R.id.editTextName)
        editTextEmailRegister = findViewById(R.id.editTextEmailRegister)
        editTextPasswordRegister = findViewById(R.id.editTextPasswordRegister)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        buttonRegister = findViewById(R.id.buttonRegister)
        imageViewShowPasswordRegister = findViewById(R.id.imageViewShowPasswordRegister)
        imageViewShowConfirmPassword = findViewById(R.id.imageViewShowConfirmPassword)

        // Compartidas
        buttonLoginGoogle = findViewById(R.id.buttonLoginGoogle)
        buttonLoginFacebook = findViewById(R.id.buttonLoginFacebook)
        textViewTerms = findViewById(R.id.textViewTerms)
    }

    private fun setupClickListeners() {
        // Click en pestañas
        btnTabLogin.setOnClickListener {
            showLoginTab()
        }

        btnTabRegister.setOnClickListener {
            showRegisterTab()
        }

        //Login
        buttonLogin.setOnClickListener {
            val usernameOrEmail = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (validateLoginInputs(usernameOrEmail, password)) {
                performLogin(usernameOrEmail, password)
            }
        }

        textViewForgotPassword.setOnClickListener {
            Toast.makeText(this, "Función de recuperación en desarrollo", Toast.LENGTH_SHORT).show()
        }

        imageViewShowPassword.setOnClickListener {
            togglePasswordVisibility(editTextPassword, imageViewShowPassword, 0)
        }

        //Registro
        buttonRegister.setOnClickListener {
            val username = editTextName.text.toString().trim()
            val email = editTextEmailRegister.text.toString().trim()
            val password = editTextPasswordRegister.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()

            if (validateRegisterInputs(username, email, password, confirmPassword)) {
                performRegister(username, email, password)
            }
        }

        imageViewShowPasswordRegister.setOnClickListener {
            togglePasswordVisibility(editTextPasswordRegister, imageViewShowPasswordRegister, 1)
        }

        imageViewShowConfirmPassword.setOnClickListener {
            togglePasswordVisibility(editTextConfirmPassword, imageViewShowConfirmPassword, 2)
        }

        // Botones sociales
        buttonLoginGoogle.setOnClickListener {
            Toast.makeText(this, "Login con Google - Se acabo el presupuesto...", Toast.LENGTH_SHORT).show()
        }

        buttonLoginFacebook.setOnClickListener {
            Toast.makeText(this, "Login con Facebook - Se acabo el presupuesto...", Toast.LENGTH_SHORT).show()
        }
    }

    // ==================== FUNCIONES DE AUTENTICACIÓN ====================

    private fun performLogin(usernameOrEmail: String, password: String) {
        // Deshabilitar botón durante el proceso
        buttonLogin.isEnabled = false
        buttonLogin.text = "Iniciando sesión..."

        try {
            val user = dbHelper.getUserByCredentials(usernameOrEmail, password)

            if (user != null) {
                // Login exitoso
                saveUserSession(user)
                Toast.makeText(this, "¡Bienvenido ${user.username}!", Toast.LENGTH_SHORT).show()
                navigateToHome()
            } else {
                // Credenciales incorrectas
                Toast.makeText(this, "Email/usuario o contraseña incorrectos", Toast.LENGTH_LONG).show()
                buttonLogin.isEnabled = true
                buttonLogin.text = "Iniciar Sesión"
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_LONG).show()
            buttonLogin.isEnabled = true
            buttonLogin.text = "Iniciar Sesión"
        }
    }

    private fun performRegister(username: String, email: String, password: String) {
        // Deshabilitar botón durante el proceso
        buttonRegister.isEnabled = false
        buttonRegister.text = "Registrando..."

        try {
            // Verificar si el username ya existe
            if (dbHelper.usernameExists(username)) {
                Toast.makeText(this, "El nombre de usuario ya está en uso", Toast.LENGTH_LONG).show()
                buttonRegister.isEnabled = true
                buttonRegister.text = "Registrarse"
                return
            }

            // Verificar si el email ya existe
            if (dbHelper.emailExists(email)) {
                Toast.makeText(this, "El email ya está registrado", Toast.LENGTH_LONG).show()
                buttonRegister.isEnabled = true
                buttonRegister.text = "Registrarse"
                return
            }

            val newUser = User(
                username = username,
                password = password,
                email = email,
                createdAt = System.currentTimeMillis()
            )

            val userId = dbHelper.insertUser(newUser)

            if (userId > 0) {
                // Registro exitoso
                Toast.makeText(this, "¡Cuenta creada exitosamente!", Toast.LENGTH_SHORT).show()

                // Cambiar a pestaña de login
                showLoginTab()

                // Pre-llenar los campos de login con el email o username
                editTextEmail.setText(username)
                editTextPassword.setText(password)

                buttonRegister.isEnabled = true
                buttonRegister.text = "Registrarse"
            } else {
                Toast.makeText(this, "Error al crear la cuenta", Toast.LENGTH_LONG).show()
                buttonRegister.isEnabled = true
                buttonRegister.text = "Registrarse"
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            buttonRegister.isEnabled = true
            buttonRegister.text = "Registrarse"
        }
    }

    // ==================== GESTIÓN DE SESIÓN ====================

    private fun saveUserSession(user: User) {
        val prefsManager = PreferencesManager(this)
        prefsManager.saveUserId(user.id)
        prefsManager.setLoggedIn(true)
    }

    private fun navigateToHome() {
        val intent = Intent(this, main::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // ==================== FUNCIONES DE PESTAÑAS ====================

    private fun showLoginTab() {
        // Si ya está visible, no hacer nada
        if (layoutLogin.visibility == View.VISIBLE) return

        // Animar salida del registro
        layoutRegister.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.slide_out_right))
        layoutRegister.visibility = View.GONE

        // Animar entrada del login
        layoutLogin.visibility = View.VISIBLE
        layoutLogin.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.slide_in_left))

        // Cambiar apariencia de pestañas con animación
        animateTabSelection(btnTabLogin, btnTabRegister)
    }

    private fun showRegisterTab() {
        // Si ya está visible, no hacer nada
        if (layoutRegister.visibility == View.VISIBLE) return

        // Animar salida del login
        layoutLogin.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.slide_out_left))
        layoutLogin.visibility = View.GONE

        // Animar entrada del registro
        layoutRegister.visibility = View.VISIBLE
        layoutRegister.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.slide_in_right))

        // Cambiar apariencia de pestañas con animación
        animateTabSelection(btnTabRegister, btnTabLogin)
    }

    private fun animateTabSelection(selectedTab: Button, unselectedTab: Button) {
        // Animar tab seleccionado
        selectedTab.animate()
            .alpha(1.0f)
            .setDuration(200)
            .start()
        selectedTab.setBackgroundResource(R.drawable.boton_redondeado)
        selectedTab.setTextColor(ContextCompat.getColor(this, android.R.color.white))

        // Animar tab no seleccionado
        unselectedTab.animate()
            .alpha(0.7f)
            .setDuration(200)
            .start()
        unselectedTab.setBackgroundResource(android.R.color.transparent)
        unselectedTab.setTextColor(ContextCompat.getColor(this, android.R.color.white))
    }

    // ==================== VALIDACIONES ====================

    private fun validateLoginInputs(usernameOrEmail: String, password: String): Boolean {
        if (usernameOrEmail.isEmpty()) {
            editTextEmail.error = "Ingrese su email o nombre de usuario"
            return false
        }

        if (password.isEmpty()) {
            editTextPassword.error = "Ingrese su contraseña"
            return false
        }

        if (password.length < 6) {
            editTextPassword.error = "La contraseña debe tener al menos 6 caracteres"
            return false
        }

        return true
    }

    private fun validateRegisterInputs(username: String, email: String, password: String, confirmPassword: String): Boolean {
        if (username.isEmpty()) {
            editTextName.error = "Ingrese su nombre de usuario"
            return false
        }

        if (username.length < 3) {
            editTextName.error = "El nombre debe tener al menos 3 caracteres"
            return false
        }

        if (email.isEmpty()) {
            editTextEmailRegister.error = "Ingrese su correo electrónico"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmailRegister.error = "Correo electrónico inválido"
            return false
        }

        if (password.isEmpty()) {
            editTextPasswordRegister.error = "Ingrese su contraseña"
            return false
        }

        if (password.length < 6) {
            editTextPasswordRegister.error = "La contraseña debe tener al menos 6 caracteres"
            return false
        }

        if (confirmPassword.isEmpty()) {
            editTextConfirmPassword.error = "Confirme su contraseña"
            return false
        }

        if (password != confirmPassword) {
            editTextConfirmPassword.error = "Las contraseñas no coinciden"
            return false
        }

        return true
    }

    private fun togglePasswordVisibility(editText: EditText, imageView: ImageView, type: Int) {
        val isVisible = when (type) {
            0 -> {
                isPasswordVisible = !isPasswordVisible
                isPasswordVisible
            }
            1 -> {
                isPasswordRegisterVisible = !isPasswordRegisterVisible
                isPasswordRegisterVisible
            }
            else -> {
                isConfirmPasswordVisible = !isConfirmPasswordVisible
                isConfirmPasswordVisible
            }
        }

        if (isVisible) {
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            imageView.setImageResource(android.R.drawable.ic_menu_view)
        } else {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            imageView.setImageResource(android.R.drawable.ic_partial_secure)
        }
        editText.setSelection(editText.text.length)
    }

    private fun setupGradientBackground() {
        val mainLayout = findViewById<View>(R.id.mainLayout)
        val gradient = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            orientation = GradientDrawable.Orientation.TOP_BOTTOM
            colors = intArrayOf(
                ContextCompat.getColor(this@LoginActivity, android.R.color.holo_blue_light),
                ContextCompat.getColor(this@LoginActivity, android.R.color.holo_purple)
            )
        }
        mainLayout.background = gradient
    }
}