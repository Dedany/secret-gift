package com.dedany.secretgift.presentation.game.createGame

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class CreateGameActivity : AppCompatActivity() {

    private var binding: ActivityCreateGameBinding? = null
    private var bindingDialog: RegisterGamePlayerBinding? = null
    private var viewModel: CreateGameViewModel? = null
    private var playerAdapter: PlayerAdapter? = null

    private val settingsActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val eventDate = result.data?.getStringExtra("EVENT_DATE")
                val maxPrice = result.data?.getStringExtra("MAX_PRICE")
                val minPrice = result.data?.getStringExtra("MIN_PRICE")
                val rules: List<Rule> =
                    result.data?.getSerializableExtra("RULES") as? List<Rule> ?: emptyList()

                // Pasar los valores al ViewModel
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


        val gameId = intent.getIntExtra(Constants.KEY_GAME_ID, -1)

        if (gameId != -1) {
            viewModel?.setGameId(gameId)
            viewModel?.loadLocalGameById(gameId)
            viewModel?.updateGame()
        } else {
            viewModel?.addCreatingUserToPlayers()  // Se ejecuta solo si es un nuevo juego
            viewModel?.createOrUpdateGame()
        }

        setAdapters()
        initObservers()
        initListeners()
        initAd()
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

        viewModel?.localGame?.observe(this, Observer { game ->
            game?.let {
                binding?.edNameRoom?.setText(it.name)
                playerAdapter?.submitList(it.players)
            }
        })

        viewModel?.ownerId?.observe(this) { ownerId ->
            playerAdapter?.setOwnerEmail(ownerId)
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

        viewModel?.emailDataMessage?.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }

        }

        viewModel?.players?.observe(this) { players ->
            playerAdapter?.submitList(players)
        }
    }


    private fun initListeners() {
        binding?.edNameRoom?.doOnTextChanged { text, _, _, _ ->
            val isNotEmpty = !text.isNullOrEmpty()
            binding?.tvAddPeople?.visibility = if (isNotEmpty) View.VISIBLE else View.GONE
            binding?.btnAdd?.visibility = if (isNotEmpty) View.VISIBLE else View.GONE
            viewModel?.setName(text.toString())
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
            viewModel?.onSaveGameClicked()
        }

        binding?.btnGameSettings?.setOnClickListener {
            val gameId = viewModel?.getGameId() ?: -1
            val eventDate = viewModel?.eventDate ?: ""
            val maxPrice = viewModel?.maxPrice ?: ""
            val minPrice = viewModel?.minPrice ?: ""


            val playersList = viewModel?.getPlayersList() ?: emptyList()

            val intent = Intent(this, GameSettingsActivity::class.java).apply {
                putExtra(Constants.KEY_GAME_ID, gameId)
                putExtra("EVENT_DATE", eventDate)
                putExtra("MAX_PRICE", maxPrice)
                putExtra("MIN_PRICE", minPrice)
                putExtra("PLAYERS_LIST", ArrayList(playersList))
            }

            settingsActivityResultLauncher.launch(intent)
        }

        binding?.btnAdd?.setOnClickListener {
            showAddplayerDialog()
        }


        binding?.btnAdd?.setOnClickListener {
            showAddplayerDialog()
        }
    }


    private fun showAddplayerDialog() {
        val dialogBinding = RegisterGamePlayerBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding.root)
        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val height = resources.getDimensionPixelSize(R.dimen.dialog_height)

        dialog.window?.setLayout(width, height)
        dialog.window?.setGravity(Gravity.CENTER)

        dialog.show()
        dialogBinding.btnConfirm.setOnClickListener {
            val name = dialogBinding.nameEditText.text.toString().trim()
            val email = dialogBinding.emailEditText.text.toString().trim().lowercase(Locale.ROOT)

            if (name.isNotEmpty() && email.isNotEmpty()) {
                if (viewModel?.validateEmail(email) == true) {
                    val existingPlayers = viewModel?.players?.value ?: emptyList()
                    val emailExists = existingPlayers.any { it.email == email }

                    if (emailExists) {
                        Toast.makeText(this, "El email ya está registrado", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        viewModel?.addPlayer(name, email)
                        dialog.dismiss()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
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
        AlertDialog.Builder(this)
            .setTitle("Guardar Juego")
            .setMessage("Una vez enviado el juego, no podrás modificarlo. ¿Estás seguro de que quieres enviar?")
            .setPositiveButton("Guardar") { _, _ ->
                viewModel?.saveGame()
            }
            .setNegativeButton("Cancelar") { _, _ ->
                viewModel?.onDialogDismissed()
            }
            .show()
    }

    fun initAd() {
        //iniciar adMob
        MobileAds.initialize(this) { initialAd ->
            val statusMap = initialAd.adapterStatusMap
            for ((adapter, satatus) in statusMap) {
                Log.d("AdMob", "Adapter: $adapter Status: ${satatus.description}")
            }
        }

        /*binding?.adView?.apply {
             //asigna tamaño

             // Carga el anuncio
             val adRequest = AdRequest.Builder().setContentUrl("https://www.amazon.es")
             loadAd(adRequest.build())
         }*/
    }
}