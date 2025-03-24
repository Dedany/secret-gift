package com.dedany.secretgift.presentation.splash

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
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
    private lateinit var connectivityManager: ConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
        setContentView(binding.root)

        connectivityManager()
        checkConnectivity()
        monitorConnectivityChanges()
        initObservers()
        viewModel.checkLoginStatus()
    }

    fun initObservers() {
        viewModel.isLoggedIn.observe(this) { hasSession ->
            val activityClass =
                if (hasSession) MainActivity::class.java else LoginActivity::class.java
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, activityClass)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()  // Finaliza la SplashActivity
            }, 3000)
        }
    }
    private fun connectivityManager() {
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private fun checkConnectivity() {
        val isConnected = isNetworkAvailable()
        if (isConnected) {
            Log.d("Connectivity", "Conectado a Internet")
            Toast.makeText(this, "Conectado a Internet", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("Connectivity", "Sin conexión a Internet")
            Toast.makeText(this, "Sin conexión a Internet", Toast.LENGTH_SHORT).show()
        }
    }

    //comprueba si hay conexión a internet
    private fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    //monitorea los cambios de conexión a internet
    private fun monitorConnectivityChanges() {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Log.d("Connectivity", "Conexión restaurada")
                Toast.makeText(applicationContext, "Conexión restaurada", Toast.LENGTH_SHORT).show()
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                Log.d("Connectivity", "Conexión perdida")
                Toast.makeText(applicationContext, "Conexión perdida", Toast.LENGTH_SHORT).show()
            }
        }
        // Registra el callback para los cambios de conexión
        connectivityManager.registerDefaultNetworkCallback(callback)
    }
}

