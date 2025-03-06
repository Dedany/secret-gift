package com.dedany.secretgift.presentation.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

import androidx.recyclerview.widget.LinearLayoutManager
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.FragmentMainBinding
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.entities.User
import com.dedany.secretgift.presentation.details.DetailsMainActivity
import com.dedany.secretgift.presentation.game.createGame.CreateGameActivity
import com.dedany.secretgift.presentation.helpers.Constants
import com.dedany.secretgift.presentation.helpers.getCustomSerializable
import com.dedany.secretgift.presentation.login.LoginActivity
import com.dedany.secretgift.presentation.main.GamesAdapter
import com.dedany.secretgift.presentation.main.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

    private var binding: FragmentMainBinding? = null
    private var viewModel: MainActivityViewModel? = null
    private var gamesAdapter: GamesAdapter? = null
    private lateinit var user: User // Variable para almacenar al usuario

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    if (result.data?.hasExtra(Constants.KEY_GAME) == true) {
                        val game = result.data?.getCustomSerializable<Game>(Constants.KEY_GAME)
                        val position = result.data?.extras?.getInt(Constants.KEY_GAME_POSITION)
                        position?.let {
                            game?.let {
                                //viewModel?.updateGamesList(position, game)
                                gamesAdapter?.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMainBinding.bind(view)
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]


        user = arguments?.getSerializable("user") as? User
            ?: throw IllegalArgumentException("User not found in arguments!")

        setUpAdapters()
        setUpObservers()
        setUpListeners()


        viewModel?.loadGames()
    }

    private fun setUpAdapters() {
        gamesAdapter = GamesAdapter(
            onGameClick = { game, position ->
                openGameDetails(game, position)
            },
            onGameDelete = { game, _ ->
                //viewModel?.deleteLocalGame(game)
            }
        )

        binding?.recyclerViewMain?.adapter = gamesAdapter
        binding?.recyclerViewMain?.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setUpObservers() {
        // Observar los juegos cargados en el ViewModel
        viewModel?.games?.observe(viewLifecycleOwner) { games ->
            gamesAdapter?.submitList(games)
        }
    }

    private fun setUpListeners() {
        binding?.buttonCreateEvent?.setOnClickListener {
            // Navegar a la pantalla de crear juego
            val intent = Intent(requireContext(), CreateGameActivity::class.java)
            startActivity(intent)
        }

        binding?.btnLogoutMain?.setOnClickListener {
            // Cerrar sesión y navegar a la pantalla de inicio de sesión
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openGameDetails(game: Game, position: Int) {
        // Navegar a la pantalla de detalles del juego
        val intent = Intent(requireContext(), DetailsMainActivity::class.java).apply {
            putExtra(Constants.KEY_GAME, game)
            putExtra(Constants.KEY_GAME_POSITION, position)
        }
        resultLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
