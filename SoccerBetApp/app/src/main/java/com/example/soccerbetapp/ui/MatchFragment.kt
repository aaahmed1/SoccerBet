package com.example.soccerbetapp.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.soccerbetapp.MainViewModel
import com.example.soccerbetapp.R
import com.example.soccerbetapp.databinding.FragmentMatchBinding

class MatchFragment: Fragment(R.layout.fragment_match) {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMatchBinding.bind(view)
        val game = viewModel.curGame
        Glide.with(this).load(game.teams.home.logo).into(binding.homeTeam)
        Glide.with(this).load(game.teams.away.logo).into(binding.awayTeam)
        binding.gameStatus.text = game.fixture.status.long
        val homeGoals = game.goals.home ?: 0
        val awayGoals = game.goals.away ?: 0
        binding.matchScore.text = "${homeGoals} - ${awayGoals}"
    }
}