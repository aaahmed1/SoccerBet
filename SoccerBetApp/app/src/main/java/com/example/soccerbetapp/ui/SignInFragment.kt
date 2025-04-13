package com.example.soccerbetapp.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.soccerbetapp.MainViewModel
import com.example.soccerbetapp.R
import com.example.soccerbetapp.databinding.FragmentSignInBinding

class SignInFragment: Fragment(R.layout.fragment_sign_in) {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSignInBinding.bind(view)
        binding.loginBut.setOnClickListener {
            viewModel.authUser.login()
        }
    }
}