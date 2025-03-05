package com.dedany.secretgift.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.usecases.games.GamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(private val gamesUseCase: GamesUseCase) :
    ViewModel() {

    private val _games: MutableLiveData<List<Game>?> = MutableLiveData()
    val games: LiveData<List<Game>?> = _games

    fun loadGames() {
        viewModelScope.launch {
            _games.value = gamesUseCase.getGamesByUser()
        }
    }

    fun updateGamesList(position: Int, games: Game) {
        _games.value?.get(position)?.apply {
            name = games.name
        }
    }

    fun deleteGame(game: Game) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                gamesUseCase.deleteGame(game)
            }
            //Actualiza la lista
            val updatedGames = _games.value?.toMutableList()?.apply {
                remove(game)
            }
            _games.value = updatedGames
        }
    }
}
