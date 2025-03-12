package com.dedany.secretgift.presentation.game.createGame

import android.util.Log
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
                    val existingGame = gamesUseCase.getLocalGamesById(gameId)
                    gameId = existingGame.id
                    updateGame()
                } catch (e: Exception) {
                    Log.e("CreateGameViewModel", "Error buscando el juego: ${e.message}")
                    createGame()
                }
            }
        }
    }


    private fun createGame() {
        if (gameName.isNotEmpty() && gameName.length > 3 && checkMinimumPlayers()) {
            viewModelScope.launch {
                val ownerId = useCase.getRegisteredUser().id
                val localGame = LocalGame(ownerId = ownerId, name = gameName, players = playerList)

                val gameId = gamesUseCase.createLocalGame(localGame).toInt()
                this@CreateGameViewModel.gameId = gameId
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

    fun saveGame() {
        viewModelScope.launch {
            try {
                if (checkGame()) {
                    val isGameSaved = gamesUseCase.createGame(gameId)
                    _isGameSavedSuccess.value = isGameSaved
                }
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