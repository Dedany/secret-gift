package com.dedany.secretgift.presentation.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dedany.secretgift.domain.entities.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    private val _errorData = MutableLiveData<String>()
    val errorData: LiveData<String> = _errorData

    fun setUserData(user: User?) {
        if (user == null) {
            _errorData.value = "Error: El usuario no está disponible."
            return
        }
        if (user.name.isEmpty()) {
            _errorData.value = "Error: El nombre del usuario es inválido."
            return
        }

        if (user.email.isEmpty()) {
            _errorData.value = "Error: El correo electrónico del usuario es inválido."
            return
        }
        _userName.value = user.name
        _userEmail.value = user.email
        _userId.value = user.id
    }
}
