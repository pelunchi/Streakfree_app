package com.example.streakfreeapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.streakfreeapp.R
import com.example.streakfreeapp.data.database.DatabaseHelper
import com.example.streakfreeapp.data.models.Addiction
import com.example.streakfreeapp.ui.activities.LoginActivity
import com.example.streakfreeapp.ui.adapters.AddictionAdapter
import com.example.streakfreeapp.utils.Constants
import com.example.streakfreeapp.utils.PreferencesManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

class ProfileFragment : Fragment() {

    private lateinit var tvUsername: MaterialTextView
    private lateinit var tvEmail: MaterialTextView
    private lateinit var btnAddAddiction: MaterialButton
    private lateinit var btnChangeTheme: MaterialButton
    private lateinit var btnLogout: MaterialButton

    private lateinit var addictionAdapter: AddictionAdapter
    private val addictions = mutableListOf<Addiction>()
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var prefsManager: PreferencesManager
    private var currentUserId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // ✅ Aplicar tema guardado al rootLayout del Fragment
        prefsManager = PreferencesManager(requireContext())
        val themeResId = prefsManager.getThemeDrawable()
        view.findViewById<ConstraintLayout>(R.id.rootLayout)
            .setBackgroundResource(themeResId)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())

        initViews(view)
        setupRecyclerView()      // 👈 Configura el adapter PRIMERO
        loadUserData()           // 👈 Luego carga los datos
        setupClickListeners()
    }

    private fun initViews(view: View) {
        tvUsername = view.findViewById(R.id.tvUsername)
        tvEmail = view.findViewById(R.id.tvEmail)
        btnAddAddiction = view.findViewById(R.id.btnAddAddiction)
        btnChangeTheme = view.findViewById(R.id.btnChangeTheme)
        btnLogout = view.findViewById(R.id.btnLogout)
    }

    private fun loadUserData() {
        currentUserId = prefsManager.getUserId()

        if (currentUserId != -1) {
            val user = dbHelper.getUser(currentUserId)
            user?.let {
                tvUsername.text = it.username
                tvEmail.text = it.email ?: "Sin email"
                addictions.clear()
                addictions.addAll(dbHelper.getUserAddictions(currentUserId))
                addictionAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setupRecyclerView() {
        addictionAdapter = AddictionAdapter(addictions) { addiction ->
            showEditAddictionDialog(addiction)
        }
        view?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerAddictions)
            ?.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = addictionAdapter
            }
    }

    private fun setupClickListeners() {
        btnAddAddiction.setOnClickListener {
            showAddAddictionDialog()
        }

        btnChangeTheme.setOnClickListener {
            showThemeSelector()
        }

        btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun showAddAddictionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_addiction, null)
        val etAddictionName = dialogView.findViewById<TextInputEditText>(R.id.etAddictionName)
        val etAddictionIcon = dialogView.findViewById<TextInputEditText>(R.id.etAddictionIcon)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Nueva Adicción")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val name = etAddictionName.text.toString()
                val icon = etAddictionIcon.text.toString()

                if (name.isNotEmpty()) {
                    val addiction = Addiction(
                        id = 0,
                        userId = currentUserId,
                        name = name,
                        icon = icon.ifEmpty { Constants.DEFAULT_ICONS.random() },
                        currentStreak = 0,
                        bestStreak = 0,
                        lastUpdate = System.currentTimeMillis()
                    )

                    val id = dbHelper.insertAddiction(addiction)
                    if (id > 0) {
                        addiction.id = id.toInt()
                        addictions.add(addiction)
                        addictionAdapter.notifyItemInserted(addictions.size - 1)
                        Toast.makeText(requireContext(), "Adicción agregada", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Ingresa un nombre", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditAddictionDialog(addiction: Addiction) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(addiction.name)
            .setItems(arrayOf("Editar", "Eliminar")) { _, which ->
                when (which) {
                    0 -> editAddiction(addiction)
                    1 -> deleteAddiction(addiction)
                }
            }
            .show()
    }

    private fun editAddiction(addiction: Addiction) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_addiction, null)
        val etAddictionName = dialogView.findViewById<TextInputEditText>(R.id.etAddictionName)
        val etAddictionIcon = dialogView.findViewById<TextInputEditText>(R.id.etAddictionIcon)

        etAddictionName.setText(addiction.name)
        etAddictionIcon.setText(addiction.icon)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Editar Adicción")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                addiction.name = etAddictionName.text.toString()
                addiction.icon = etAddictionIcon.text.toString()

                if (dbHelper.updateAddiction(addiction)) {
                    addictionAdapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Adicción actualizada", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteAddiction(addiction: Addiction) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Eliminar")
            .setMessage("¿Estás seguro de eliminar ${addiction.name}?")
            .setPositiveButton("Eliminar") { _, _ ->
                if (dbHelper.deleteAddiction(addiction.id)) {
                    val position = addictions.indexOf(addiction)
                    addictions.remove(addiction)
                    addictionAdapter.notifyItemRemoved(position)
                    Toast.makeText(requireContext(), "Adicción eliminada", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showThemeSelector() {
        val themes = arrayOf(
            "Degradado Morado",
            "Degradado Azul",
            "Degradado Verde",
            "Degradado Naranja",
            "Degradado Rosa"
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cambiar Tema")
            .setItems(themes) { _, which ->
                prefsManager.saveTheme(which)
                val themeResId = prefsManager.getThemeDrawable()

                // ✅ Aplicar el nuevo tema al Fragment actual
                view?.findViewById<ConstraintLayout>(R.id.rootLayout)
                    ?.setBackgroundResource(themeResId)

                Toast.makeText(requireContext(), "Tema cambiado", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun logout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                // Limpiar sesión
                prefsManager.clearAll()

                // Redirigir al login
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                requireContext().startActivity(intent)

                // Cerrar la Activity actual
                requireActivity().finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}