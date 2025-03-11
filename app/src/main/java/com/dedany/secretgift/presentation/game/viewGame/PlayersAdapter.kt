package com.dedany.secretgift.presentation.game.viewGame

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.dedany.secretgift.databinding.ItemPlayersViewBinding
import com.dedany.secretgift.domain.entities.Player

class PlayersAdapter : ListAdapter<Player, PlayersAdapter.PlayerViewHolder>(PlayersDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = ItemPlayersViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = getItem(position)
        holder.bind(player)
    }

     class PlayerViewHolder(private val binding: ItemPlayersViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player) {
            binding.tvPlayerName.text = player.name
            // You can add more data to bind here if needed
        }
    }
}

class PlayersDiffCallback : DiffUtil.ItemCallback<Player>() {
    override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
        return oldItem.email == newItem.email
    }

    override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
        return oldItem == newItem
    }
}