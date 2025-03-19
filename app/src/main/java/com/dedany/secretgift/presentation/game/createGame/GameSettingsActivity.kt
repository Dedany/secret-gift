package com.dedany.secretgift.presentation.game.createGame

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityGameOptionsBinding
import com.dedany.secretgift.domain.entities.Player
import com.dedany.secretgift.domain.entities.Rule
import com.dedany.secretgift.presentation.helpers.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class GameSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameOptionsBinding
    private val gameSettingsViewModel: GameSettingsViewModel by viewModels()
    private lateinit var rulesAdapter: RulesAdapter
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                resources.getColor(R.color.transparent, null),
            )
        )
        binding = ActivityGameOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el adaptador antes de usarlo en los observadores
        initAdapters()

        val gameId = intent.getIntExtra(Constants.KEY_GAME_ID, -1)
        if (gameId != -1) {
            gameSettingsViewModel.getLocalGameById(gameId)
        }

        initListeners()
        initObservers() // Ahora podemos observar sin problemas
    }

    private fun initAdapters() {
        // Filtrar jugadores disponibles para incompatibilidades
        val playersList = intent.getSerializableExtra("PLAYERS_LIST") as? List<Player> ?: emptyList()

        val auxPlayersList = playersList.subList(1,playersList.size)

        rulesAdapter = RulesAdapter(playersList.map { it.name }, auxPlayersList.map{ it.name }) {
            gameSettingsViewModel.removeRuleAt(it)
        }

        binding.recyclerViewRules.adapter = rulesAdapter
    }

    private fun initObservers() {
        // Ahora que el adaptador está inicializado, podemos observar las reglas sin problemas
        gameSettingsViewModel.rules.observe(this) { rules ->
            rulesAdapter.submitList(rules) // Actualizar el adaptador con las nuevas reglas
        }

        gameSettingsViewModel.eventDate.observe(this) { eventDate ->
            binding.editTextEventDate.setText(eventDate)
        }



        gameSettingsViewModel.maxPrice.observe(this) { maxPrice ->
            binding.editTextMaxPriceOptions.setText(maxPrice)
        }

        gameSettingsViewModel.minPrice.observe(this) { minPrice ->
            binding.editTextMinPriceOptions.setText(minPrice)
        }
    }

    private fun initListeners() {
        // DataPicker
        binding.editTextEventDate.setOnClickListener {
            // Si ya hay una fecha seleccionada, la borramos
            if (gameSettingsViewModel.eventDate.value != null) {
                binding.editTextEventDate.text.clear()
                gameSettingsViewModel.setEventDate(null)
            }
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val selectedDate = dateFormat.format(calendar.time)
                binding.editTextEventDate.setText(selectedDate) // Directamente actualiza el EditText
                gameSettingsViewModel.setEventDate(selectedDate)
            }, year, month, day)

            datePickerDialog.show()
        }


        // Guardar cambios
        binding.btnSaveChanges.setOnClickListener {
            if (gameSettingsViewModel.isDateSelected.value == false) {
                Toast.makeText(this, "Por favor, selecciona una fecha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val eventDate = gameSettingsViewModel.eventDate.value!!
            val maxPrice = binding.editTextMaxPriceOptions.text.toString()
            val minPrice = binding.editTextMinPriceOptions.text.toString()
            val incompatibilities = getIncompatibilities()

            val rules = incompatibilities.map { Rule(it.first, it.second) }
            gameSettingsViewModel.setRules(rules)

            val intent = Intent(this, CreateGameActivity::class.java).apply {
                putExtra("EVENT_DATE", eventDate)
                putExtra("MAX_PRICE", maxPrice)
                putExtra("MIN_PRICE", minPrice)
                putExtra("RULES", ArrayList(rules))
            }

            setResult(RESULT_OK, intent)
            finish()
        }

        // Regresar
        binding.iconBack.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()  // Cierra la actividad
        }

        // Añadir incompatibilidad
        binding.btnAddIncompatibilities.setOnClickListener {
            gameSettingsViewModel.addNewRule()
        }
    }

    private fun getIncompatibilities(): List<Pair<String, String>> {
        val rules = rulesAdapter.currentList
        return if (rules.isNotEmpty()) {
            rules.mapNotNull { rule ->
                if (rule.playerOne != null && rule.playerTwo != null) {
                    Pair(rule.playerOne!!, rule.playerTwo!!)
                } else {
                    null
                }
            }
        } else {
            emptyList()
        }
    }
}