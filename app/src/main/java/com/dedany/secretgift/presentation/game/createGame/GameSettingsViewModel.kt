package com.dedany.secretgift.presentation.game.createGame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dedany.secretgift.domain.entities.Rule

class GameSettingsViewModel : ViewModel() {

    private val _eventDate = MutableLiveData<String>()
    val eventDate: LiveData<String> = _eventDate

    private val _numPlayers = MutableLiveData<String>()
    val numPlayers: LiveData<String> = _numPlayers

    private val _maxPrice = MutableLiveData<String>()
    val maxPrice: LiveData<String> = _maxPrice

    private val _incompatibilities = MutableLiveData<List<Pair<String, String>>>()
    val incompatibilities: LiveData<List<Pair<String, String>>> = _incompatibilities

    private val _rules = MutableLiveData<List<Rule>>(listOf())
    val rules: LiveData<List<Rule>> = _rules

    fun addNewRule() {
        val rules = _rules.value ?: emptyList()
        _rules.value = rules.plus(Rule("F1","F2"))
    }

    fun removeRuleAt(position: Int) {
        val rules = _rules.value?.toMutableList() ?: mutableListOf()
        rules.removeAt(position)
        _rules.value = rules
    }

    fun setEventDate(date: String) {
        _eventDate.value = date
    }

    fun setNumPlayers(players: String) {
        _numPlayers.value = players
    }

    fun setMaxPrice(price: String) {
        _maxPrice.value = price
    }

    fun setIncompatibilities(incompatibilities: List<Pair<String, String>>) {
        _incompatibilities.value = incompatibilities
    }

}
