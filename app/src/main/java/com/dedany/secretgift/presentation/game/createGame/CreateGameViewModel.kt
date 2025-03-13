package com.dedany.secretgift.presentation.game.createGame

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dedany.secretgift.domain.entities.LocalGame
import com.dedany.secretgift.domain.entities.Player
import com.dedany.secretgift.domain.entities.SavePlayer
import com.dedany.secretgift.domain.usecases.games.GamesUseCase
import com.dedany.secretgift.domain.usecases.users.UsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    private var _player: MutableLiveData<List<Player>> = MutableLiveData()
    val player: LiveData<List<Player>> = _player
    private var _players: MutableLiveData<List<Player>> = MutableLiveData(listOf())
    val players: LiveData<List<Player>> = _players

    private var gameName: String = ""
    private var gameId: Int = 0
    private var playerList = mutableListOf<Player>()
    private var playerEmail: String = ""
    private var savePlayerList = listOf<SavePlayer>()
    //Settings
    private var eventDate: String = ""
    private var numPlayers: String = ""
    private var maxPrice: String = ""
    private var incompatibilities: List<Pair<String, String>> = emptyList()

    fun checkName() {
   if(gameName.isNotEmpty() && gameName.length > 3) {

       _isGameNameValid.value = true
   } else {
       _isGameNameValid.value = false
       _insufficientDataMessage.value = "El nombre del juego necesita un mínimo de 4 letras"
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
        createGame()
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
            val updatedGame = LocalGame(id = gameId, ownerId = ownerId, name = gameName, players = playerList)

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

    fun checkGame(): Boolean {
        checkName()
        checkMinimumPlayers()
        return _isGameNameValid.value == true && checkMinimumPlayers()
    }



    //AÑADIR JUGADORES A LA LISTA
    fun addPlayer(name: String, email: String) {
        val newPlayer = Player(name = name, email = email)
        playerList.add(newPlayer)
        _players.value = playerList.toList()
        createOrUpdateGame()

    }
    fun setGameSettings(eventDate: String, numPlayers: String, maxPrice: String, incompatibilities: List<Pair<String, String>>) {
        this.eventDate = eventDate
        this.numPlayers = numPlayers
        this.maxPrice = maxPrice
        this.incompatibilities = incompatibilities
    }

    fun saveGame() {
        viewModelScope.launch {
            try {
                if (checkGame()) {
                    val ownerId = useCase.getRegisteredUser().id
                    convertPlayersToSavePlayers()
                    val isGameSaved = gamesUseCase.saveGameToBackend(
                        gameId,
                        ownerId,
                        gameName,
                        savePlayerList,
                        eventDate,
                        numPlayers,
                        maxPrice,
                        incompatibilities
                    )


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

    fun convertPlayersToSavePlayers() {
        savePlayerList = playerList.map { player ->
            // Aquí es necesario determinar cómo obtener 'linkedTo'.
            // Si no tienes esta información en el objeto Player, deberás agregar lógica para obtenerla.
            SavePlayer(
                name = player.name,
                email = player.email,
                linkedTo = getLinkedTo(player)
            )
        }
    }

    fun getLinkedTo(player: Player): String {
        // Lógica para determinar qué jugador está vinculado a este jugador (es decir, 'linkedTo')
        // Ejemplo de cómo se podría hacer si tienes una lista de incompatibilidades o alguna otra estructura.

        return "" // Retorna el valor adecuado o una cadena vacía si no hay vinculación.
    }
}