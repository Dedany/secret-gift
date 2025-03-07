package com.dedany.secretgift.presentation.game.viewGame

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityViewGameBinding
import com.dedany.secretgift.presentation.helpers.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class ViewGameActivity : AppCompatActivity() {
    private var binding: ActivityViewGameBinding? = null
    private var viewModel: ViewGameViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                resources.getColor(R.color.transparent, null),

            )
        )
        binding = ActivityViewGameBinding.inflate(layoutInflater)
        binding?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it.root) { v, insets ->
                val bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            or WindowInsetsCompat.Type.displayCutout()
                )
                v.updatePadding(
                    left = bars.left,
                    right = bars.right,
                    bottom = bars.bottom,
                )
                WindowInsetsCompat.CONSUMED
            }
        }

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

        viewModel?.isLoading?.observe(this){
            if (it){
                binding?.loader?.isVisible=true
                binding?.coordinatorLayout?.isVisible=false
                binding?.viewGameSettings?.isVisible=false
            }else{
                binding?.loader?.isVisible=false
                binding?.coordinatorLayout?.isVisible=true
                binding?.viewGameSettings?.isVisible=true
            }
        }

        viewModel?.gameCodeError?.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            finish()
        }

        viewModel?.game?.observe(this) { gameData ->
           binding?.tvGameName?.text = gameData.name
            binding?.currentPlayerName?.text = gameData.currentPlayer
            binding?.matchedPlayerName?.text = gameData.matchedPlayer
            binding?.tvPlayersNumber?.text = gameData.players.size.toString()
            binding?.tvMinMoney?.text = gameData.minCost.toString()
            binding?.tvMaxMoney?.text = gameData.maxCost.toString()

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding?.tvGameDate?.text = dateFormat.format(gameData.gameDate)
        }


    }
}