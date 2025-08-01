package com.example.soccerbetapp.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.soccerbetapp.MainViewModel
import com.example.soccerbetapp.R
import com.example.soccerbetapp.databinding.FragmentGamesBinding

class MyGamesFragment: Fragment(R.layout.fragment_games) {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentGamesBinding.bind(view)
        val rv = binding.recyclerView
        val gamesRowAdapter = GamesRowAdapter(viewModel, findNavController(), 1)
        rv.adapter = gamesRowAdapter
        rv.layoutManager = LinearLayoutManager(activity)
        viewModel.observeMyGames().observe(viewLifecycleOwner) {
            gamesRowAdapter.submitList(it)
        }
        val dividerItemDecoration = DividerItemDecoration(
            rv.context, LinearLayoutManager.VERTICAL
        )
        rv.addItemDecoration(dividerItemDecoration)
        viewModel.fetchMyGames()
    }
}