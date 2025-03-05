package com.dedany.secretgift.presentation.game.createGame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateGameViewModel @Inject constructor(

) : ViewModel() {

    private var _isGameNameValid: MutableLiveData<Boolean> = MutableLiveData()
    val isGameNameValid: LiveData<Boolean> = _isGameNameValid

    private var gameName: String = ""

    fun nameGameIsValid() {
        _isGameNameValid.value = gameName.isNotEmpty() && gameName.length > 3
    }

    fun setName(name: String) {
        gameName = name
    }
}