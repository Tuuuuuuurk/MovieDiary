package com.example.moviediary.ui.statistic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moviediary.data.GenreStatistic
import com.example.moviediary.databinding.StatisticItemFragmentBinding

class GenresStatisticListAdapter
    : ListAdapter<GenreStatistic, GenresStatisticListAdapter.GenresViewHolder>(DiffCallback()) {

    private var genresList: List<GenreStatistic> = ArrayList()

    fun setStatistic(genres: List<GenreStatistic>) {
        genresList = genres
        submitList(genresList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenresViewHolder {
        val binding = StatisticItemFragmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GenresViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenresViewHolder, position: Int) {
        holder.bind(genresList[position])
    }

    override fun getItemCount() = genresList.size

    inner class GenresViewHolder(private val binding: StatisticItemFragmentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(genre: GenreStatistic) = with(itemView) {
            binding.apply {
                statisticNameView.text = genre.genre
                statisticRatingView.text = genre.rating.toString()
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<GenreStatistic>() {
        override fun areItemsTheSame(oldItem: GenreStatistic, newItem: GenreStatistic) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: GenreStatistic, newItem: GenreStatistic) =
            oldItem == newItem
    }
}