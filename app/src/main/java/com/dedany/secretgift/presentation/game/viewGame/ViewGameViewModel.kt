package com.dedany.secretgift.presentation.game.viewGame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dedany.secretgift.data.dataSources.errorHandler.ErrorDto
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.domain.usecases.games.GamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewGameViewModel @Inject constructor(
    private val useCase: GamesUseCase
) : ViewModel() {

    private val _game: MutableLiveData<Game> = MutableLiveData<Game>()
    val game: LiveData<Game> = _game

    private val _gameCodeError: MutableLiveData<String> = MutableLiveData<String>()
    val gameCodeError: LiveData<String> = _gameCodeError

    private val _isMailSent: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val isMailSent: LiveData<Boolean> = _isMailSent

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _formattedMaxCost = MutableLiveData<String>()
    val formattedMaxCost: LiveData<String> get() = _formattedMaxCost


    private fun formatMaxCost(maxCost: Int): String {
        return if (maxCost == 0) "∞" else maxCost.toString()
    }

    fun sendMailToPlayer(playerId: String, playerEmail: String) {
        val gameId = game.value?.id

        if (!isValidEmail(playerEmail)) {
            _gameCodeError.value = "El email no es válido"
            return
        }

        viewModelScope.launch {
            try {
                _isMailSent.value =
                    useCase.sendMailToPlayer(gameId.orEmpty(), playerId, playerEmail)
            } catch (e: ErrorDto) {
                _gameCodeError.value = e.errorMessage
            }
        }

    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun fetchGaMeData(gameCode: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _game.value = useCase.getGame(gameCode)
                _formattedMaxCost.value = formatMaxCost(game.value?.maxCost ?: 0)
                _isLoading.value = false
            } catch (e: ErrorDto) {
                _gameCodeError.value = e.errorMessage
            }
        }
    }
}