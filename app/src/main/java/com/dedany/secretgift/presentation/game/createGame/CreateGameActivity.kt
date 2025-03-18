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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityCreateGameBinding
import com.dedany.secretgift.databinding.RegisterGamePlayerBinding
import com.dedany.secretgift.domain.entities.Rule
import com.dedany.secretgift.presentation.helpers.Constants
import com.dedany.secretgift.presentation.main.MainActivity
import com.google.android.gms.ads.MobileAds
import com.google.android.material.textfield.TextInputEditText
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
                val rules: List<Rule> = result.data?.getSerializableExtra("RULES") as? List<Rule> ?: emptyList()

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
        enableEdgeToEdge()
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
                //   val newName =getNewNameForPlayer()
                //   viewModel?.editPlayer(player ,newName)
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

            val intent = Intent(this, GameSettingsActivity::class.java).apply {
                putExtra(Constants.KEY_GAME_ID, gameId)
                putExtra("EVENT_DATE", eventDate)
                putExtra("MAX_PRICE", maxPrice)
                putExtra("MIN_PRICE", minPrice)
            }
            settingsActivityResultLauncher.launch(intent)
        }
        binding?.btnAdd?.setOnClickListener { showAddPlayerDialog() }}



    private fun showAddPlayerDialog() {
        val dialog = Dialog(this).apply {
            bindingDialog = RegisterGamePlayerBinding.inflate(layoutInflater)
            setContentView(bindingDialog?.root)
            show()
        }

        bindingDialog?.btnConfirm?.setOnClickListener {
            val name = bindingDialog?.nameEditText?.text.toString().trim()
            val email = bindingDialog?.emailEditText?.text.toString().trim().lowercase(Locale.ROOT)

            if (name.isEmpty() || email.isEmpty()) {
                showToast("Por favor, completa todos los campos")
                return@setOnClickListener
            }

            if (viewModel?.validateEmail(email) == true) {
                handlePlayerValidation(name, email)
                dialog.dismiss()
            } else {
                showToast(viewModel?.emailDataMessage?.value ?: "Email inválido")
            }
        }

        bindingDialog?.btnCancel?.setOnClickListener { dialog.dismiss() }
    }

    private fun handlePlayerValidation(name: String, email: String) {
        val existingPlayers = viewModel?.players?.value ?: emptyList()
        when {
            existingPlayers.any { it.name.equals(name, ignoreCase = true) } -> showToast("El nombre ya está registrado")
            existingPlayers.any { it.email == email } -> showToast("El email ya está registrado")
            else -> viewModel?.addPlayer(name, email)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
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


/*binding?.btnAdd?.setOnClickListener {
      val dialog = Dialog(this)
      dialog.setContentView(R.layout.register_game_player)
      dialog.show()

      val btnConfirm = dialog.findViewById<Button>(R.id.btn_confirm)
      val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)
      val nameEditText = dialog.findViewById<TextInputEditText>(R.id.name_edit_text)
      val emailEditText = dialog.findViewById<TextInputEditText>(R.id.email_edit_text)

      btnConfirm.setOnClickListener {
          val name = nameEditText.text.toString().trim()
          val email = emailEditText.text.toString().trim().lowercase(Locale.ROOT)

          if (name.isEmpty() || email.isEmpty()) {
              Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                  .show()
              return@setOnClickListener
          }

          if (viewModel?.validateEmail(email) == true) {
              val existingPlayers = viewModel?.players?.value ?: emptyList()
              val nameExists = existingPlayers.any { it.name.equals(name, ignoreCase = true) }
              val emailExists = existingPlayers.any { it.email == email }

              when {
                  nameExists -> {
                      Toast.makeText(this, "El nombre ya está registrado", Toast.LENGTH_SHORT)
                          .show()
                  }

                  emailExists -> {
                      Toast.makeText(this, "El email ya está registrado", Toast.LENGTH_SHORT)
                          .show()
                  }

                  else -> {
                      viewModel?.addPlayer(name, email)
                      dialog.dismiss()
                  }
              }
          } else {
              Toast.makeText(
                  this,
                  viewModel?.emailDataMessage?.value ?: "Email inválido",
                  Toast.LENGTH_SHORT
              ).show()
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
}*/
