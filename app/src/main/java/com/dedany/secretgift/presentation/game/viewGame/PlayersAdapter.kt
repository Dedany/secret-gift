package com.dedany.secretgift.presentation.game.viewGame

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ItemPlayersViewBinding
import com.dedany.secretgift.domain.entities.User
import com.dedany.secretgift.presentation.helpers.setMailStatusIcon

class PlayersAdapter(
    private val onSendEmail: (User, Int) -> Unit
) : ListAdapter<User, PlayersAdapter.PlayerViewHolder>(PlayersDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding =
            ItemPlayersViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = getItem(position)
        holder.bind(player, position)
    }

    inner class PlayerViewHolder(private val binding: ItemPlayersViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(player: User, position: Int) {
            binding.tvPlayerName.text = player.name
            binding.ibMailStatus.setMailStatusIcon(player.mailStatus)
            binding.ibSendEmail.setOnClickListener {
                onSendEmail(player, position)
            }
        }
    }
}

class PlayersDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}