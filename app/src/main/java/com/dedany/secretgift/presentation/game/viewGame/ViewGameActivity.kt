package com.dedany.secretgift.presentation.game.viewGame

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityViewGameBinding
import com.dedany.secretgift.presentation.helpers.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewGameActivity : AppCompatActivity() {
    private var binding: ActivityViewGameBinding? = null
    private var viewModel: ViewGameViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityViewGameBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ViewGameViewModel::class.java]
        setContentView(binding?.root)

        loadGame()
        setObservers()
    }

    private fun loadGame() {
        val intent = intent
        if (intent.hasExtra(Constants.KEY_GAME)){
            val gameCode = intent.getStringExtra(Constants.KEY_GAME)
            if (gameCode != null) {
                viewModel?.fetchGaMeData(gameCode)
            } else {
                //TODO: handle error
            }
        }


    }

    private fun setObservers() {
        viewModel?.game?.observe(this) { gameData ->
           binding?.tvGameName?.text = gameData.name
            binding?.currentPlayerName?.text = gameData.currentPlayer
            binding?.matchedPlayerName?.text = gameData.matchedPlayer
            binding?.tvPlayersNumber?.text = gameData.players.size.toString()
            binding?.tvMinMoney?.text = gameData.minCost.toString()
            binding?.tvMaxMoney?.text = gameData.maxCost.toString()
            binding?.tvGameDate?.text = gameData.gameDate.toString()

        }


    }
}