package com.dedany.secretgift.presentation.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ItemApiGameListBinding
import com.dedany.secretgift.databinding.ItemGameListBinding
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.entities.GameSummary

class GamesAdapter(
    private val onGameClick: (String) -> Unit,
    private val onGameDelete: (Game, Int) -> Unit
) : ListAdapter<GameSummary, GamesAdapter.GameViewHolder>(ListAdapterCallback()) {

    inner class GameViewHolder(private val binding: ItemApiGameListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(game: GameSummary, position: Int) {
            binding.tvApiGameName.text = game.name

            binding.tvApiGameName.setTextColor(
                ContextCompat.getColor(binding.root.context, R.color.brown)
            )

            binding.root.setOnClickListener {
                onGameClick(game.accessCode)
            }
        }
    }

    class ListAdapterCallback : DiffUtil.ItemCallback<GameSummary>() {
        override fun areItemsTheSame(oldItem: GameSummary, newItem: GameSummary): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GameSummary, newItem: GameSummary): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding =
            ItemApiGameListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
}
