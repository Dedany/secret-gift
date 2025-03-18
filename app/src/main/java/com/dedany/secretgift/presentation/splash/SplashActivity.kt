package com.dedany.secretgift.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivitySplashBinding
import com.dedany.secretgift.presentation.login.LoginActivity
import com.dedany.secretgift.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private var binding: ActivitySplashBinding? = null
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Llamamos a la función de verificación de estado de login
        viewModel.checkLoginStatus()

        // Observamos el estado de 'isLoggedIn' para saber si redirigir al MainActivity o LoginActivity
        viewModel.isLoggedIn.observe(this) { isLoggedIn ->
            Log.d("SplashActivity", "🟢 Valor de isLoggedIn: $isLoggedIn")
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = if (isLoggedIn) {
                    // Si está logueado, redirigimos a MainActivity
                    Intent(this, MainActivity::class.java)
                } else {
                    // Si no está logueado, redirigimos a LoginActivity
                    Intent(this, LoginActivity::class.java)
                }
                Log.d("SplashActivity", "⏩ Navegando a: ${intent.component?.className}")
                startActivity(intent)
                finish() // Terminamos la SplashActivity para que no regrese
            }, 1000) // Retraso de 1 segundo antes de redirigir
        }
    }
}