package com.dedany.secretgift.presentation.main

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityMainBinding
import com.dedany.secretgift.domain.entities.User
import com.dedany.secretgift.presentation.fragments.MainFragment
import com.dedany.secretgift.presentation.fragments.ProfileFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var mainFragment: MainFragment
    private lateinit var profileFragment: ProfileFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAd()
        observeViewModel()

        if (savedInstanceState == null) {
            viewModel.loadUser()
            viewModel.loadGames()
        } else {
            restoreFragments() // Si la actividad se recrea, restauramos los fragmentos
        }

        setupBottomNavigation()
    }

    private fun observeViewModel() {
        viewModel.user.observe(this) { user ->
            if (user != null) {
                setupFragments(user)
            } else {
                Log.e("MainActivity", "El usuario es nulo")
            }
        }

    }

    private fun setupFragments(user: User) {
        // Verificamos si los fragmentos ya existen en el FragmentManager
        mainFragment = supportFragmentManager.findFragmentByTag("MainFragment") as? MainFragment
            ?: MainFragment().apply {
                arguments = Bundle().apply { putSerializable("user", user) }
            }

        profileFragment = supportFragmentManager.findFragmentByTag("ProfileFragment") as? ProfileFragment
            ?: ProfileFragment().apply {
                arguments = Bundle().apply { putSerializable("user", user) }
            }

        // Añadimos los fragmentos solo si no existen ya
        if (!mainFragment.isAdded) {
            supportFragmentManager.beginTransaction()
                .add(binding.fragmentContainer.id, mainFragment, "MainFragment")
                .commit()
        }

        if (!profileFragment.isAdded) {
            supportFragmentManager.beginTransaction()
                .add(binding.fragmentContainer.id, profileFragment, "ProfileFragment")
                .hide(profileFragment)
                .commit()
        }
    }

    private fun restoreFragments() {
        mainFragment = supportFragmentManager.findFragmentByTag("MainFragment") as? MainFragment
            ?: MainFragment()
        profileFragment = supportFragmentManager.findFragmentByTag("ProfileFragment") as? ProfileFragment
            ?: ProfileFragment()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    showFragment(mainFragment)
                    true
                }
                R.id.navigation_profile -> {
                    showFragment(profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .hide(mainFragment)
            .hide(profileFragment)
            .show(fragment)
            .commit()
    }

    private fun initAd() {
        MobileAds.initialize(this) { initialAd ->
            initialAd.adapterStatusMap.forEach { (adapter, status) ->
                Log.d("AdMob", "Adapter: $adapter Status: ${status.description}")
            }
        }

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }
}
