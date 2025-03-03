package com.dedany.secretgift.presentation.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityMainBinding
import com.dedany.secretgift.domain.entities.RegisteredUser
import com.dedany.secretgift.presentation.fragments.MainFragment
import com.dedany.secretgift.presentation.fragments.ProfileFragment
import com.dedany.secretgift.presentation.helpers.getCustomSerializable
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var user: RegisteredUser  // Variable para almacenar al usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el usuario del Intent
        user = intent.getCustomSerializable<RegisteredUser>("user") as? RegisteredUser ?: run {
            Log.e("MainActivity", "El usuario es nulo")
            return
        }



        if (savedInstanceState == null) {
            val mainFragment = MainFragment()
            val bundle = Bundle()
            bundle.putSerializable("user", user)  // Pasar el usuario al fragmento
            mainFragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, mainFragment)
                .commit()
        }

        // Configurar la navegación del BottomNavigationView
        binding.bottomNavigationMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val mainFragment = MainFragment()
                    val bundle = Bundle()
                    bundle.putSerializable("user", user)  // Pasar el usuario al fragmento
                    mainFragment.arguments = bundle

                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainer.id, mainFragment)
                        .commit()
                    true
                }
                R.id.navigation_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainer.id, ProfileFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}
