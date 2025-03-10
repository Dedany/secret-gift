package com.dedany.secretgift.presentation.game.createGame

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityCreateGameBinding
import com.dedany.secretgift.presentation.login.LoginViewModel
import com.dedany.secretgift.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateGameActivity : AppCompatActivity() {

    private var binding: ActivityCreateGameBinding? = null
    private var viewModel: CreateGameViewModel? = null


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


        initObservers()
        initListeners()


    }


    private fun initObservers() {
        viewModel?.isGameNameValid?.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Nombre del grupo creado correctamente", Toast.LENGTH_SHORT)
                    .show()

            } else {
                Toast.makeText(this, "necesita un mínimo de 4 letras", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel?.isGameSavedSuccess?.observe(this) { isSuccess ->
            if (isSuccess) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, "Error al guardar el juego", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel?.showConfirmationDialog?.observe(this) { showDialog ->
            if (showDialog) {
                showConfirmationDialog()
            }
        }
    }

    private fun observeGameSettings() {
        val gameSettingsViewModel: GameSettingsViewModel by viewModels()

        val eventDate = gameSettingsViewModel.eventDate.value ?: ""
        val numPlayers = gameSettingsViewModel.numPlayers.value ?: ""
        val maxPrice = gameSettingsViewModel.maxPrice.value ?: ""
        val incompatibilities = gameSettingsViewModel.incompatibilities.value ?: emptyList<Pair<String, String>>()
    }

    private fun initListeners() {
        binding?.edNameRoom?.doOnTextChanged { text, start, before, count ->
            viewModel?.setName(text.toString())
        }
        binding?.edNameRoom?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel?.checkName()
            }
        }

        binding?.btnCreateGame?.setOnClickListener {
            viewModel?.createGame()
            }
        binding?.btnSaveGame?.setOnClickListener {
            viewModel?.onSaveGameClicked()
        }
        binding?.btnGameSettings?.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            settingsActivityResultLauncher.launch(intent)
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



