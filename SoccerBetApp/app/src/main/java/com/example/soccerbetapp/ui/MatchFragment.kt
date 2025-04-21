package com.example.soccerbetapp.ui

import android.os.Bundle
import android.util.Log
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
            binding.totalPointsDraw.text = "Total points bet on draw: ${it.drawPoints}"
            binding.totalPointsAway.text = "Total points bet on away: ${it.awayPoints}"
        }
        binding.winHomeButton.setOnClickListener {
            val user = viewModel.observeDBUser().value!!
            val game = viewModel.observeCurGame().value!!
            val madeBet = user.bets.contains(game.fixture.id)
            val input = binding.winHomeBet.text
            val points = input.toString().toIntOrNull()
            if (!madeBet && !input.isNullOrEmpty() && points != null && points <= user.total) {
                binding.winHomeBet.text.clear()
                viewModel.makeUserBet(points, 0)
            }
        }
        binding.drawButton.setOnClickListener {
            val user = viewModel.observeDBUser().value!!
            val game = viewModel.observeCurGame().value!!
            val madeBet = user.bets.contains(game.fixture.id)
            val input = binding.drawBet.text
            //Log.d("matchFrag", input.toString())
            val points = input.toString().toIntOrNull()
            //Log.d("matchFrag", (!madeBet).toString())
            //Log.d("matchFrag", input.isNullOrEmpty().toString())
            //Log.d("matchFrag", (points != null).toString())
            //Log.d("matchFrag", (points!! <= user.total).toString())
            if (!madeBet && !input.isNullOrEmpty() && points != null && points <= user.total) {
                Log.d("matchFrag", "hello")
                binding.winHomeBet.text.clear()
                viewModel.makeUserBet(points, 1)
            }
        }
        binding.winAwayButton.setOnClickListener {
            val user = viewModel.observeDBUser().value!!
            val game = viewModel.observeCurGame().value!!
            val madeBet = user.bets.contains(game.fixture.id)
            val input = binding.winAwayBet.text
            val points = input.toString().toIntOrNull()
            if (!madeBet && !input.isNullOrEmpty() && points != null && points <= user.total) {
                binding.winHomeBet.text.clear()
                viewModel.makeUserBet(points, 2)
            }
        }
    }
}