package com.dedany.secretgift.presentation.game.createGame

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dedany.secretgift.databinding.ActivityGameOptionsBinding
import com.dedany.secretgift.domain.entities.Rule
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

        initAdapters()
        initObservers()
        initListeners()
    }


    private fun initObservers() {

        gameSettingsViewModel.rules.observe(this) { rules ->
            rulesAdapter.submitList(rules)
        }
    }


    private fun initListeners() {

        //Datapicker
        binding.editTextEventDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val selectedDate = "$dayOfMonth-${month + 1}-$year"
                binding.editTextEventDate.setText(selectedDate)
            }, year, month, day)
            datePickerDialog.show()
        }


        binding.btnSaveChanges.setOnClickListener {
            val eventDate = binding.editTextEventDate.text.toString()
            val maxPrice = binding.editTextMaxPriceOptions.text.toString()
            val minPrice = binding.editTextMinPriceOptions.text.toString()
            val incompatibilities = getIncompatibilities()

            val rules = incompatibilities.map { Rule(it.first, it.second) }
            gameSettingsViewModel.setRules(rules)

            val intent = Intent(this, CreateGameActivity::class.java)
            intent.putExtra("EVENT_DATE", eventDate)
            intent.putExtra("MAX_PRICE", maxPrice)
            intent.putExtra("MIN_PRICE", minPrice)
            intent.putExtra("RULES", ArrayList(rules))
            //rules

            setResult(RESULT_OK, intent)
            finish()
        }


        binding.iconBack.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()  // Cierra la actividad
        }


        binding.btnAddIncompatibilities.setOnClickListener {
            gameSettingsViewModel.addNewRule()
        }
    }


    private fun getIncompatibilities(): List<Pair<String, String>> {
        val adapter = binding.recyclerViewRules.adapter as RulesAdapter
        val rules = adapter.currentList
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

        rulesAdapter =
            RulesAdapter(listOf("Player 1", "Player 2", "Player 3", "Player 4", "Player 5")) {
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
