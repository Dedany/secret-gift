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

    private var _isGameSavedSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isGameSavedSuccess: LiveData<Boolean> = _isGameSavedSuccess

    private var _player: MutableLiveData<List<Player>> = MutableLiveData()
    val player: LiveData<List<Player>> = _player

    private var gameName: String = ""
    private val playerList = mutableListOf<Player>()

    fun checkName() {
        _isGameNameValid.value = gameName.isNotEmpty() && gameName.length > 3
    }

    fun setName(name: String) {
        gameName = name
    }

    fun deletePlayer(player: Player) {
        playerList.remove(player)
        _player.value = playerList
    }

    fun createGame() {
        viewModelScope.launch {
            checkName()
            if (_isGameNameValid.value == true /*&& playerList.isNotEmpty()*/) {
                val ownerId = useCase.getRegisteredUser().id
                gamesUseCase.createLocalGame(LocalGame(ownerId= ownerId, name =gameName))
                _isGameSavedSuccess.value = true
            } else {


            }
        }

    }
}