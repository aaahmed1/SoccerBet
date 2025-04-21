package com.example.soccerbetapp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.soccerbetapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        navController = findNavController(R.id.main_frame)
        binding.navGames.setOnClickListener {
            showNavBar()
            viewModel.removeBetListener()
            navController.navigate(R.id.gamesFragment)
        }
        binding.navProfile.setOnClickListener {
            showNavBar()
            viewModel.removeBetListener()
            navController.navigate(R.id.profileFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        //Log.d("dbTEST", viewModel.)
        viewModel.authUser = AuthUser(activityResultRegistry)
        lifecycle.addObserver(viewModel.authUser)
        viewModel.authUser.observeUser().observe(this) {
            viewModel.currentUser = it
            if (viewModel.currentUser.isInvalid()) {
                hideNavBar()
                navController.navigate(R.id.signInFragment)
            }
            else {
                showNavBar()
                viewModel.updateDBUser()
                navController.navigate(R.id.profileFragment)
            }
        }
    }

    private fun hideNavBar() {
        binding.bottomNavBar.visibility = View.GONE
    }

    private fun showNavBar() {
        binding.bottomNavBar.visibility = View.VISIBLE
    }
}