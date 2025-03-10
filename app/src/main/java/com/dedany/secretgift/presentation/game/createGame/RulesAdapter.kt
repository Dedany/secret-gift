package com.dedany.secretgift.presentation.game.createGame

import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dedany.secretgift.databinding.ItemRuleBinding
import com.dedany.secretgift.domain.entities.Rule

class RulesAdapter(
    private val players: List<String>,
    private val onRemoveClick: (Int) -> Unit
) : ListAdapter<Rule, RulesAdapter.RuleViewHolder>(DiffCallback()) {

    inner class RuleViewHolder(private val binding: ItemRuleBinding) :
        RecyclerView.ViewHolder(binding.root) {

            private val spinnerPlayerOneAdapter = ArrayAdapter(itemView.context, R.layout.simple_spinner_item, players)
            private val spinnerPlayerTwoAdapter = ArrayAdapter(itemView.context, R.layout.simple_spinner_item, players)

        fun bind(rule: Rule) {
            binding.spinnerPlayer1.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    rule.playerOne = players[p2]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
            binding.spinnerPlayer2.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    rule.playerTwo = players[p2]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
            binding.spinnerPlayer1.adapter = spinnerPlayerOneAdapter
            binding.spinnerPlayer2.adapter = spinnerPlayerTwoAdapter

            if (rule.playerOne != null) {
                binding.spinnerPlayer1.setSelection(players.indexOf(rule.playerOne))
            }
            if (rule.playerTwo != null) {
                binding.spinnerPlayer2.setSelection(players.indexOf(rule.playerTwo))
            }

            binding.btnRemove.setOnClickListener {
                onRemoveClick(adapterPosition)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Rule>() {
        override fun areItemsTheSame(oldItem: Rule, newItem: Rule): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Rule, newItem: Rule): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RuleViewHolder {
        val binding = ItemRuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RuleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RuleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}
