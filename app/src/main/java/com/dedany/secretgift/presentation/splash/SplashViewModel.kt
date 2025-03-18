package com.dedany.secretgift.presentation.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dedany.secretgift.domain.usecases.auth.AuthUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
):ViewModel(
) {

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn


    //verifica si está logueado
   /*fun checkLoginStatus() {
        val loggedInStatus = authUseCase.login()
        _isLoggedIn.value = loggedInStatus
    }*/


fun checkLoginStatus() {
    val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    Log.d("SplashViewModel", "checkLoginStatus ejecutado, usuario: $currentUser")
    if (currentUser != null) {
        // Si el usuario está autenticado, obtenemos el token
        currentUser.getIdToken(true)  // true para forzar la actualización del token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result?.token
                    Log.d("SplashViewModel", "Token obtenido: $idToken")
                    _isLoggedIn.value = idToken != null
                } else {
                    Log.d("SplashViewModel", "Error al obtener el token: ${task.exception?.message}")
                    // Error al obtener el token
                    _isLoggedIn.value = false
                }
            }
    } else {
        Log.d("SplashViewModel", "No hay usuario autenticado")
        // El usuario no está autenticado
        _isLoggedIn.value = false
    }
}
}

