package com.dedany.secretgift.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dedany.secretgift.domain.entities.Game
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

    private val _games: MutableLiveData<List<Game>?> = MutableLiveData()
    val games: LiveData<List<Game>?> = _games

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User> = _user

    fun loadGames() {
        viewModelScope.launch {
            _games.value = gamesUseCase.getGamesByUser()
        }
    }

    fun loadUser() {
        viewModelScope.launch {
            val registeredUser = usersUseCase.getRegisteredUser()
            _user.postValue(registeredUser)
    }
}}

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

