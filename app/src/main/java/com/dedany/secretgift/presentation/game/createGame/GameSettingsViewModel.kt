package com.dedany.secretgift.presentation.game.createGame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dedany.secretgift.domain.entities.Rule

class GameSettingsViewModel : ViewModel() {


    private val _rules = MutableLiveData<List<Rule>>(listOf())
    val rules: LiveData<List<Rule>> = _rules

    fun addNewRule() {
        val rules = _rules.value ?: emptyList()
        _rules.value = rules.plus(Rule("F1", "F2"))
    }

    fun removeRuleAt(position: Int) {
        val rules = _rules.value?.toMutableList() ?: mutableListOf()
        rules.removeAt(position)
        _rules.value = rules
    }
    fun setRules(newRules: List<Rule>) {
        _rules.value = newRules
    }

}
