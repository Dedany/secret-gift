package com.dedany.secretgift.presentation.game.createGame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameSettingsViewModel : ViewModel() {

    private val _eventDate = MutableLiveData<String>()
    val eventDate: LiveData<String> = _eventDate

    private val _numPlayers = MutableLiveData<String>()
    val numPlayers: LiveData<String> = _numPlayers

    private val _maxPrice = MutableLiveData<String>()
    val maxPrice: LiveData<String> = _maxPrice

    private val _incompatibilities = MutableLiveData<String>()
    val incompatibilities: LiveData<String> = _incompatibilities

    fun setEventDate(date: String) {
        _eventDate.value = date
    }

    fun setNumPlayers(players: String) {
        _numPlayers.value = players
    }

    fun setMaxPrice(price: String) {
        _maxPrice.value = price
    }

    fun setIncompatibilities(incompatibilities: String) {
        _incompatibilities.value = incompatibilities
    }
}
