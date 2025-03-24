package com.dedany.secretgift.presentation.game.createGame

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.Observer
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
        initAdapters()

        val gameId = intent.getIntExtra(Constants.KEY_GAME_ID, -1)
        if (gameId != -1) {
            gameSettingsViewModel.getLocalGameById(gameId)
        }

        initListeners()
        initObservers()
    }

    private fun initAdapters() {
        val playersList = intent.getSerializableExtra("PLAYERS_LIST") as? List<Player> ?: emptyList()

        val auxPlayersList = playersList.subList(1,playersList.size)

        rulesAdapter = RulesAdapter(playersList.map { it.name }, auxPlayersList.map{ it.name }) {
            gameSettingsViewModel.removeRuleAt(it)
        }

        binding.recyclerViewRules.adapter = rulesAdapter
    }

    private fun initObservers() {

        //invisibilidad del recylclerView si no hay reglas
        gameSettingsViewModel.rules.observe(this) { rules ->
            rulesAdapter.submitList(rules)

            if(rules.isNotEmpty()){
                binding.recyclerViewRules.visibility = View.VISIBLE
                binding.placeHolderOptions.visibility = View.GONE
                binding.textViewIncompatibilities.visibility = View.VISIBLE

            }else {
                binding.recyclerViewRules.visibility = View.GONE
                binding.placeHolderOptions.visibility = View.VISIBLE
                binding.textViewIncompatibilities.visibility = View.GONE

            }
        }

        gameSettingsViewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
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

        gameSettingsViewModel.isSaving.observe(this, Observer { isSaving ->
            if (isSaving) {
                binding.loader.visibility = View.VISIBLE
                binding.cardViewOptions.visibility =View.GONE
            } else {
                binding.loader.visibility = View.GONE
            }
        })
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

        binding?.recyclerViewRules?.visibility= if (gameSettingsViewModel.rules.value?.isNotEmpty() == true) View.VISIBLE else View.GONE


        binding.btnSaveChanges.setOnClickListener {
            if (gameSettingsViewModel.isDateSelected.value == false) {
                Toast.makeText(this, "Por favor, selecciona una fecha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val eventDate = gameSettingsViewModel.eventDate.value!!
            val maxPrice = binding.editTextMaxPriceOptions.text.toString()
            val minPrice = binding.editTextMinPriceOptions.text.toString()
            val incompatibilities = getIncompatibilities()

            // Crea el mapa de restricciones a partir de las incompatibilidades
            val restrictions = incompatibilities.groupBy(
                { it.first },
                { it.second }
            ).mapValues { it.value.toSet() }

            val rules = incompatibilities.map { Rule(it.first, it.second) }
            gameSettingsViewModel.setRules(rules)

            // Llamada a la función canAssignGift
            gameSettingsViewModel.canAssignGift(
                participants = gameSettingsViewModel.playerList.value ?: emptyList(),
                restrictions = restrictions
            ) { canAssign ->
                if (canAssign) {
                    // Si es posible asignar regalos, guardar el juego
                    gameSettingsViewModel.saveGame(eventDate, maxPrice, minPrice, rules) {
                        val intent = Intent(this, CreateGameActivity::class.java).apply {
                            putExtra("EVENT_DATE", eventDate)
                            putExtra("MAX_PRICE", maxPrice)
                            putExtra("MIN_PRICE", minPrice)
                            putExtra("RULES", ArrayList(rules))
                        }

                        setResult(RESULT_OK, intent)
                        finish()
                    }
                } else {
                    // Mostrar mensaje si no es posible asignar regalos
                    Toast.makeText(this, "No se pueden asignar los regalos debido a restricciones", Toast.LENGTH_SHORT).show()
                } }

        }

        binding.iconBack.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
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