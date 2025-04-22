package com.example.soccerbetapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.soccerbetapp.MainViewModel
import com.example.soccerbetapp.databinding.RowUsersBinding
import com.example.soccerbetapp.model.DBUser

class UsersAdapter(private val viewModel: MainViewModel)
    : ListAdapter<DBUser, UsersAdapter.VH>(UserDiff()) {

        inner class VH(val rowUsersBinding: RowUsersBinding): RecyclerView.ViewHolder(rowUsersBinding.root) {

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowUsersBinding = RowUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(rowUsersBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val user = getItem(position)
        val binding = holder.rowUsersBinding
        binding.userName.text = user.name
        binding.userPoints.text = user.total.toString()
    }

    class UserDiff: DiffUtil.ItemCallback<DBUser>() {
        override fun areItemsTheSame(oldItem: DBUser, newItem: DBUser): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DBUser, newItem: DBUser): Boolean {
            return oldItem.name == newItem.name && oldItem.total == newItem.total
        }
    }
}