package com.dedany.secretgift.presentation.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivitySplashBinding
import com.dedany.secretgift.presentation.login.LoginActivity
import com.dedany.secretgift.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)

        // Lanza la comprobación de estado de login de manera asíncrona
        lifecycleScope.launch {
            viewModel.checkLoginStatus()
        }

        // Después de 3 segundos, observa el estado de login y navega a la actividad correspondiente
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.nextActivity.observe(this) { activityClass ->
                val intent = Intent(this, activityClass)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()  // Finaliza la SplashActivity
            }
        }, 3000)  // 3 segundos de retraso
    }
}

