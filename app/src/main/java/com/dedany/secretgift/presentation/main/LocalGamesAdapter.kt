package com.dedany.secretgift.presentation.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ItemGameListBinding
import com.dedany.secretgift.domain.entities.LocalGame

class LocalGamesAdapter(
    private val onGameClick: (LocalGame, Int) -> Unit,
    private val onGameDelete: (Int, Int) -> Unit
) : ListAdapter<LocalGame, LocalGamesAdapter.GameViewHolder>(GameDiffCallback()) {

    inner class GameViewHolder(private val binding: ItemGameListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(game: LocalGame, position: Int) {
            binding.tvGameName.text = game.name

            binding.tvGameName.setTextColor(
                ContextCompat.getColor(binding.root.context, R.color.blue)
            )
            binding.ibMainDelete.setOnClickListener {
                onGameDelete(game.id, position)
            }

            binding.root.setOnClickListener {
                onGameClick(game, position)
            }
        }
    }

    class GameDiffCallback : DiffUtil.ItemCallback<LocalGame>() {
        override fun areItemsTheSame(oldItem: LocalGame, newItem: LocalGame): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LocalGame, newItem: LocalGame): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding =
            ItemGameListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
}