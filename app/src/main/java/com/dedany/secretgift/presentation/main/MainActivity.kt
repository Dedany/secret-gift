package com.dedany.secretgift.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dedany.secretgift.databinding.ActivityMainBinding
import com.dedany.secretgift.presentation.game.createGame.CreateGameActivity
import com.dedany.secretgift.presentation.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var binding : ActivityMainBinding? = null
    private var viewModel : MainActivityViewModel? = null
    private var gamesAdapter : GamesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        setContentView(binding?.root)

        setUpAdapters()
        setUpObservers()
        setUpListeners()
        viewModel?.loadGames()
    }

    private fun setUpAdapters(){
        gamesAdapter = GamesAdapter()
        binding?.recyclerViewMain?.adapter = gamesAdapter
        binding?.recyclerViewMain?.layoutManager = LinearLayoutManager(this)

    }

    private fun setUpObservers(){
        viewModel?.games?.observe(this) { games ->
            gamesAdapter?.submitList(games)
        }
    }

    private fun setUpListeners(){
        binding?.buttonCreateEvent?.setOnClickListener {
            val intent = Intent(this, CreateGameActivity::class.java)
            startActivity(intent)
        }

        binding?.btnLogoutMain?.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
