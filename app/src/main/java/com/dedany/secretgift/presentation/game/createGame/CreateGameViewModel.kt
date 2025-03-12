package com.dedany.secretgift.presentation.game.createGame

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dedany.secretgift.domain.entities.LocalGame
import com.dedany.secretgift.domain.entities.Player
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

    private val _createdGame = MutableLiveData<LocalGame?>()
    val createdGame: LiveData<LocalGame?> = _createdGame

    private var _players: MutableLiveData<List<Player>> = MutableLiveData(listOf())
    val players: LiveData<List<Player>> = _players

    private var gameName: String = ""
    private var gameId: Int = 0
    private var playerList = mutableListOf<Player>()
    private var playerEmail: String = ""

    fun checkName() {
        _isGameNameValid.value = gameName.isNotEmpty() && gameName.length > 3
    }

    //NOMBRE DEL JUEGO
    fun setName(name: String) {
        gameName = name
        checkName()
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

        createGame()

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
                    // Buscar juego existente por nombre
                    val existingGame = gamesUseCase.getLocalGamesByName(gameName)

                    if (existingGame.id == 0) { // Si no existe, creamos uno nuevo
                        createGame()
                    } else {
                        gameId = existingGame.id
                        updateGame() // Si existe, actualizamos el juego
                    }
                } catch (e: Exception) {

                    Log.e("CreateGameViewModel", "Error buscando el juego: ${e.message}")
                    createGame()
                }
            }
        }
    }


    fun createGame() {
        if (gameName.isNotEmpty() && gameName.length > 3) {
            viewModelScope.launch {
                val ownerId = useCase.getRegisteredUser().id
                val localGame = LocalGame(ownerId = ownerId, name = gameName, players = playerList)

                val gameId = gamesUseCase.createLocalGame(localGame).toInt()
                setGameId(gameId)

                val updatedGame = localGame.copy(id = gameId)
                _createdGame.value = updatedGame

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
                _createdGame.value = updatedGame
                _isGameCreatedSuccess.value = true
            } else {
                _isGameCreatedSuccess.value = false
            }
        }
    }


    //AÑADIR JUGADORES A LA LISTA
    fun addPlayer(name: String, email: String) {
        val newPlayer = Player(name = name, email = email)
        playerList.add(newPlayer)
        _players.value = playerList.toList()

    }

    fun saveGame() { //API
        viewModelScope.launch {
            try {
                val gameSaved = gamesUseCase.createGame(gameId)
                _isGameSavedSuccess.value = gameSaved
            } catch (e: Exception) {
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
}