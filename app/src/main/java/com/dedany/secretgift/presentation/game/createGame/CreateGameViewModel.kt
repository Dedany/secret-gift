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

    private var _isGameCreatedSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isGameCreatedSuccess: LiveData<Boolean> = _isGameCreatedSuccess

    private var _insufficientDataMessage: MutableLiveData<String> = MutableLiveData("")
    val insufficientDataMessage: LiveData<String> = _insufficientDataMessage

    private var _player: MutableLiveData<List<Player>> = MutableLiveData()
    val player: LiveData<List<Player>> = _player

    private var gameName: String = ""
    private var gameId: Int = 0
    private val playerList = mutableListOf<Player>()

    fun checkName() {
   if(gameName.isNotEmpty() && gameName.length > 3) {

       _isGameNameValid.value = true
   } else {
       _isGameNameValid.value = false
       _insufficientDataMessage.value = "El nombre del juego necesita un mínimo de 4 letras"
   }
    }

    fun setName(name: String) {
        gameName = name

    }

    fun setGameId(id: Int) {
       gameId = id
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

    fun createGame() {
        viewModelScope.launch {
            checkName()
            checkMinimumPlayers()
            if (_isGameNameValid.value == true && playerList.size >= 3) {
                val ownerId = useCase.getRegisteredUser().id
                val localGame = LocalGame(ownerId = ownerId, name = gameName)
                gamesUseCase.createLocalGame(localGame)
                setGameId(localGame.id)
                _isGameCreatedSuccess.value = true
            } else {


            }
        }

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
}