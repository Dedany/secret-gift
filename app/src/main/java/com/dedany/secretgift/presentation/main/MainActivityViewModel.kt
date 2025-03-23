package com.dedany.secretgift.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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

    private val _ownedGames: MutableLiveData<List<GameSummary>?> = MutableLiveData()
    val ownedGames: LiveData<List<GameSummary>?> = _ownedGames

    private val _playedGames: MutableLiveData<List<GameSummary>?> = MutableLiveData()
    val playedGames: LiveData<List<GameSummary>?> = _playedGames


    private val _localGames: MutableLiveData<List<LocalGame>?> = MutableLiveData()
    val localGames: LiveData<List<LocalGame>?> = _localGames

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User> = _user

    private val _isdeleting: MutableLiveData<Boolean> = MutableLiveData()
    val isdeleting: LiveData<Boolean> = _isdeleting

    private var _deletedGameMessage: MutableLiveData<String> = MutableLiveData()
    val deletedGameMessage: LiveData<String> = _deletedGameMessage


    fun loadPlayedGames() {
        viewModelScope.launch {
            val result = gamesUseCase.getPlayedGamesByUser()
            Log.d("ViewModel", "PlayedGames cargados: ${result.size}")
            _playedGames.value = result
        }
    }

    fun loadOwnedGames() {
        viewModelScope.launch {
            val result = gamesUseCase.getOwnedGamesByUser()
            Log.d("ViewModel", "OwnedGames cargados: ${result.size}")
            _ownedGames.value = result
        }
    }
    val combinedGames: LiveData<List<GameSummary>> = MediatorLiveData<List<GameSummary>>().apply {
        addSource(ownedGames) { ownedList ->
            Log.d("ViewModel", "Combinando desde owned: ${ownedList?.size ?: 0} owned, ${playedGames.value?.size ?: 0} played")
            val owned = ownedList.orEmpty()
            val played = playedGames.value.orEmpty()

            // Verifica que no haya una lista vacía reemplazando los valores
            if (owned.isNotEmpty() || played.isNotEmpty()) {
                value = combineGames(owned, played)
            }
        }

        addSource(playedGames) { playedList ->
            Log.d("ViewModel", "Combinando desde played: ${ownedGames.value?.size ?: 0} owned, ${playedList?.size ?: 0} played")
            val owned = ownedGames.value.orEmpty()
            val played = playedList.orEmpty()

            // Verifica que no haya una lista vacía reemplazando los valores
            if (owned.isNotEmpty() || played.isNotEmpty()) {
                value = combineGames(owned, played)
            }
        }
    }

    private fun combineGames(owned: List<GameSummary>, played: List<GameSummary>): List<GameSummary> {
        // Marca los juegos apropiadamente
        val ownedMarked = owned.map { it.copy(isOwnedGame = true) }
        val playedMarked = played.map { it.copy(isOwnedGame = false) }

        // Combina y elimina duplicados priorizando owned
        return (ownedMarked + playedMarked).groupBy { it.id }
            .map { (_, games) -> games.firstOrNull { it.isOwnedGame } ?: games.first() }
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

    fun deleteRemoteGame(gameId: String, userId: String) {
        viewModelScope.launch {
            _isdeleting.value = true
            val isDeleted = gamesUseCase.deleteGame(gameId, userId)
            delay(1000)
            if (isDeleted) {
                _deletedGameMessage.value = "Juego borrado correctamente"
                loadOwnedGames()
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



