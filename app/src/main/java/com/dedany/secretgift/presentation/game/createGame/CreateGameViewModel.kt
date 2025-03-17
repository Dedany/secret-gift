package com.dedany.secretgift.presentation.game.createGame

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

    private var _isGameNameValid: MutableLiveData<Boolean> = MutableLiveData()
    val isGameNameValid: LiveData<Boolean> = _isGameNameValid

    private val _showConfirmationDialog = MutableLiveData<Boolean>()
    val showConfirmationDialog: LiveData<Boolean> get() = _showConfirmationDialog

    private var _isGameSavedSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isGameSavedSuccess: LiveData<Boolean> = _isGameSavedSuccess

    private var _email: MutableLiveData<String> = MutableLiveData()
    val email: LiveData<String> = _email

    private var _isGameCreatedSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isGameCreatedSuccess: LiveData<Boolean> = _isGameCreatedSuccess

    private var _insufficientDataMessage: MutableLiveData<String> = MutableLiveData("")
    val insufficientDataMessage: LiveData<String> = _insufficientDataMessage

    private var _emailDataMessage: MutableLiveData<String> = MutableLiveData("")
    val emailDataMessage: LiveData<String> = _emailDataMessage

    private var _player: MutableLiveData<List<Player>> = MutableLiveData()
    val player: LiveData<List<Player>> = _player
    private var _players: MutableLiveData<List<Player>> = MutableLiveData(listOf())
    val players: LiveData<List<Player>> = _players


    private val _ownerId = MutableLiveData<String>()
    val ownerId: LiveData<String> get() = _ownerId

    private var gameName: String = ""
    private var gameId: Int = 0
    private var playerList = mutableListOf<Player>()
    private var playerEmail: String = ""

    private var eventDate: Date = Date()
    private var maxPrice: Int = 0
    private var minPrice: Int = 0
    private var rules: List<Rule> = listOf()

    fun fetchOwnerEmail() {
        viewModelScope.launch {
            _ownerId.value = useCase.getRegisteredUser().email
        }
    }

    fun addCreatingUserToPlayers() {
        viewModelScope.launch {
            try {
                val user = useCase.getRegisteredUser()
                val creatingPlayer = Player(name = user.name, email = user.email)
                playerList.add(creatingPlayer)
                _players.value = playerList.toList()
            } catch (e: Exception) {
                Log.e("CreateGameViewModel", "Error adding creating user: ${e.message}")
                _insufficientDataMessage.value = "Error al añadir el usuario creador"
            }
        }
    }
    fun checkName() {
        if (gameName.isNotEmpty() && gameName.length > 1) {

            _isGameNameValid.value = true
        } else {
            _isGameNameValid.value = false
            _insufficientDataMessage.value = "El nombre del juego necesita un mínimo de 1 letras"
        }
    }

    //NOMBRE DEL JUEGO
    fun setName(name: String) {
        gameName = name
        checkName()
        createOrUpdateGame()
    }

    //CORREO DEL JUGADOR
    fun setEmail(email: String) {
        playerEmail = email
    }

    //ID JUEGO
    fun setGameId(id: Int) {
        this.gameId = id
    }

    //ELIMINAR JUGADOR
    fun deletePlayer(player: Player) {
        playerList.remove(player)
        _players.value = playerList.toList()
        createOrUpdateGame()
    }

    //EDITAR JUGADOR
    fun editPlayer(player: Player, newName: String) {
        val playerIndex = playerList.indexOf(player)
        if (playerIndex != -1) {
            playerList[playerIndex].name = newName
            _players.value = playerList.toList()
        }

        createOrUpdateGame()

    }

    fun onSaveGameClicked() {
        _showConfirmationDialog.value = true
    }

    fun onDialogDismissed() {
        _showConfirmationDialog.value = false
    }

    fun createOrUpdateGame() {
        if (_isGameNameValid.value == true) {
            viewModelScope.launch {
                try {
                    // Verifica si el ID del juego es válido para determinar si es nuevo o existente
                    if (gameId != 0) {
                        val existingGame = gamesUseCase.getLocalGamesById(gameId)
                        // Si el juego existe, se actualiza
                        if (existingGame.id != 0) {
                            updateGame()
                        } else {
                            // Si no, se crea
                            createGame()
                        }
                    } else {
                        createGame()
                    }
                } catch (e: Exception) {
                    Log.e("CreateGameViewModel", "Error: ${e.message}")
                    _insufficientDataMessage.value = "Ocurrió un error al procesar el juego"
                }
            }
        }
    }


    private fun createGame() {
        if (gameName.isNotEmpty() && gameName.length > 3 && checkMinimumPlayers()) {
            viewModelScope.launch {
                val ownerId = useCase.getRegisteredUser().id
                val localGame = LocalGame(ownerId = ownerId, name = gameName, players = playerList)

                val newGameId = gamesUseCase.createLocalGame(localGame).toInt()
                gameId = newGameId
                _isGameCreatedSuccess.value = true
            }
        }
    }

    private fun updateGame() {
        viewModelScope.launch {
            val ownerId = useCase.getRegisteredUser().id

            val updatedGame = LocalGame(
                id = gameId,
                ownerId = ownerId,
                name = gameName,
                players = playerList,
                maxCost = maxPrice,
                minCost = minPrice,
                gameDate = eventDate,
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


    fun checkMinimumPlayers(): Boolean {
        if (playerList.size < 3) {
            _insufficientDataMessage.value = "Necesitas al menos 3 jugadores"
            return false
        } else {
            return true
        }
    }

    private fun checkEventDate(): Boolean {

        return true
    }

    //COMPRUEBA LOS CAMPOS OBLIGATORIOS ANTES DE MANDAR AL BACKEND
    fun checkGame(): Boolean {
        checkName()
        checkMinimumPlayers()
        checkEventDate()
        return _isGameNameValid.value == true && checkMinimumPlayers() && checkEventDate()
    }


    //AÑADIR JUGADORES A LA LISTA
    fun addPlayer(name: String, email: String) {

        val existingPlayers = _players.value ?: emptyList()

        val nameExists = existingPlayers.any { it.name.equals(name, ignoreCase = true) }
        val emailExists = existingPlayers.any { it.email == email }

        if (nameExists) {
            _emailDataMessage.value = "El nombre ya está registrado"
            return
        }
        if (emailExists) {
            _emailDataMessage.value = "El email ya está registrado"
            return
        }

        val newPlayer = Player(name = name, email = email)
        playerList.add(newPlayer)
        _players.value = playerList.toList()
        createOrUpdateGame()

    }



    // Métodos para almacenar y configurar los valores recibidos de GameSettingsViewModel
    fun setGameSettings(
        eventDate: String,
        maxPrice: String,
        minPrice: String,
        rules : List<Rule>
    ) {

        this.eventDate = parseDate(eventDate)
        this.maxPrice = maxPrice.toIntOrNull() ?: 0
        this.minPrice = minPrice.toIntOrNull() ?: 0
        this.rules = rules

        Log.d(
            "CreateGameViewModel",
            "Fecha del evento: $eventDate, Max Price: $maxPrice, Min Price: $minPrice"
        )
    }

    private fun parseDate(dateString: String): Date {
        return try {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            dateFormat.parse(dateString) ?: Date()
        } catch (e: Exception) {
            Date() // Retorna la fecha actual si el formato es inválido
        }
    }

    fun saveGame() {
        viewModelScope.launch {
            try {
                if (true) {

                    val isGameSaved = gamesUseCase.createGame(gameId)

                    _isGameSavedSuccess.value = isGameSaved
                }
            } catch (e: Exception) {
                Log.e("CreateGameViewModel", "Error al guardar el juego: ${e.message}")
                _isGameSavedSuccess.value = false
            }
        }
    }


    //CARGAR JUGADOR
    fun loadPlayer() {
        viewModelScope.launch {
            try {
                val game = gamesUseCase.getLocalGame(gameId)
                _players.value = game.players
            } catch (e: Exception) {
                _players.value = emptyList()
            }
        }
    }

    fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            _emailDataMessage.value = "El correo electrónico no puede estar vacío"
            return false
        }

        if (!isValidEmail(email)) {
            _emailDataMessage.value = "Por favor, ingresa un correo electrónico válido"
            return false
        }

        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

/*    private fun checkEventDate(): Boolean {
        if (eventDate.isEmpty()) {
            _insufficientDataMessage.value = "La fecha del evento es obligatoria"
            return false
        }

        return true
    }*/