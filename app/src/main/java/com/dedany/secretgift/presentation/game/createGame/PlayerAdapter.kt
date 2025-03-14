package com.dedany.secretgift.presentation.game.createGame
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dedany.secretgift.databinding.ItemElementListBinding

import com.dedany.secretgift.domain.entities.Player

class PlayerAdapter(
    private val onDeleteClick: (Player) -> Unit,
    private val onEditClick: (Player) -> Unit
) : ListAdapter<Player, PlayerAdapter.PlayerViewHolder>(PlayerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = ItemElementListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = getItem(position)
        holder.bind(player)
    }

    inner class PlayerViewHolder(private val binding: ItemElementListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(player: Player) {

            val formattedName = FormatPlayerName(player.name)
            Log.d("PlayerAdapter", "Formatted Name: $formattedName")
            binding.tvName.text = formattedName
            binding.ibDelete.setOnClickListener { onDeleteClick(player) }
            binding.ibEdit.setOnClickListener { onEditClick(player) }
        }
    }
}

class PlayerDiffCallback : DiffUtil.ItemCallback<Player>() {
    override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
        return oldItem.email == newItem.email
    }

    override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
        return oldItem == newItem
    }
}


    fun FormatPlayerName(playerName: String): String {
        return if (playerName.contains(" ")) {
            val words =playerName.split(" ")
            "${words[0]}\n${words[1]}"
        }else{
            playerName
        }
    }


