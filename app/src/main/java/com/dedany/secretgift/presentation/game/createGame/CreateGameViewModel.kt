package com.dedany.secretgift.presentation.game.createGame

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dedany.secretgift.domain.entities.LocalGame
import com.dedany.secretgift.domain.entities.Player
import com.dedany.secretgift.domain.entities.Rule
import com.dedany.secretgift.domain.usecases.games.GamesUseCase
import com.dedany.secretgift.domain.usecases.users.UsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CreateGameViewModel @Inject constructor(
    private val gamesUseCase: GamesUseCase,
    private val useCase: UsersUseCase
) : ViewModel() {

    private val _showConfirmationDialog = MutableLiveData<Boolean>()
    val showConfirmationDialog: LiveData<Boolean> get() = _showConfirmationDialog

    private var playerList = mutableListOf<Player>()


    private var _isGameSavedSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isGameSavedSuccess: LiveData<Boolean> = _isGameSavedSuccess

    private var _email: MutableLiveData<String> = MutableLiveData()
    val email: LiveData<String> = _email

    private var _isGameCreatedSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isGameCreatedSuccess: LiveData<Boolean> = _isGameCreatedSuccess

    private var _nameErrorMessage: MutableLiveData<String> = MutableLiveData("")
    val nameErrorMessage: LiveData<String> = _nameErrorMessage

    private var _playersErrorMessage: MutableLiveData<String> = MutableLiveData("")
    val playersErrorMessage: LiveData<String> = _playersErrorMessage

    private var _dateErrorMessage: MutableLiveData<String> = MutableLiveData("")
    val dateErrorMessage: LiveData<String> = _dateErrorMessage

    private var _emailDataMessage: MutableLiveData<String> = MutableLiveData("")
    val emailDataMessage: LiveData<String> = _emailDataMessage

    private val _validationError = MutableLiveData<String?>()
    val validationError: LiveData<String?> get() = _validationError

    private var _player: MutableLiveData<List<Player>> = MutableLiveData()
    val player: LiveData<List<Player>> = _player
    private var _players: MutableLiveData<List<Player>> = MutableLiveData(listOf())
    val players: LiveData<List<Player>> = _players

    private val _localGame: MutableLiveData<LocalGame> = MutableLiveData()
    val localGame: LiveData<LocalGame> = _localGame

    private val _isSaving = MutableLiveData<Boolean>()
    val isSaving: LiveData<Boolean> get() = _isSaving


    private val _ownerId = MutableLiveData<String>()
    val ownerId: LiveData<String> get() = _ownerId

    private var gameName: String = ""
    private var gameId: Int = 0

    private var playerEmail: String = ""

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate
    var maxPrice: Int = 0
    var minPrice: Int = 0
    private var rules: List<Rule> = listOf()

    private var isUserAdded = false
    private val _addPlayerResult = MutableLiveData<AddPlayerResult>()
    val addPlayerResult: LiveData<AddPlayerResult> = _addPlayerResult

    private val handler = Handler(Looper.getMainLooper())
    private var saveRunnable: Runnable? = null
    private val delayMillis: Long = 2000
    private var lastText = ""
    private var currentText = ""

    fun addCreatingUserToPlayers() {
        if (isUserAdded) return

        viewModelScope.launch {
            try {
                val user = useCase.getRegisteredUser()
                if (user.name.isEmpty() || user.email.isEmpty()) {
                    _validationError.value = "Usuario no tiene nombre o correo registrado."
                    return@launch
                }
                val creatingPlayer = Player(name = user.name, email = user.email)
                playerList.add(creatingPlayer)
                _players.value = playerList.toList()
                isUserAdded = true
            } catch (e: Exception) {
                _validationError.value = "Error al agregar el jugador: ${e.message ?: "Desconocido"}"
            }
        }
    }


    fun setName(name: String) {
        if (name.isEmpty()) {
            _validationError.value = "El nombre de la sala no puede estar vacío."
            return
        }
        gameName = name
        onTextChanged(name)
    }


    fun fetchOwnerEmail() {
        viewModelScope.launch {
            try {
                val user = useCase.getRegisteredUser()
                _ownerId.value = user.email
            } catch (e: Exception) {
                _validationError.value = "Error al obtener el correo del usuario: ${e.message}"
            }
        }
    }

    fun getGameId(): Int {
        if (gameId == 0) {
            _validationError.value = "El ID de la sala no está configurado correctamente."
        }
        return gameId
    }

    //ID JUEGO
    fun setGameId(id: Int) {
        this.gameId = id
    }

    //ELIMINAR JUGADOR
    fun deletePlayer(player: Player) {
        try {
            if (playerList.contains(player)) {
                playerList.remove(player)
                _players.value = playerList.toList()
                createOrUpdateGame()
            } else {
                _validationError.value = "El jugador no existe en la lista."
            }
        } catch (e: Exception) {
            _validationError.value = "Error al eliminar el jugador: ${e.message}"
        }
    }

    fun getPlayersList(): List<Player> {
        return _players.value ?: emptyList()
    }

    fun editPlayer(oldPlayer: Player, newName: String, newEmail: String) {
        if (newName.isBlank()) {
            _validationError.value = "El nombre del jugador no puede estar vacío."
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            _validationError.value = "Correo electrónico inválido."
            return
        }
        val updatedPlayers = _players.value?.map { player ->
            if (player == oldPlayer) player.copy(name = newName, email = newEmail) else player
        } ?: return

        _players.value = updatedPlayers
        createOrUpdateGame()
    }



    fun onSaveGameClicked() {
        _showConfirmationDialog.value = true
    }

    fun onDialogDismissed() {
        _showConfirmationDialog.value = false
    }

    fun createOrUpdateGame() {
        viewModelScope.launch {
            try {
                if (gameId != 0) {
                    val existingGame = gamesUseCase.getLocalGamesById(gameId)
                    if (existingGame.id != 0) {
                        updateGame()
                    } else {
                        _validationError.value = "No se pudo encontrar la sala para actualizar."
                    }
                } else {
                    createGame()
                }
            } catch (e: Exception) {
                _validationError.value = "Error al crear o actualizar la sala: ${e.message}"
            }
        }
    }


    fun createGame() {
        if (gameName.isNotEmpty() && gameName.length > 3) {
            viewModelScope.launch {
                try {
                    val ownerId = useCase.getRegisteredUser().id
                    val localGame = LocalGame(ownerId = ownerId, name = gameName, players = playerList)
                    val newGameId = gamesUseCase.createLocalGame(localGame).toInt()
                    gameId = newGameId
                    _isGameCreatedSuccess.value = true
                } catch (e: Exception) {
                    _validationError.value = "Hubo un error al crear la sala. Intenta de nuevo."
                    _isGameCreatedSuccess.value = false
                }
            }
        } else {
            _validationError.value = "El nombre de sala debe ser al menos de 3 caracteres."
        }
    }

    fun updateGame() {
        viewModelScope.launch {
            val ownerId = useCase.getRegisteredUser().id
            val parsedDate = selectedDate.value?.let { parseDate(it) }
            val updatedGame = LocalGame(
                id = gameId,
                ownerId = ownerId,
                name = gameName,
                players = playerList,
                maxCost = maxPrice,
                minCost = minPrice,
                gameDate = parsedDate,
                rules = rules
            )

            val result = gamesUseCase.updateLocalGame(updatedGame)

            if (result > 0) {
                _isGameCreatedSuccess.value = true
            } else {
                _isGameCreatedSuccess.value = false
            }
        }
    }

    fun checkGame(): Boolean {
        if (!checkName()) return false
        if (!checkMinimumPlayers()) return false
        if (!checkEventDate()) return false
        _validationError.value = null
        return true
    }

    fun checkName(): Boolean {
        return if (gameName.length < 3) {
            _validationError.value = "El nombre de sala debe tener al menos 3 caracteres"
            false
        } else {
            true
        }
    }

    private fun checkMinimumPlayers(): Boolean {
        if (playerList.size < 3) {
            _validationError.value = "La sala debe tener al menos 3 jugadores"
            return false
        }
        return true
    }

    private fun checkEventDate(): Boolean {
        if (selectedDate.value == null) {
            _validationError.value = "La fecha del evento es obligatoria"
            return false
        }
        return true
    }



    fun loadLocalGameById(id: Int) {
        viewModelScope.launch {
            try {
                val game = gamesUseCase.getLocalGame(id)

                gameId = game.id
                playerList = game.players.toMutableList()
                _players.value = playerList.toList()

                if (game.gameDate != null) {
                    _selectedDate.value = formatDate(game.gameDate)
                } else {
                    _selectedDate.value = "Fecha no disponible"
                }

                maxPrice = game.maxCost ?: 0
                minPrice = game.minCost ?: 0
                rules = game.rules

                _localGame.value = game

            } catch (e: Exception) {
                _validationError.value = "Ocurrió un error al cargar la sala. Intenta nuevamente más tarde."
            }
        }
    }

    //AÑADIR JUGADORES A LA LISTA
    fun addPlayer(name: String, email: String) {
        if (name.isEmpty() || email.isEmpty()) {
            _addPlayerResult.value = AddPlayerResult.Error("Por favor, completa todos los campos")
            return
        }

        if (!isValidEmail(email)) {
            _addPlayerResult.value = AddPlayerResult.Error("Por favor, ingresa un correo electrónico válido")
            return
        }

        val existingPlayers = _players.value ?: emptyList()
        val emailExists = existingPlayers.any { it.email == email }
        val nameExists = existingPlayers.any { it.name == name }
        if (emailExists) {
            _addPlayerResult.value = AddPlayerResult.Error("Ya hay un jugador con este email")
            return
        }
        if (nameExists) {
            _addPlayerResult.value = AddPlayerResult.Error("Ya hay un jugador con este nombre")
            return
        }

        val newPlayer = Player(name = name, email = email)
        playerList.add(newPlayer)
        _players.value = playerList.toList()
        _addPlayerResult.value = AddPlayerResult.Success
        createOrUpdateGame()
    }



    fun setGameSettings(eventDate: String, maxPrice: String, minPrice: String, rules: List<Rule>) {
        if (eventDate.isEmpty()) {
            _validationError.value = "La fecha del evento no puede estar vacía."
            return
        }
        _selectedDate.value = eventDate
        val parsedMaxPrice = maxPrice.toIntOrNull()
        val parsedMinPrice = minPrice.toIntOrNull()
        if (parsedMaxPrice == null) {
            _validationError.value = "El precio máximo no es válido."
            return
        }
        if (parsedMinPrice == null) {
            _validationError.value = "El precio mínimo no es válido."
            return
        }
        this.maxPrice = parsedMaxPrice
        this.minPrice = parsedMinPrice
        this.rules = rules
    }


    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return format.format(date)
    }

    private fun parseDate(dateString: String): Date {
        return try {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            dateFormat.parse(dateString) ?: Date()
        } catch (e: Exception) {
            Date() // Retorna la fecha actual si el formato es inválido
        }
    }
    //Guardar Juego en Backend
    fun saveGame() {
        viewModelScope.launch {
            try {
                _isSaving.value = true


                val isGameSaved = gamesUseCase.createGame(gameId)
                _isGameSavedSuccess.value = isGameSaved

            } catch (e: Exception) {
                Log.e("CreateGameViewModel", "Error al guardar el juego: ${e.message}")
                _isGameSavedSuccess.value = false
            }
            _isSaving.value = false
        }
    }

    fun onTextChanged(newText: String) {
        currentText = newText

        if (lastText != currentText) {
            lastText = currentText
            restartTimer()
        }
    }

    private fun restartTimer() {
        saveRunnable?.let { handler.removeCallbacks(it) }

        saveRunnable = Runnable {
            if (currentText == lastText) {
                createOrUpdateGame()
            } else {
                Log.d("TextChanged", "El texto ha cambiado antes de que se actualice")
            }
        }

        // Postear el Runnable con el retraso especificado
        saveRunnable?.let { handler.postDelayed(it, delayMillis) }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    sealed class AddPlayerResult {
        object Success : AddPlayerResult()
        data class Error(val message: String) : AddPlayerResult()
    }

}

