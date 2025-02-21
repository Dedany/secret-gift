package com.dedany.secretgift.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.usecases.games.GamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(private val gamesUseCase: GamesUseCase): ViewModel(){
    private val _games: MutableLiveData<List<Game>> = MutableLiveData()
    val games: LiveData<List<Game>> = _games

    fun loadGames(){
        viewModelScope.launch {
            _games.value = gamesUseCase.getGames()
        }
    }
}
