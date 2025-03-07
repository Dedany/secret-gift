package com.dedany.secretgift.presentation.game.createGame

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityGameOptionsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameOptionsBinding
    private val gameSettingsViewModel: GameSettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        }
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
    }

    private fun getIncompatibilities(): String {
        val player1 = binding.spinnerPlayer1.selectedItem.toString()
        val player2 = binding.spinnerPlayer2.selectedItem.toString()
        return "$player1 ↔ $player2"
    }
}
