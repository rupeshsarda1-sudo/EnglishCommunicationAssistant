package com.rupesh.englishassistant.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rupesh.englishassistant.databinding.ItemHistoryBinding
import com.rupesh.englishassistant.model.HistoryEntry
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter : ListAdapter<HistoryEntry, HistoryAdapter.HistoryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: HistoryEntry) {
            binding.tvOriginal.text = entry.originalText
            binding.tvLanguage.text = entry.language
            binding.tvCasual.text = "💬 ${entry.casualSuggestion}"
            binding.tvProfessional.text = "🧑‍💼 ${entry.professionalSuggestion}"
            val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
            binding.tvTimestamp.text = sdf.format(Date(entry.timestamp))
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<HistoryEntry>() {
        override fun areItemsTheSame(old: HistoryEntry, new: HistoryEntry) = old.id == new.id
        override fun areContentsTheSame(old: HistoryEntry, new: HistoryEntry) = old == new
    }
}
