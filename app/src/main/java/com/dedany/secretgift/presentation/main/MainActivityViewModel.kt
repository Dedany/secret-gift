package com.dedany.secretgift.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.entities.GameSummary
import com.dedany.secretgift.domain.entities.LocalGame
import com.dedany.secretgift.domain.entities.User
import com.dedany.secretgift.domain.usecases.games.GamesUseCase
import com.dedany.secretgift.domain.usecases.users.UsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val gamesUseCase: GamesUseCase,
    private val usersUseCase: UsersUseCase
) : ViewModel() {

    private val _games: MutableLiveData<List<GameSummary>?> = MutableLiveData()
    val games: LiveData<List<GameSummary>?> = _games

    private val _localGames: MutableLiveData<List<LocalGame>?> = MutableLiveData()
    val localGames: LiveData<List<LocalGame>?> = _localGames

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User> = _user


    fun loadGames() {
        viewModelScope.launch {
            _games.value = gamesUseCase.getGamesByUser()
        }
    }

    fun loadLocalGames(){
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
    fun deleteLocalGame(game: LocalGame){
        viewModelScope.launch {
            gamesUseCase.deleteLocalGame(game)
            loadLocalGames()
        }
    }
}

//    fun updateGamesList(position: Int, games: Game) {
//        _games.value?.get(position)?.apply {
//            name = games.name
//        }
//    }
//
//    fun deleteLocalGame(game: CreateGame) {
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                gamesUseCase.deleteLocalGame(game)
//            }
//            //Actualiza la lista
//            val updatedGames = _games.value?.toMutableList()?.apply {
//                remove(game)
//            }
//            _games.value = updatedGames
//        }
//    }

