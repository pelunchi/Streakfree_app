package com.example.streakfreeapp.ui.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.streakfreeapp.R
import com.example.streakfreeapp.data.models.Addiction
import com.google.android.material.card.MaterialCardView

class AddictionAdapter(
    private val addictions: List<Addiction>,
    private val onItemClick: (Addiction) -> Unit
) : RecyclerView.Adapter<AddictionAdapter.AddictionViewHolder>() {

    inner class AddictionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: MaterialCardView = view.findViewById(R.id.cardAddiction)
        val tvIcon: TextView = view.findViewById(R.id.tvIcon)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvCurrentStreak: TextView = view.findViewById(R.id.tvCurrentStreak)
        val tvBestStreak: TextView = view.findViewById(R.id.tvBestStreak)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddictionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_addiction, parent, false)
        return AddictionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddictionViewHolder, position: Int) {
        val addiction = addictions[position]

        holder.tvIcon.text = addiction.icon
        holder.tvName.text = addiction.name
        holder.tvCurrentStreak.text = "${addiction.currentStreak} días"
        holder.tvBestStreak.text = "Mejor: ${addiction.bestStreak} días"

        holder.cardView.setOnClickListener {
            onItemClick(addiction)
        }
    }

    override fun getItemCount() = addictions.size
}