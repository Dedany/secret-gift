package com.dedany.secretgift.presentation.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.FragmentMainBinding
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.entities.GameSummary
import com.dedany.secretgift.domain.entities.LocalGame
import com.dedany.secretgift.domain.entities.User
import com.dedany.secretgift.presentation.game.createGame.CreateGameActivity
import com.dedany.secretgift.presentation.game.viewGame.ViewGameActivity
import com.dedany.secretgift.presentation.helpers.Constants
import com.dedany.secretgift.presentation.helpers.getCustomSerializable
import com.dedany.secretgift.presentation.login.LoginActivity
import com.dedany.secretgift.presentation.main.GamesAdapter
import com.dedany.secretgift.presentation.main.LocalGamesAdapter
import com.dedany.secretgift.presentation.main.MainActivityViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

    private var binding: FragmentMainBinding? = null
    private var viewModel: MainActivityViewModel? = null
    private var gamesAdapter: GamesAdapter? = null
    private var localGamesAdapter: LocalGamesAdapter? = null
    private lateinit var user: User // Variable para almacenar al usuario

    private var resultLauncherGame =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    val gameId = result.data?.getIntExtra(Constants.KEY_GAME_ID, -1)
                    if (gameId != null && gameId != -1) {
                        viewModel?.loadLocalGames()
                    }
                }
            }
        }

    private var resultLauncherAccess =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    val accessCode = result.data?.getStringExtra(Constants.KEY_ACCESS_CODE)
                    if (!accessCode.isNullOrEmpty()) {
                        // Recargar la lista de juegos de la API
                        viewModel?.loadGames()
                    }
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]


        user = arguments?.getSerializable("user") as? User
            ?: throw IllegalArgumentException("User not found in arguments!")

        setUpAdapters()
        setUpObservers()
        setUpListeners()

        viewModel?.loadLocalGames()
        viewModel?.loadGames()
    }

    override fun onResume() {
        super.onResume()
        viewModel?.loadLocalGames()
        viewModel?.loadGames()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding?.root
    }

    private fun setUpAdapters() {
        localGamesAdapter = LocalGamesAdapter(
            onGameClick = { game, _ ->
                openLocalGameDetails(game.id)
            },
            onGameDelete = { game,position ->
                showDeleteConfirmationDialog(game, null)
            }
        )

        gamesAdapter = GamesAdapter(
            onGameClick = { accessCode ->
                openApiGameDetails(accessCode)
            },
            onGameDelete = { game, position ->
                showDeleteConfirmationDialog(null, game)
            }
        )


        binding?.recyclerViewMain?.adapter = localGamesAdapter
        binding?.recyclerViewMain?.layoutManager = LinearLayoutManager(requireContext())
        binding?.recyclerViewMainApiGames?.adapter = gamesAdapter
        binding?.recyclerViewMainApiGames?.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setUpObservers() {

        viewModel?.games?.observe(viewLifecycleOwner) { games ->
            gamesAdapter?.submitList(games)
        }

        viewModel?.localGames?.observe(viewLifecycleOwner) { localGames ->
            localGamesAdapter?.submitList(localGames)
        }

        viewModel?.deletedGameMessage?.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpListeners() {
        binding?.buttonCreateEvent?.setOnClickListener {
            val intent = Intent(requireContext(), CreateGameActivity::class.java)
            startActivity(intent)
        }

        binding?.btnLogoutMain?.setOnClickListener {

            val builder = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Confirmar cierre de sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión? Esto borrará todos los borradores de juegos.")
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Cerrar sesión") { dialog, _ ->
                    viewModel?.deleteAllGames()
                    viewModel?.logout()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)

                    dialog.dismiss()
                }
            builder.show()
        }

    }


    private fun openLocalGameDetails(gameId: Int) {
        val intent = Intent(requireContext(), CreateGameActivity::class.java).apply {
            putExtra(Constants.KEY_GAME_ID, gameId)
        }
        resultLauncherGame.launch(intent)
    }

    private fun openApiGameDetails(accessCode: String) {
        val intent = Intent(requireContext(), ViewGameActivity::class.java).apply {
            putExtra(Constants.KEY_ACCESS_CODE, accessCode)
        }
        resultLauncherAccess.launch(intent)
    }
    private fun showDeleteConfirmationDialog(localGame: LocalGame?, game: GameSummary?) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val gameName = localGame?.name ?: game?.name
        builder.setTitle("Confirmar borrado")
            .setMessage("¿Estás seguro de que quieres borrar el juego $gameName?")
            .setPositiveButton("Borrar") { _, _ ->
                if (localGame != null) {
                    viewModel?.deleteLocalGame(localGame.id)
                } else if (game != null) {
                    viewModel?.deleteRemoteGame(game.id)
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
