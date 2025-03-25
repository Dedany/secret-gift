package com.dedany.secretgift.presentation.game.createGame

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityCreateGameBinding
import com.dedany.secretgift.databinding.RegisterGamePlayerBinding
import com.dedany.secretgift.domain.entities.Rule
import com.dedany.secretgift.presentation.helpers.Constants
import com.dedany.secretgift.presentation.main.MainActivity
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class CreateGameActivity : AppCompatActivity() {

    private var binding: ActivityCreateGameBinding? = null
    private var bindingDialog: RegisterGamePlayerBinding? = null
    private var viewModel: CreateGameViewModel? = null
    private var playerAdapter: PlayerAdapter? = null
    private var addPlayerDialog: Dialog? = null

    private val settingsActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val eventDate = result.data?.getStringExtra("EVENT_DATE")
                val maxPrice = result.data?.getStringExtra("MAX_PRICE")
                val minPrice = result.data?.getStringExtra("MIN_PRICE")
                val rules: List<Rule> =
                    result.data?.getSerializableExtra("RULES") as? List<Rule> ?: emptyList()

                viewModel?.setGameSettings(
                    eventDate ?: "",
                    maxPrice ?: "",
                    minPrice ?: "",
                    rules
                )
                viewModel?.createOrUpdateGame()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                resources.getColor(R.color.transparent, null),
            )
        )
        binding = ActivityCreateGameBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[CreateGameViewModel::class.java]
        setContentView(binding?.root)

        setupViewModel()
        setAdapters()
        initObservers()
        initListeners()
        initAd()
    }

    private fun setupViewModel() {
        viewModel?.fetchOwnerEmail()

        val gameId = intent.getIntExtra(Constants.KEY_GAME_ID, -1)



        if (gameId != -1) {
            viewModel?.setGameId(gameId)
            viewModel?.loadLocalGameById(gameId)
            viewModel?.updateGame()
        } else {
            viewModel?.addCreatingUserToPlayers()
            viewModel?.createOrUpdateGame()
        }
    }

    private fun setAdapters() {
        playerAdapter = PlayerAdapter(
            onDeleteClick = { player ->
                viewModel?.deletePlayer(player)
            },
            onEditClick = { player ->
                val dialogBinding = RegisterGamePlayerBinding.inflate(layoutInflater)
                val dialog = Dialog(this)
                dialog.setContentView(dialogBinding.root)



                val width = ViewGroup.LayoutParams.WRAP_CONTENT
                val height = resources.getDimensionPixelSize(R.dimen.dialog_height)
                dialog.window?.setLayout(width, height)
                dialog.window?.setGravity(Gravity.CENTER)

                dialogBinding.nameEditText.setText(player.name)
                dialogBinding.emailEditText.setText(player.email)

                dialogBinding.btnConfirm.setOnClickListener {
                    val newName = dialogBinding.nameEditText.text.toString().trim()
                    val newEmail = dialogBinding.emailEditText.text.toString().trim()

                    if (newName.isNotEmpty() && newEmail.isNotEmpty()) {
                        viewModel?.editPlayer(player, newName, newEmail)
                        dialog.dismiss()
                    }
                }
                dialogBinding.btnCancel.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()

            }
        )
        binding?.recyclerView?.adapter = playerAdapter
    }

    private fun initObservers() {

        viewModel?.isSaving?.observe(this) { isSaving ->
            if (isSaving) {
                binding?.loader?.visibility = View.VISIBLE
                binding?.constraintLayout2?.visibility = View.GONE
            } else {
                binding?.loader?.visibility = View.GONE
            }
        }
        viewModel?.validationError?.observe(this) { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            } else {
                showConfirmationDialog()
            }
        }
        viewModel?.localGame?.observe(this) { game ->
            game?.let {
                binding?.edNameRoom?.setText(it.name)
                playerAdapter?.submitList(it.players)
                updateUIBasedOnName(it.name)
            }
        }
        viewModel?.ownerId?.observe(this) { ownerEmail ->
            playerAdapter?.setOwnerEmail(ownerEmail)
        }

        viewModel?.isGameSavedSuccess?.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Sala guardada exitosamente", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, "No se puede guardar la sala", Toast.LENGTH_SHORT).show()

                finish()
                startActivity(Intent(this, CreateGameActivity::class.java))
            }
        }
        viewModel?.nameErrorMessage?.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
        viewModel?.playersErrorMessage?.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
        viewModel?.dateErrorMessage?.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
        viewModel?.showConfirmationDialog?.observe(this) { showDialog ->
            if (showDialog) {
                showConfirmationDialog()
            }
        }

        viewModel?.emailDataMessage?.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }

        }

        viewModel?.players?.observe(this) { players ->
            playerAdapter?.submitList(players)
        }
        viewModel?.addPlayerResult?.observe(this) { result ->
            when (result) {
                is CreateGameViewModel.AddPlayerResult.Success -> {
                    addPlayerDialog?.dismiss()
                }
                is CreateGameViewModel.AddPlayerResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun initListeners() {
        binding?.edNameRoom?.doOnTextChanged { text, _, _, _ ->
            updateUIBasedOnName(text.toString())
        }
        binding?.edNameRoom?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel?.checkName()
            }
        }
        binding?.iconBack?.setOnClickListener {
            finish()
        }

        binding?.btnSaveGame?.setOnClickListener {
            viewModel?.checkGame()
        }

        binding?.btnGameSettings?.setOnClickListener {
            val playersList = viewModel?.getPlayersList() ?: emptyList()

            if (playersList.size < 3) {
                Toast.makeText(this, "Necesitas 3 jugadores mínimo", Toast.LENGTH_SHORT).show()
            } else {
                val gameId = viewModel?.getGameId() ?: -1
                val intent = Intent(this, GameSettingsActivity::class.java).apply {
                    putExtra(Constants.KEY_GAME_ID, gameId)
                    putExtra("PLAYERS_LIST", ArrayList(playersList))
                }
                settingsActivityResultLauncher.launch(intent)
            }
        }

        binding?.btnAdd?.setOnClickListener {
            showAddplayerDialog()
        }

    }


    private fun showAddplayerDialog() {
        val dialogBinding = RegisterGamePlayerBinding.inflate(layoutInflater)
        addPlayerDialog = Dialog(this)
        addPlayerDialog?.setContentView(dialogBinding.root)
        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val height = resources.getDimensionPixelSize(R.dimen.dialog_height)

        addPlayerDialog?.window?.setLayout(width, height)
        addPlayerDialog?.window?.setGravity(Gravity.CENTER)

        addPlayerDialog?.show()
        dialogBinding.btnConfirm.setOnClickListener {
            val name = dialogBinding.nameEditText.text.toString().trim()
            val email = dialogBinding.emailEditText.text.toString().trim().lowercase(Locale.ROOT)
            viewModel?.addPlayer(name, email)
        }

        dialogBinding.btnCancel.setOnClickListener {
            addPlayerDialog?.dismiss()
        }
        dialogBinding.nameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                dialogBinding.nameEditText.hint = ""
            } else {
                dialogBinding.nameEditText.hint =
                    getString(R.string.name)
            }
        }
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("¿Estás segur@?")
            .setMessage("Una vez enviada la sala, no se podrá modificar. ¿Estás seguro de que quieres enviar?")
            .setPositiveButton("Enviar") { _, _ ->
                viewModel?.saveGame()
            }
            .setNegativeButton("Cancelar") { _, _ ->
                viewModel?.onDialogDismissed()
            }
            .show()
    }
    private fun updateUIBasedOnName(name: String) {

        val isNotEmpty = name.isNotEmpty()
        binding?.tvAddPeople?.visibility = if (isNotEmpty) View.VISIBLE else View.GONE
        binding?.btnAdd?.visibility = if (isNotEmpty) View.VISIBLE else View.GONE
        viewModel?.setName(name)

        val isValidName = name.length >= 3
        binding?.btnGameSettings?.isEnabled = isValidName
    }

    fun initAd() {
        //iniciar adMob
        MobileAds.initialize(this) { initialAd ->
            val statusMap = initialAd.adapterStatusMap
            for ((adapter, satatus) in statusMap) {
                Log.d("AdMob", "Adapter: $adapter Status: ${satatus.description}")
            }
        }

    }
}