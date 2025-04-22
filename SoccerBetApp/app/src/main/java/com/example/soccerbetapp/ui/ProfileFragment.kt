package com.example.soccerbetapp.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.soccerbetapp.MainViewModel
import com.example.soccerbetapp.R
import com.example.soccerbetapp.databinding.FragmentProfileBinding

class ProfileFragment: Fragment(R.layout.fragment_profile) {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentProfileBinding.bind(view)
        binding.logoutBut.setOnClickListener {
            viewModel.removeUserListener()
            viewModel.authUser.logout()
        }
        binding.betsBut.setOnClickListener {
            findNavController().navigate(R.id.myGamesFragment)
        }
        viewModel.observeDBUser().observe(viewLifecycleOwner) {
            binding.totalPoints.text = "Total points: ${it?.total.toString()}"
        }
        binding.displayName.text = viewModel.currentUser.name
        binding.email.text = viewModel.currentUser.email
    }
}