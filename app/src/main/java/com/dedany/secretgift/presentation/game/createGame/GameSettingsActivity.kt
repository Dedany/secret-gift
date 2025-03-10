package com.dedany.secretgift.presentation.game.createGame

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dedany.secretgift.databinding.ActivityGameOptionsBinding
import com.dedany.secretgift.domain.entities.Rule

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameOptionsBinding
    private val gameSettingsViewModel: GameSettingsViewModel by viewModels()
    private lateinit var rulesAdapter: RulesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapters()
        initObservers()
        initListeners()
    }

    private fun initObservers() {
        gameSettingsViewModel.eventDate.observe(this) { date ->
            binding.editTextEventDate.setText(date)
        }
        gameSettingsViewModel.numPlayers.observe(this) { players ->
            binding.editTextPlayersOptions.setText(players)
        }
        gameSettingsViewModel.maxPrice.observe(this) { price ->
            binding.editTextMaxPriceOptions.setText(price)
        }
        gameSettingsViewModel.incompatibilities.observe(this) { incompatibilities ->
            if (incompatibilities.isEmpty()) {

            } else {

            }
        }

        gameSettingsViewModel.rules.observe(this) { rules ->
            rulesAdapter.submitList(rules)
        }
    }

    private fun initAdapters(){
        rulesAdapter = RulesAdapter(listOf("Player 1", "Player 2", "Player 3", "Player 4", "Player 5")) {
            gameSettingsViewModel.removeRuleAt(it)
        }
        binding.recyclerViewRules.adapter = rulesAdapter
        rulesAdapter.submitList(listOf(Rule("Player 1", "Player 2"), Rule("Player 3", "Player 4")))
    }

    private fun initListeners() {
        binding.btnSaveChanges.setOnClickListener {

            val eventDate = binding.editTextEventDate.text.toString()
            val numPlayers = binding.editTextPlayersOptions.text.toString()
            val maxPrice = binding.editTextMaxPriceOptions.text.toString()
            val incompatibilities = getIncompatibilities()

            gameSettingsViewModel.setEventDate(eventDate)
            gameSettingsViewModel.setNumPlayers(numPlayers)
            gameSettingsViewModel.setMaxPrice(maxPrice)
            gameSettingsViewModel.setIncompatibilities(incompatibilities)

            // Volver a la actividad anterior
            setResult(RESULT_OK)
            finish()
        }

        binding.iconBack.setOnClickListener {
            // Volver sin guardar cambios
            setResult(RESULT_CANCELED)
            finish()
        }

        binding.btnAddIncompatibilities.setOnClickListener{
            gameSettingsViewModel.addNewRule()
        }
    }

    private fun getIncompatibilities(): List<Pair<String, String>> {

        val adapter = binding.recyclerViewRules.adapter as RulesAdapter
        val rules = adapter.currentList

        return if (rules.isNotEmpty()) {
            rules.mapNotNull { rule ->
                if (rule.playerOne != null && rule.playerTwo != null) {
                    Pair(rule.playerOne!!, rule.playerTwo!!)
                } else {
                    null
                }
            }
        } else {
            emptyList()
        }
    }


}
