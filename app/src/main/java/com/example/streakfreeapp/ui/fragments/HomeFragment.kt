package com.example.streakfreeapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.streakfreeapp.R
import com.example.streakfreeapp.data.database.DatabaseHelper
import com.example.streakfreeapp.data.models.Addiction
import com.example.streakfreeapp.data.models.DailyLog
import com.example.streakfreeapp.utils.PreferencesManager
import java.util.Calendar
import java.util.concurrent.TimeUnit
import android.widget.ScrollView
import android.widget.LinearLayout

class HomeFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var prefsManager: PreferencesManager
    private var currentUserId: Int = -1
    private var currentAddiction: Addiction? = null

    private val weekLetters = listOf("L", "M", "MI", "J", "V", "S", "D")

    // Views principales
    private lateinit var txtMainTitle: TextView
    private lateinit var emojiOne: TextView
    private lateinit var emojiTwo: TextView
    private lateinit var txtCurrentStreak: TextView
    private lateinit var txtBestStreak: TextView
    private lateinit var txtTotalDays: TextView
    private lateinit var btnCompleted: CardView
    private lateinit var btnFailed: CardView
    private lateinit var homeScrollView: ScrollView

    // Contenedor de tarjetas secundarias (scrollable)
    private lateinit var secondaryHabitsContainer: LinearLayout

    // Logros
    private lateinit var itemAchievementOne: View
    private lateinit var itemAchievementTwo: View
    private lateinit var itemAchievementThree: View
    private lateinit var itemAchievementFour: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        applyTheme()
        dbHelper = DatabaseHelper(requireContext())
        prefsManager = PreferencesManager(requireContext())
        currentUserId = prefsManager.getUserId()

        if (currentUserId == -1) {
            requireActivity().finish()
            requireActivity().startActivity(requireActivity().intent.setClassName(
                requireContext(),
                "com.example.streakfreeapp.ui.activities.LoginActivity"
            ))
            return
        }

        loadAddictions()
        updateDayIndicators()
        setupClickListeners()
        updateGreeting()
    }

    private fun initViews(view: View) {
        homeScrollView = view.findViewById(R.id.homeScrollView)
        txtMainTitle = view.findViewById(R.id.txt_main_title)
        emojiOne = view.findViewById(R.id.emoji_one)
        emojiTwo = view.findViewById(R.id.emoji_two)
        txtCurrentStreak = view.findViewById(R.id.txt_current_streak)
        txtBestStreak = view.findViewById(R.id.txt_best_streak)
        txtTotalDays = view.findViewById(R.id.txt_total_days)
        btnCompleted = view.findViewById(R.id.btn_completed)
        btnFailed = view.findViewById(R.id.btn_failed)

        secondaryHabitsContainer = view.findViewById(R.id.secondaryHabitsContainer)

        itemAchievementOne = view.findViewById(R.id.item_achievement_one)
        itemAchievementTwo = view.findViewById(R.id.item_achievement_two)
        itemAchievementThree = view.findViewById(R.id.item_achievement_three)
        itemAchievementFour = view.findViewById(R.id.item_achievement_four)
    }

    private fun applyTheme() {
        val prefsManager = PreferencesManager(requireContext())
        val themeResId = prefsManager.getThemeDrawable()
        requireActivity().window.setBackgroundDrawableResource(themeResId)
        homeScrollView.setBackgroundResource(themeResId)
    }

    private fun loadAddictions() {
        val addictions = dbHelper.getUserAddictions(currentUserId)
        if (addictions.isEmpty()) {
            Toast.makeText(requireContext(), "Agrega una adicción en tu perfil", Toast.LENGTH_LONG).show()
            return
        }

        currentAddiction = addictions.first()
        updateMainCard(currentAddiction!!)
        updateSecondaryCards(addictions)
        updateAchievements(currentAddiction!!)
    }

    private fun updateMainCard(addiction: Addiction) {
        txtMainTitle.text = addiction.name
        emojiOne.text = addiction.icon
        emojiTwo.text = "🎯"
        txtCurrentStreak.text = addiction.currentStreak.toString()
        txtBestStreak.text = "${addiction.bestStreak} días"
        val totalDays = dbHelper.getAddictionLogs(addiction.id).size
        txtTotalDays.text = "$totalDays días"
    }

    private fun updateSecondaryCards(addictions: List<Addiction>) {
        // Limpiar el contenedor
        secondaryHabitsContainer.removeAllViews()

        addictions.forEach { addiction ->
            val card = createAddictionCard(addiction)
            secondaryHabitsContainer.addView(card)
        }
    }

    private fun createAddictionCard(addiction: Addiction): CardView {
        val card = CardView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                120.dpToPx(requireContext()),
                136.dpToPx(requireContext())
            ).apply {
                marginStart = 8.dpToPx(requireContext())
                marginEnd = 8.dpToPx(requireContext())
            }
            radius = 16f
        }

        // Crear layout interno
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setPadding(12.dpToPx(requireContext()), 12.dpToPx(requireContext()), 12.dpToPx(requireContext()), 12.dpToPx(requireContext()))
        }

        // Icono (emoji)
        val iconText = TextView(requireContext()).apply {
            text = addiction.icon
            textSize = 24f
            gravity = android.view.Gravity.CENTER
        }

        // Nombre de la adicción
        val nameText = TextView(requireContext()).apply {
            text = addiction.name
            textSize = 12f
            maxLines = 2
            ellipsize = android.text.TextUtils.TruncateAt.END
            gravity = android.view.Gravity.CENTER
        }

        // Streak de la adicción
        val streakText = TextView(requireContext()).apply {
            text = if (addiction.currentStreak > 0) {
                "${addiction.currentStreak} día${if (addiction.currentStreak > 1) "s" else ""}"
            } else {
                "Racha perdida"
            }
            textSize = 10f
            gravity = android.view.Gravity.CENTER
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }

        layout.addView(iconText)
        layout.addView(nameText)
        layout.addView(streakText)
        card.addView(layout)

        // ✅ Usar tus drawables exactos
        if (currentAddiction?.id == addiction.id) {
            card.background = ContextCompat.getDrawable(requireContext(), R.drawable.selected_card)
            // Cambiar texto a blanco para mejor contraste
            iconText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            nameText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            streakText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        } else {
            card.background = ContextCompat.getDrawable(requireContext(), R.drawable.habit_card_default)
            // Texto negro para fondo blanco
            iconText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            nameText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            streakText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }

        // Click listener
        card.setOnClickListener {
            currentAddiction = addiction
            updateMainCard(addiction)
            updateAchievements(addiction)
            updateSecondaryCards(dbHelper.getUserAddictions(currentUserId))
            updateDayIndicators()
            Toast.makeText(requireContext(), "Seleccionada: ${addiction.name}", Toast.LENGTH_SHORT).show()
        }

        return card
    }

    private fun updateAchievements(addiction: Addiction) {
        val achievements = listOf(
            addiction.bestStreak >= 7,
            addiction.bestStreak >= 14,
            addiction.bestStreak >= 30,
            addiction.bestStreak >= 60
        )

        val achievementItems = listOf(
            itemAchievementOne,
            itemAchievementTwo,
            itemAchievementThree,
            itemAchievementFour
        )

        achievementItems.forEachIndexed { index, item ->
            val card = item.findViewById<CardView>(R.id.achievement_card)
            val emoji = item.findViewById<TextView>(R.id.achievement_emoji)
            val label = item.findViewById<TextView>(R.id.achievement_label)

            if (achievements[index]) {
                card.setCardBackgroundColor(requireContext().getColor(R.color.gray_background))
                emoji.alpha = 1f
                label.alpha = 1f
            } else {
                card.setCardBackgroundColor(requireContext().getColor(R.color.purple_badge_locked))
                emoji.alpha = 0.3f
                label.alpha = 0.3f
            }
        }
    }

    private fun setupClickListeners() {
        btnCompleted.setOnClickListener {
            completeCurrentDay()
        }
        btnFailed.setOnClickListener {
            failCurrentDay()
        }
    }

    private fun completeCurrentDay() {
        val addiction = currentAddiction ?: return

        if (hasTodayLog(addiction.id)) {
            Toast.makeText(requireContext(), "Ya registraste una acción hoy para esta adicción", Toast.LENGTH_SHORT).show()
            showTimeRemainingToast()
            return
        }

        addiction.incrementStreak()
        dbHelper.updateAddiction(addiction)

        val log = DailyLog(
            addictionId = addiction.id,
            date = System.currentTimeMillis(),
            completed = true,
            failed = false
        )
        dbHelper.insertDailyLog(log)

        updateMainCard(addiction)
        updateSecondaryCards(dbHelper.getUserAddictions(currentUserId))
        updateAchievements(addiction)
        updateDayIndicators()
        Toast.makeText(requireContext(), "¡Día completado!", Toast.LENGTH_SHORT).show()
        showTimeRemainingToast()
    }

    private fun failCurrentDay() {
        val addiction = currentAddiction ?: return

        if (hasTodayLog(addiction.id)) {
            Toast.makeText(requireContext(), "Ya registraste una acción hoy para esta adicción", Toast.LENGTH_SHORT).show()
            showTimeRemainingToast()
            return
        }

        addiction.resetStreak()
        dbHelper.updateAddiction(addiction)

        val log = DailyLog(
            addictionId = addiction.id,
            date = System.currentTimeMillis(),
            completed = false,
            failed = true
        )
        dbHelper.insertDailyLog(log)

        updateMainCard(addiction)
        updateSecondaryCards(dbHelper.getUserAddictions(currentUserId))
        updateDayIndicators()
        Toast.makeText(requireContext(), "Día fallado", Toast.LENGTH_SHORT).show()
        showTimeRemainingToast()
    }

    private fun hasTodayLog(addictionId: Int): Boolean {
        val logs = dbHelper.getAddictionLogs(addictionId)
        return logs.any { isSameDay(it.date, System.currentTimeMillis()) }
    }

    private fun isSameDay(date1: Long, date2: Long): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.timeInMillis = date1
        cal2.timeInMillis = date2
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun updateDayIndicators() {
        val dayViews = listOf(
            requireView().findViewById<CardView>(R.id.day_l),
            requireView().findViewById<CardView>(R.id.day_m),
            requireView().findViewById<CardView>(R.id.day_mi),
            requireView().findViewById<CardView>(R.id.day_j),
            requireView().findViewById<CardView>(R.id.day_v),
            requireView().findViewById<CardView>(R.id.day_s),
            requireView().findViewById<CardView>(R.id.day_d)
        )

        val calendar = Calendar.getInstance()
        val todayIndex = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }

        val logs = currentAddiction?.let { dbHelper.getAddictionLogs(it.id) } ?: emptyList()

        dayViews.forEachIndexed { index, card ->
            val letter = weekLetters[index]
            val textView = card.findViewById<TextView>(R.id.day_text)
            val checkIcon = card.findViewById<TextView>(R.id.check_icon)

            if (currentAddiction != null) {
                val dayLog = logs.firstOrNull { log ->
                    val logCalendar = Calendar.getInstance().apply { timeInMillis = log.date }
                    val logIndex = when (logCalendar.get(Calendar.DAY_OF_WEEK)) {
                        Calendar.MONDAY -> 0
                        Calendar.TUESDAY -> 1
                        Calendar.WEDNESDAY -> 2
                        Calendar.THURSDAY -> 3
                        Calendar.FRIDAY -> 4
                        Calendar.SATURDAY -> 5
                        Calendar.SUNDAY -> 6
                        else -> -1
                    }
                    logIndex == index
                }

                when {
                    dayLog?.completed == true -> {
                        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_completed))
                        textView.visibility = View.GONE
                        checkIcon.visibility = View.VISIBLE
                        checkIcon.text = "✔"
                    }
                    dayLog?.failed == true -> {
                        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red_failed))
                        textView.visibility = View.GONE
                        checkIcon.visibility = View.VISIBLE
                        checkIcon.text = "✖"
                    }
                    index == todayIndex -> {
                        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray_background))
                        textView.visibility = View.VISIBLE
                        textView.text = letter
                        textView.alpha = 1f
                        checkIcon.visibility = View.GONE
                    }
                    else -> {
                        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_inactive))
                        textView.visibility = View.VISIBLE
                        textView.text = letter
                        textView.alpha = 0.5f
                        checkIcon.visibility = View.GONE
                    }
                }
            } else {
                card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_inactive))
                textView.visibility = View.VISIBLE
                textView.text = letter
                textView.alpha = 0.5f
                checkIcon.visibility = View.GONE
            }
        }
    }

    private fun updateGreeting() {
        val user = dbHelper.getUser(currentUserId)
        val userName = user?.username ?: "usuario"
        val greetingText = "¡Hola, $userName!"
        val textViewGreeting = view?.findViewById<TextView>(R.id.textViewGreeting)
        textViewGreeting?.text = greetingText
    }

    private fun showTimeRemainingToast() {
        val now = System.currentTimeMillis()
        val tomorrowStart = getStartOfNextDay(now)
        val diffMillis = tomorrowStart - now

        if (diffMillis <= 0) {
            Toast.makeText(requireContext(), "¡Hoy puedes registrar tu progreso!", Toast.LENGTH_SHORT).show()
            return
        }

        val hours = TimeUnit.MILLISECONDS.toHours(diffMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis) % 60

        val hourText = if (hours == 1L) "hora" else "horas"
        val minuteText = if (minutes == 1L) "minuto" else "minutos"

        val message = "⏰ Faltan $hours $hourText y $minutes $minuteText para el próximo día"
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun getStartOfNextDay(currentTime: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = currentTime
        cal.add(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}

// Extension function para convertir dp a px
fun Int.dpToPx(context: android.content.Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}