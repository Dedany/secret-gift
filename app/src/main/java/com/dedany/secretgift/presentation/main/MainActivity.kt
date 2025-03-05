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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var user: RegisteredUser  // Variable para almacenar al usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAd()
        user = intent.getCustomSerializable<RegisteredUser>("user") as? RegisteredUser ?: run {
            Log.e("MainActivity", "El usuario es nulo")
            return
        }



        if (savedInstanceState == null) {
            val mainFragment = MainFragment()
            val profileFragment = ProfileFragment()
            val bundle = Bundle()
            bundle.putSerializable("user", user)  // Pasar el usuario al fragmento
            mainFragment.arguments = bundle
            profileFragment.arguments = bundle

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
                    val profileFragment = ProfileFragment()
                    val bundle = Bundle()
                    bundle.putSerializable("user", user)  // Pasar el usuario al fragmento
                    profileFragment.arguments = bundle

                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainer.id, profileFragment)
                        .commit()
                    true
                }

                else -> false
            }
        }

    }

    fun initAd() {
        //iniciar adMob
        MobileAds.initialize(this) { initialAd ->
            val statusMap = initialAd.adapterStatusMap
            for ((adapter, satatus) in statusMap) {
                Log.d("AdMob", "Adapter: $adapter Status: ${satatus.description}")
            }
        }
        binding?.adView?.let { adView ->

            //asigna tamaño
            /*adView.setAdSize(adSize)

            // Configurar el ID
            adView.adUnitId =
                getString(R.string.admob_banner_id)*/

            // Carga el anuncio
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        } ?: run {
            Log.e("AdMob", "adView es nulo.no hay anuncio")
        }
    }
}
