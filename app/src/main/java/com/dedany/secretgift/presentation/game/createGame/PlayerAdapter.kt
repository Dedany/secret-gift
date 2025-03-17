package com.dedany.secretgift.presentation.game.createGame
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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

    private var ownerEmail: String? = null


    fun setOwnerEmail(ownerEmail: String) {
        this.ownerEmail = ownerEmail
        notifyDataSetChanged()
    }

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

            binding.tvName.text = player.name
            binding.ibDelete.visibility = if (player.email == ownerEmail) View.GONE else View.VISIBLE
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





