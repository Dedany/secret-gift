package com.dedany.secretgift.presentation.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dedany.secretgift.domain.entities.Game

class DetailsMainViewModel : ViewModel() {
    private val _game = MutableLiveData<Game>()
    val game: LiveData<Game> = _game

    private val _position = MutableLiveData<Int>()
    val position: LiveData<Int> = _position

    fun setGameValue(game: Game) {
        _game.value = game
    }

    fun setPositionValue(position: Int) {
        _position.value = position
    }



}