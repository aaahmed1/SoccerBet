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
import com.google.android.material.snackbar.Snackbar
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeComponents

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
            if (it.fixture.status.short == "NS") {
                val dateTimeOffset = DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse(it.fixture.date)
                val date = dateTimeOffset.toLocalDate().toString()
                val time = dateTimeOffset.toLocalTime().toString()
                binding.gameStatus.text = "Game starts on ${date} at ${time} UTC"
            }
            else binding.gameStatus.text = it.fixture.status.long
            val timeElapsed = it.fixture.status.elapsed ?: 0
            binding.gameTime.text = "Minutes elapsed: ${timeElapsed}"
            val homeGoals = it.goals.home ?: 0
            val awayGoals = it.goals.away ?: 0
            binding.matchScore.text = "${homeGoals} - ${awayGoals}"
            val curBet = viewModel.observeCurBet().value
            Log.d("curBet", viewModel.observeCurBet().value.toString())
            if (it.fixture.status.short == "FT" && curBet != null && !curBet.finished) {
                Log.d("awardPoints", "Time to award points")
                viewModel.awardBet(it.fixture.id)
            }
        }
        viewModel.observeDBUser().observe(viewLifecycleOwner) {
            binding.yourPoints.text = "Points remaining: ${it.total}"
        }
        viewModel.observeCurBet().observe(viewLifecycleOwner) {
            binding.totalPointsHome.text = "Total points bet on home: ${it.homePoints}"
            binding.totalPointsDraw.text = "Total points bet on draw: ${it.drawPoints}"
            binding.totalPointsAway.text = "Total points bet on away: ${it.awayPoints}"
            val total = it.homePoints + it.drawPoints + it.awayPoints
            var mod1 = 1.0
            var mod2 = 1.0
            var mod3 = 1.0
            if (it.homePoints > 0) mod1 = total.toDouble() / it.homePoints
            if (it.drawPoints > 0) mod2 = total.toDouble() / it.drawPoints
            if (it.awayPoints > 0) mod3 = total.toDouble() / it.awayPoints
            binding.winHomeModifier.text = String.format("%.1f", mod1) + "x"
            binding.drawModifier.text = String.format("%.1f", mod2) + "x"
            binding.winAwayModifier.text = String.format("%.1f", mod3) + "x"
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
            else {
                Snackbar.make(it, "Can't make bet", Snackbar.LENGTH_SHORT).setAnchorView(R.id.bottom_nav_bar).show()
            }
        }
        binding.drawButton.setOnClickListener {
            val user = viewModel.observeDBUser().value!!
            val game = viewModel.observeCurGame().value!!
            val madeBet = user.bets.contains(game.fixture.id)
            val input = binding.drawBet.text
            val points = input.toString().toIntOrNull()
            if (!madeBet && !input.isNullOrEmpty() && points != null && points <= user.total) {
                Log.d("matchFrag", "hello")
                binding.drawBet.text.clear()
                viewModel.makeUserBet(points, 1)
            }
            else {
                Snackbar.make(it, "Can't make bet", Snackbar.LENGTH_SHORT).setAnchorView(R.id.bottom_nav_bar).show()
            }
        }
        binding.winAwayButton.setOnClickListener {
            val user = viewModel.observeDBUser().value!!
            val game = viewModel.observeCurGame().value!!
            val madeBet = user.bets.contains(game.fixture.id)
            val input = binding.winAwayBet.text
            val points = input.toString().toIntOrNull()
            if (!madeBet && !input.isNullOrEmpty() && points != null && points <= user.total) {
                binding.winAwayBet.text.clear()
                viewModel.makeUserBet(points, 2)
            }
            else {
                Snackbar.make(it, "Can't make bet", Snackbar.LENGTH_SHORT).setAnchorView(R.id.bottom_nav_bar).show()
            }
        }
        binding.reloadBut.setOnClickListener {
            viewModel.fetchCurGame()
        }
        viewModel.observeUserBet().observe(viewLifecycleOwner) {
            val result = it.result
            when (result) {
                0 -> binding.yourBet.text = "You bet ${it.points} points on the home team"
                1 -> binding.yourBet.text = "You bet ${it.points} points on a draw"
                else -> binding.yourBet.text = "You bet ${it.points} points on the away team"
            }
        }
    }
}