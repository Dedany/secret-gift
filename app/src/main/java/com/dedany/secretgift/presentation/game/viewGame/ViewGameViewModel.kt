package com.dedany.secretgift.presentation.game.viewGame

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
class ViewGameViewModel @Inject constructor(
    private val useCase: GamesUseCase
): ViewModel() {

    private val _game: MutableLiveData<Game> = MutableLiveData<Game>()
    val game: LiveData<Game> = _game

    fun setGame(game: Game) {
        _game.value = game
    }

    fun fetchGaMeData(gameCode: String) {
            viewModelScope.launch {
                try {
                _game.value = useCase.getGame(gameCode)
                } catch (e: Exception) {
                    //TODO: handle error
                }
            }
    }
}