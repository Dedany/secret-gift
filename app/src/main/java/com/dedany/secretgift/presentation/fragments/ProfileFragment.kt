package com.dedany.secretgift.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.FragmentProfileBinding
import com.dedany.secretgift.domain.entities.RegisteredUser
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var binding: FragmentProfileBinding? = null
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var user: RegisteredUser

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inicializamos el binding
        binding = FragmentProfileBinding.bind(view)

        // Obtenemos el usuario del Bundle
        user = arguments?.getSerializable("user") as? RegisteredUser ?: return

        // Configurarmos los datos del usuario en el ViewModel
        viewModel.setUserData(user)

        initObservers()
    }

    private fun initObservers() {
        viewModel.userName.observe(viewLifecycleOwner) { name ->
            binding?.textNameValue?.text = name
        }

        viewModel.userEmail.observe(viewLifecycleOwner) { email ->
            binding?.textEmailValue?.text = email
        }

        viewModel.userId.observe(viewLifecycleOwner) { id ->
            binding?.textIdValue?.text = id
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}