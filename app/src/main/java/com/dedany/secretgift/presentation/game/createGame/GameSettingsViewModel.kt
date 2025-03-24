package com.dedany.secretgift.presentation.game.createGame

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dedany.secretgift.domain.entities.Player
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

    private val _eventDate = MutableLiveData<String?>(null)
    val eventDate: LiveData<String?> get() = _eventDate

    private val _isDateSelected = MutableLiveData<Boolean>(false)
    val isDateSelected: LiveData<Boolean> get() = _isDateSelected

    private val _isSaving = MutableLiveData<Boolean>(false)
    val isSaving: LiveData<Boolean> get() = _isSaving

    private val _maxPrice = MutableLiveData<String>()
    val maxPrice: LiveData<String> get() = _maxPrice

    private val _minPrice = MutableLiveData<String>()
    val minPrice: LiveData<String> get() = _minPrice

    private val _playerList = MutableLiveData<List<Player>>(emptyList()) // Inicializamos con una lista vacía
    val playerList: LiveData<List<Player>> get() = _playerList


    private val _rules = MutableLiveData<List<Rule>>(listOf())
    val rules: LiveData<List<Rule>> = _rules

    // Métodos para actualizar los valores
    fun setEventDate(date: String?) {
        _eventDate.value = date
        _isDateSelected.value = date != null
    }
    fun setPlayerList(players: List<Player>) {
        _playerList.value = players
    }

    fun setMaxPrice(price: String) {
        _maxPrice.value = price
    }

    fun setMinPrice(price: String) {
        _minPrice.value = price
    }

    fun canAssignGift(participants: List<Player>, restrictions: Map<String, Set<String>>, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                // Llamamos al use case para verificar si es posible asignar regalos con las restricciones
                val canAssign = gamesUseCase.canAssignGift(participants, restrictions)

                // Retornamos el resultado a través del callback
                onComplete(canAssign)
            } catch (e: Exception) {
                // En caso de error, logueamos el mensaje de error y retornamos false
                Log.e("GameSettingsViewModel", "Error al verificar asignación de regalos: ${e.message}")
                onComplete(false)
            }
        }
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

                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val formattedDate = game.gameDate?.let { dateFormat.format(it) }
                _playerList.value = game.players
                _eventDate.value = formattedDate
                _isDateSelected.value = formattedDate != null // Update isDateSelected
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