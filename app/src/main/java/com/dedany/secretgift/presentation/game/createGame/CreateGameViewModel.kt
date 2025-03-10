package com.dedany.secretgift.presentation.game.createGame

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

    private var _player: MutableLiveData<List<Player>> = MutableLiveData()
    val player: LiveData<List<Player>> = _player

    private var gameName: String = ""
    private var gameId: Int = 0
    private val playerList = mutableListOf<Player>()
    private var playerEmail: String= ""

    fun checkName() {
        _isGameNameValid.value = gameName.isNotEmpty() && gameName.length > 3
    }

    fun setName(name: String) {
        gameName = name
    }
    fun setEmail(email: String) {
        playerEmail = email
    }

    fun setGameId(id: Int) {
        this.gameId = id
    }

    fun deletePlayer(player: Player) {
        playerList.remove(player)
        _player.value = playerList
    }

    fun onSaveGameClicked() {
        _showConfirmationDialog.value = true
    }

    fun onDialogDismissed() {
        _showConfirmationDialog.value = false
    }

    fun createGame() {  //NOMBRE DEL JUEGO EN ROOM
        viewModelScope.launch {
            checkName()
            if (_isGameNameValid.value == true /*&& playerList.isNotEmpty()*/) {
                val ownerId = useCase.getRegisteredUser().id
                val localGame = LocalGame(ownerId = ownerId, name = gameName)
                gamesUseCase.createLocalGame(localGame)
                setGameId(localGame.id)
                _isGameCreatedSuccess.value = true
            } else {


            }
        }

    }
    fun addPlayer(name: String,email: String){  //AÑADIR JUGADORES A LA LISTA
        val newPlayer = Player(name = name, email = email)
        playerList.add(newPlayer)
        _player.value = playerList
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

}