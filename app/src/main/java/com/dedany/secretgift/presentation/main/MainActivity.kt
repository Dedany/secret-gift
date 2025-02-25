package com.dedany.secretgift.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityMainBinding
import com.dedany.secretgift.presentation.fragments.MainFragment
import com.dedany.secretgift.presentation.fragments.ProfileFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Establecer el fragmento inicial
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, MainFragment())
                .commit()
        }

        // Configurar la navegación del BottomNavigationView
        binding.bottomNavigationMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainer.id, MainFragment())
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
