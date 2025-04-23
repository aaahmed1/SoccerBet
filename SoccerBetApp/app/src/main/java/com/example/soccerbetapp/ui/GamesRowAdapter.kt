package com.example.soccerbetapp.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.soccerbetapp.MainViewModel
import com.example.soccerbetapp.R
import com.example.soccerbetapp.api.GameData
import com.example.soccerbetapp.databinding.RowGamesBinding

class GamesRowAdapter(private val viewModel: MainViewModel, private val navController: NavController, private val select: Int)
    : ListAdapter<GameData, GamesRowAdapter.VH>(GameDiff()) {

        inner class VH(val rowGamesBinding: RowGamesBinding): RecyclerView.ViewHolder(rowGamesBinding.root) {
            init {
                rowGamesBinding.root.setOnClickListener {
                    var games = viewModel.observeNextGames()
                    if (select == 1) games = viewModel.observeMyGames()
                    val game = games.value!![bindingAdapterPosition]
                    viewModel.setCurGame(game)
                    viewModel.updateCurBet(game.fixture.id)
                    viewModel.updateUserBet(game.fixture.id)
                    navController.navigate(R.id.matchFragment)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowGamesBinding = RowGamesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(rowGamesBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val game = getItem(position)
        val binding = holder.rowGamesBinding
        binding.homeTeam.text = game.teams.home.name
        binding.awayTeam.text = game.teams.away.name
    }

    class GameDiff : DiffUtil.ItemCallback<GameData>() {
        override fun areItemsTheSame(oldItem: GameData, newItem: GameData): Boolean {
            return oldItem.fixture.id == newItem.fixture.id
        }

        override fun areContentsTheSame(oldItem: GameData, newItem: GameData): Boolean {
            return oldItem.teams.home.name == newItem.teams.home.name &&
                    oldItem.teams.away.name == newItem.teams.away.name
        }
    }
}