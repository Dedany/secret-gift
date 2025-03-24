package com.dedany.secretgift.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.FragmentProfileBinding
import com.dedany.secretgift.domain.entities.User
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var binding: FragmentProfileBinding? = null
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var user: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        user = arguments?.getSerializable("user") as? User ?: return

        viewModel.setUserData(user)

        initObservers()
    }

    private fun initObservers() {
        viewModel.errorData.observe(viewLifecycleOwner, { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        })
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