package com.dedany.secretgift.presentation.game.createGame

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityCreateGameBinding
import com.dedany.secretgift.domain.entities.Player
import com.dedany.secretgift.presentation.game.viewGame.PlayersAdapter
import com.dedany.secretgift.presentation.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateGameActivity : AppCompatActivity() {

    private var binding: ActivityCreateGameBinding? = null
    private var viewModel: CreateGameViewModel? = null
    private var gameSettingsViewModel: GameSettingsViewModel? = null
    private var playerAdapter: PlayerAdapter? = null

    private val settingsActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                observeGameSettings()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateGameBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[CreateGameViewModel::class.java]
        setContentView(binding?.root)


        setAdapters()
        initObservers()
        initListeners()

    }

    private fun setAdapters(){
        playerAdapter = PlayerAdapter(
            onDeleteClick = {player ->
                viewModel?.deletePlayer(player)
            },
            onEditClick = { player ->
             //   val newName =getNewNameForPlayer()
             //   viewModel?.editPlayer(player ,newName)
            }
        )
        binding?.recyclerView?.adapter = playerAdapter
    }


    private fun initObservers() {
        viewModel?.isGameNameValid?.observe(this) { isSuccess ->

            if (isSuccess) {
                Toast.makeText(this, "Nombre del grupo creado correctamente", Toast.LENGTH_SHORT)
                    .show()

            }
        }

        viewModel?.isGameSavedSuccess?.observe(this) { isSuccess ->
            if (isSuccess) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, "Error al guardar el juego", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel?.insufficientDataMessage?.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }

        }
        viewModel?.showConfirmationDialog?.observe(this) { showDialog ->
            if (showDialog) {
                showConfirmationDialog()
            }
        }

        viewModel?.players?.observe(this) { players ->
            playerAdapter?.submitList(players)
        }
    }

    private fun observeGameSettings() {

        val eventDate = gameSettingsViewModel?.eventDate?.value ?: ""
        val numPlayers = gameSettingsViewModel?.numPlayers?.value ?: ""
        val maxPrice = gameSettingsViewModel?.maxPrice?.value ?: ""
        val incompatibilities = gameSettingsViewModel?.incompatibilities?.value ?: emptyList()
        viewModel?.setGameSettings(eventDate, numPlayers, maxPrice, incompatibilities)
    }

    private fun initListeners() {
       // binding?.edNameRoom?.doOnTextChanged { text, start, before, count ->
          //  viewModel?.setName(text.toString())
        binding?.edNameRoom?.doOnTextChanged { text, _, _, _ ->
            val isNotEmpty = !text.isNullOrEmpty()
            binding?.tvAddPeople?.visibility = if(isNotEmpty)View.VISIBLE else View.GONE
            binding?.btnAdd?.visibility = if(isNotEmpty)View.VISIBLE else View.GONE
        }
        binding?.edNameRoom?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel?.checkName()
            }
        }

        binding?.btnCreateGame?.setOnClickListener {
            viewModel?.createOrUpdateGame()
        }
        binding?.btnSaveGame?.setOnClickListener {
            viewModel?.onSaveGameClicked()
        }
        binding?.btnGameSettings?.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            settingsActivityResultLauncher.launch(intent)
        }
        binding?.btnAdd?.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.register_game_player)
            dialog.show()

            val btnConfirm = dialog.findViewById<Button>(R.id.btn_confirm)
            val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)
            val nameEditText = dialog.findViewById<TextInputEditText>(R.id.name_edit_text)
            val emailEditText = dialog.findViewById<TextInputEditText>(R.id.email_edit_text)


            btnConfirm.setOnClickListener {
                val name = nameEditText.text.toString()
                val email = emailEditText.text.toString()

                if (name.isNotEmpty() && email.isNotEmpty()) {
                    viewModel?.addPlayer(name, email)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            nameEditText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    nameEditText.hint = ""
                } else {
                    nameEditText.hint =
                        getString(R.string.name)
                }
            }
        }
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Guardar Juego")
            .setMessage("Una vez guardado el juego, no podrás modificarlo. ¿Estás seguro de que quieres guardar?")
            .setPositiveButton("Guardar") { _, _ ->
                viewModel?.saveGame()
            }
            .setNegativeButton("Cancelar") { _, _ ->
                viewModel?.onDialogDismissed()
            }
            .show()
    }

    }

