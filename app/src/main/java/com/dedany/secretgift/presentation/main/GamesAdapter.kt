package com.dedany.secretgift.presentation.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dedany.secretgift.databinding.ItemApiGameListBinding
import com.dedany.secretgift.databinding.ItemGameListBinding
import com.dedany.secretgift.domain.entities.Game

class GamesAdapter(
    private val onGameClick: (Game, Int) -> Unit,
    private val onGameDelete: (Game, Int) -> Unit
) : ListAdapter<Game, GamesAdapter.GameViewHolder>(ListAdapterCallback()) {

    inner class GameViewHolder(private val binding: ItemApiGameListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(game: Game, position: Int) {
            binding.tvApiGameName.text = game.name

            binding.ibApiMainDelete.setOnClickListener {
                onGameDelete(game, position)
            }

            binding.root.setOnClickListener {
                onGameClick(game, position)
            }
        }
    }

    class ListAdapterCallback : DiffUtil.ItemCallback<Game>() {
        override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
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
