package com.dedany.secretgift.presentation.game.createGame

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityCreateGameBinding
import com.dedany.secretgift.presentation.main.MainActivity
import com.google.android.gms.ads.MobileAds
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateGameActivity : AppCompatActivity() {

    private var binding: ActivityCreateGameBinding? = null
    private var viewModel: CreateGameViewModel? = null
    private var playerAdapter: PlayerAdapter? = null

    private val settingsActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val eventDate = result.data?.getStringExtra("EVENT_DATE")
                val maxPrice = result.data?.getStringExtra("MAX_PRICE")
                val minPrice = result.data?.getStringExtra("MIN_PRICE")
                val incompatibilities: List<Pair<String, String>>? =
                    result.data?.getSerializableExtra("INCOMPATIBILITIES") as? List<Pair<String, String>>

                // Pasar los valores al ViewModel
                viewModel?.setGameSettings(
                    eventDate ?: "",
                    maxPrice ?: "",
                    minPrice ?: "",
                    incompatibilities ?: emptyList()
                )
                viewModel?.createOrUpdateGame()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateGameBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[CreateGameViewModel::class.java]
        setContentView(binding?.root)
        viewModel?.addCreatingUserToPlayers()


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
                //   val newName =getNewNameForPlayer()
                //   viewModel?.editPlayer(player ,newName)
            }
        )
        binding?.recyclerView?.adapter = playerAdapter
    }


    private fun initObservers() {

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
            val intent = Intent(this, GameSettingsActivity::class.java)
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

            btnCancel.setOnClickListener {
                dialog.dismiss()
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

