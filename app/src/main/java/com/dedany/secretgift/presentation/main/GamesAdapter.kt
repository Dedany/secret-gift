package com.dedany.secretgift.presentation.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dedany.secretgift.databinding.ItemGameListBinding
import com.dedany.secretgift.domain.entities.Game

class GamesAdapter : ListAdapter<Game, GamesAdapter.GameViewHolder>(ListAdapterCallback()) {

    inner class GameViewHolder(
        private val binding: ItemGameListBinding
    ) : ViewHolder(binding.root) {
        fun bind(game: Game) {
            binding.tvGameName.text = game.name
        }
    }

    class ListAdapterCallback: DiffUtil.ItemCallback<Game>(){
        override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = ItemGameListBinding.inflate(LayoutInflater.from(parent.context))
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
