package com.dedany.secretgift.presentation.game.createGame

import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dedany.secretgift.domain.entities.LocalGame
import com.dedany.secretgift.domain.entities.Rule
import com.dedany.secretgift.domain.usecases.games.GamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GameSettingsViewModel @Inject constructor(
    private val gamesUseCase: GamesUseCase
) : ViewModel() {

    private val _eventDate = MutableLiveData<String>()
    val eventDate: LiveData<String> get() = _eventDate

    private val _isSaving = MutableLiveData<Boolean>(false)
    val isSaving: LiveData<Boolean> get() = _isSaving

    private val _maxPrice = MutableLiveData<String>()
    val maxPrice: LiveData<String> get() = _maxPrice

    private val _minPrice = MutableLiveData<String>()
    val minPrice: LiveData<String> get() = _minPrice

    private val _rules = MutableLiveData<List<Rule>>(listOf())
    val rules: LiveData<List<Rule>> = _rules

    // Métodos para actualizar los valores
    fun setEventDate(date: String) {
        _eventDate.value = date
    }

    fun setMaxPrice(price: String) {
        _maxPrice.value = price
    }

    fun setMinPrice(price: String) {
        _minPrice.value = price
    }

    fun addNewRule() {
        val rules = _rules.value ?: emptyList()
        _rules.value = rules.plus(Rule("F1", "F2"))
    }

    fun removeRuleAt(position: Int) {
        val rules = _rules.value?.toMutableList() ?: mutableListOf()
        rules.removeAt(position)
        _rules.value = rules
    }

    fun setRules(newRules: List<Rule>) {
        _rules.value = newRules
    }
    fun saveGame(eventDate: String, maxPrice: String, minPrice: String, rules: List<Rule>, onComplete: () -> Unit) {
        _isSaving.value = true

        viewModelScope.launch {
            try {
                delay(2000)
                Log.d("GameSettingsViewModel", "Juego guardado exitosamente")
                _isSaving.value = false
                onComplete()
            } catch (e: Exception) {
                _isSaving.value = false
                Log.e("GameSettingsViewModel", "Error al guardar el juego: ${e.message}")
            }
        }
    }
    fun getLocalGameById(id: Int) {
        viewModelScope.launch {
            try {
                val game = gamesUseCase.getLocalGame(id)

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = game.gameDate?.let { dateFormat.format(it) } ?: ""

                _eventDate.value = formattedDate
                _maxPrice.value = game.maxCost?.toString() ?: ""
                _minPrice.value = game.minCost?.toString() ?: ""
                _rules.value = game.rules
            } catch (e: Exception) {
                // Manejo de errores
                Log.e("GameSettingsViewModel", "Error al obtener el juego: ${e.message}")
            }
        }
    }
}

