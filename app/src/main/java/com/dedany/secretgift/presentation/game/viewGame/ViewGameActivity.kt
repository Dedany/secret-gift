package com.dedany.secretgift.presentation.game.viewGame

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityViewGameBinding
import com.dedany.secretgift.presentation.helpers.Constants
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class ViewGameActivity : AppCompatActivity() {
    private var binding: ActivityViewGameBinding? = null
    private var viewModel: ViewGameViewModel? = null
    private lateinit var playersAdapter: PlayersAdapter

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
        binding?.iconBack?.setOnClickListener {
            finish()
        }


        viewModel = ViewModelProvider(this)[ViewGameViewModel::class.java]
        setContentView(binding?.root)

        setAdapter()
        loadGame()
        setObservers()
        initAd()
    }

    private fun setAdapter() {
        playersAdapter = PlayersAdapter()
        binding?.playersRecyclerView?.apply {
            layoutManager = LinearLayoutManager(this@ViewGameActivity)
            adapter = playersAdapter
        }

    }

    private fun loadGame() {
        val intent = intent
        if (intent.hasExtra(Constants.KEY_ACCESS_CODE)) {
            val gameCode = intent.getStringExtra(Constants.KEY_ACCESS_CODE)
            if (gameCode != null) {
                viewModel?.fetchGaMeData(gameCode)
            } else {
                //TODO: handle error
            }
        }


    }

    private fun setObservers() {

        viewModel?.isLoading?.observe(this) {
            if (it) {
                binding?.loader?.isVisible = true
                binding?.coordinatorLayout?.isVisible = false
                binding?.viewGameSettings?.isVisible = false
            } else {
                binding?.loader?.isVisible = false
                binding?.coordinatorLayout?.isVisible = true
                binding?.viewGameSettings?.isVisible = true
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
            binding?.tvPlayersNumber?.text = (gameData.players.size + 1).toString()
            binding?.tvMinMoney?.text = gameData.minCost.toString()
            binding?.tvMaxMoney?.text = gameData.maxCost.toString()
            playersAdapter?.submitList(gameData.players)

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding?.tvGameDate?.text = dateFormat.format(gameData.gameDate)
        }


    }
    fun initAd() {
        //iniciar adMob
        MobileAds.initialize(this) { initialAd ->
            val statusMap = initialAd.adapterStatusMap
            for ((adapter, satatus) in statusMap) {
                Log.d("AdMob", "Adapter: $adapter Status: ${satatus.description}")
            }
        }
        binding?.adView?.apply {
            //asigna tamaño

            // Carga el anuncio
            val adRequest = AdRequest.Builder().setContentUrl("https://www.amazon.es")
            loadAd(adRequest.build())
        }


    }
}