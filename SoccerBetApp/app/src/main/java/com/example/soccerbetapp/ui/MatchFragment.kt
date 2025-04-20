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
        viewModel.observeCurGame().observe(viewLifecycleOwner) {
            if (binding.homeTeam.drawable == null) {
                Glide.with(this).load(it.teams.home.logo).into(binding.homeTeam)
                Glide.with(this).load(it.teams.away.logo).into(binding.awayTeam)
            }
            binding.gameStatus.text = it.fixture.status.long
            val homeGoals = it.goals.home ?: 0
            val awayGoals = it.goals.away ?: 0
            binding.matchScore.text = "${homeGoals} - ${awayGoals}"
        }
        viewModel.observeDBUser().observe(viewLifecycleOwner) {
            binding.yourPoints.text = "Points remaining: ${it.total}"
        }
        viewModel.observeCurBet().observe(viewLifecycleOwner) {
            binding.totalPointsHome.text = "Total points bet on home: ${it.homePoints}"
            binding.totalPointsDraw.text = "Total points bet on home: ${it.drawPoints}"
            binding.totalPointsAway.text = "Total points bet on home: ${it.awayPoints}"
        }
    }
}