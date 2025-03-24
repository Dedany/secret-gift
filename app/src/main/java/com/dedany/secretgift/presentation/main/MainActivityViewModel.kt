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

    private var _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> = _errorMessage


    fun loadPlayedGames() {
        viewModelScope.launch {
            try {
                val result = gamesUseCase.getPlayedGamesByUser()
                _playedGames.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar los juegos jugados: ${e.message}"
            }
        }
    }

    fun loadOwnedGames() {
        viewModelScope.launch {
            try {
                val result = gamesUseCase.getOwnedGamesByUser()
                _ownedGames.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar los juegos de propiedad: ${e.message}"
            }
        }
    }

    val combinedGames: LiveData<List<GameSummary>> = MediatorLiveData<List<GameSummary>>().apply {
        addSource(ownedGames) { ownedList ->
            val owned = ownedList.orEmpty()
            val played = playedGames.value.orEmpty()

            if (owned.isNotEmpty() || played.isNotEmpty()) {
                value = combineGames(owned, played)
            }
        }

        addSource(playedGames) { playedList ->
            val owned = ownedGames.value.orEmpty()
            val played = playedList.orEmpty()

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
            try {
                _localGames.value = gamesUseCase.getLocalGamesByUser()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar los juegos locales: ${e.message}"
            }
        }
    }

    fun loadUser() {
        viewModelScope.launch {
            try {
                val registeredUser = usersUseCase.getRegisteredUser()
                _user.value = registeredUser
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar el usuario: ${e.message}"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authUseCase.logout()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cerrar sesión: ${e.message}"
            }
        }
    }

    fun deleteLocalGame(gameId: Int) {
        viewModelScope.launch {
            _isdeleting.value = true
            try {
                val isDeleted = gamesUseCase.deleteLocalGame(gameId)
                delay(1000)
                if (isDeleted) {
                    _deletedGameMessage.value = "Juego borrado correctamente"
                    loadLocalGames()
                } else {
                    _deletedGameMessage.value = "Error al borrar el juego"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al borrar el juego local: ${e.message}"
            } finally {
                _isdeleting.value = false
            }
        }
    }

    fun deleteRemoteGame(gameId: String, userId: String) {
        viewModelScope.launch {
            _isdeleting.value = true
            try {
                val isDeleted = gamesUseCase.deleteGame(gameId, userId)
                delay(1000)
                if (isDeleted) {
                    _deletedGameMessage.value = "Juego borrado correctamente"
                    loadOwnedGames()
                } else {
                    _deletedGameMessage.value = "Error al borrar el juego"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al borrar el juego remoto: ${e.message}"
            } finally {
                _isdeleting.value = false
            }
        }
    }

    fun deleteAllGames() {
        viewModelScope.launch {
            try {
                gamesUseCase.deleteAllGames()
            } catch (e: Exception) {
                _errorMessage.value = "Error al borrar todos los juegos: ${e.message}"
            }
        }
    }
}



