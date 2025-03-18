package com.dedany.secretgift.presentation.game.createGame

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dedany.secretgift.databinding.ActivityGameOptionsBinding
import com.dedany.secretgift.domain.entities.Rule
import com.dedany.secretgift.presentation.helpers.Constants
import java.util.Calendar

class GameSettingsActivity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()
    private lateinit var binding: ActivityGameOptionsBinding
    private val gameSettingsViewModel: GameSettingsViewModel by viewModels()
    private lateinit var rulesAdapter: RulesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gameId = intent.getIntExtra(Constants.KEY_GAME_ID, -1)

        val eventDate = intent.getStringExtra("EVENT_DATE") ?: ""
        val maxPrice = intent.getStringExtra("MAX_PRICE") ?: ""
        val minPrice = intent.getStringExtra("MIN_PRICE") ?: ""

        gameSettingsViewModel.setEventDate(eventDate)
        gameSettingsViewModel.setMaxPrice(maxPrice)
        gameSettingsViewModel.setMinPrice(minPrice)

        initAdapters()
        initObservers()
        initListeners()
    }

    private fun initObservers() {

        gameSettingsViewModel.eventDate.observe(this) { eventDate ->
            binding.editTextEventDate.setText(eventDate)
        }

        // Observa el cambio del precio máximo
        gameSettingsViewModel.maxPrice.observe(this) { maxPrice ->
            binding.editTextMaxPriceOptions.setText(maxPrice)
        }

        // Observa el cambio del precio mínimo
        gameSettingsViewModel.minPrice.observe(this) { minPrice ->
            binding.editTextMinPriceOptions.setText(minPrice)
        }
        gameSettingsViewModel.rules.observe(this) { rules ->
            rulesAdapter.submitList(rules)
        }
    }

    private fun initListeners() {
        // DataPicker
        binding.editTextEventDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val selectedDate = "$dayOfMonth-${month + 1}-$year"
                binding.editTextEventDate.setText(selectedDate)
                gameSettingsViewModel.setEventDate(selectedDate)  // Actualiza el ViewModel
            }, year, month, day)
            datePickerDialog.show()
        }

        // Guardar cambios
        binding.btnSaveChanges.setOnClickListener {
            val eventDate = binding.editTextEventDate.text.toString()
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

    // Inicializa el adaptador para las reglas de incompatibilidad
    private fun initAdapters() {
        rulesAdapter = RulesAdapter(listOf("Player 1", "Player 2", "Player 3", "Player 4", "Player 5")) {
            gameSettingsViewModel.removeRuleAt(it)
        }
        binding.recyclerViewRules.adapter = rulesAdapter
        rulesAdapter.submitList(
            listOf(
                Rule("Player 1", "Player 2"),
                Rule("Player 3", "Player 4")
            )
        )  // Establece una lista de reglas predeterminadas
    }
}
