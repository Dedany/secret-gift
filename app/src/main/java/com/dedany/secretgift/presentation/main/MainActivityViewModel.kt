package com.dedany.secretgift.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dedany.secretgift.domain.entities.GameSummary
import com.dedany.secretgift.domain.entities.LocalGame
import com.dedany.secretgift.domain.entities.User
import com.dedany.secretgift.domain.usecases.auth.AuthUseCase
import com.dedany.secretgift.domain.usecases.games.GamesUseCase
import com.dedany.secretgift.domain.usecases.users.UsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val gamesUseCase: GamesUseCase,
    private val authUseCase: AuthUseCase,
    private val usersUseCase: UsersUseCase
) : ViewModel() {

    private val _games: MutableLiveData<List<GameSummary>?> = MutableLiveData()
    val games: LiveData<List<GameSummary>?> = _games

    private val _localGames: MutableLiveData<List<LocalGame>?> = MutableLiveData()
    val localGames: LiveData<List<LocalGame>?> = _localGames

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User> = _user

    private val _isdeleting: MutableLiveData<Boolean> = MutableLiveData()
    val isdeleting: LiveData<Boolean> = _isdeleting

    private var _deletedGameMessage: MutableLiveData<String> = MutableLiveData()
    val deletedGameMessage: LiveData<String> = _deletedGameMessage


    fun loadGames() {
        viewModelScope.launch {
            _games.value = gamesUseCase.getGamesByUser()
        }
    }

    fun loadLocalGames() {
        viewModelScope.launch {
            _localGames.value = gamesUseCase.getLocalGamesByUser()
        }
    }

    fun loadUser() {
        viewModelScope.launch {
            val registeredUser = usersUseCase.getRegisteredUser()
            _user.value = registeredUser
        }
    }

    fun logout() {
        viewModelScope.launch {
            authUseCase.logout()
        }
    }

    fun deleteLocalGame(gameId: Int) {
        viewModelScope.launch {
            _isdeleting.value = true

            val isDeleted = gamesUseCase.deleteLocalGame(gameId)
            delay(1000)
            if (isDeleted) {
                _deletedGameMessage.value = "Juego borrado correctamente"
                loadLocalGames()
            } else {
                _deletedGameMessage.value = "Error al borrar el juego"
            }

            _isdeleting.value = false
        }
    }

    fun deleteRemoteGame(gameId: String) {
        viewModelScope.launch {
            _isdeleting.value = true
            val isDeleted = gamesUseCase.deleteGame(gameId)
            delay(1000)
            if (isDeleted) {
                _deletedGameMessage.value = "Juego borrado correctamente"
                loadGames()
            } else {
                _deletedGameMessage.value = "Error al borrar el juego"
            }
            _isdeleting.value = false
        }
    }

    fun deleteAllGames() {
        viewModelScope.launch {
            gamesUseCase.deleteAllGames()

        }
    }
}

//    fun updateGamesList(position: Int, games: Game) {
//        _games.value?.get(position)?.apply {
//            name = games.name
//        }
//    }
//

