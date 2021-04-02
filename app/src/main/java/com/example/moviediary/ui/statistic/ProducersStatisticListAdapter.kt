package com.example.moviediary.ui.statistic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moviediary.data.ProducerStatistic
import com.example.moviediary.databinding.StatisticItemFragmentBinding

class ProducersStatisticListAdapter
    : ListAdapter<ProducerStatistic, ProducersStatisticListAdapter.ProducersViewHolder>(DiffCallback()) {

    private var producersList: List<ProducerStatistic> = ArrayList()

    fun setStatistic(producers: List<ProducerStatistic>) {
        producersList = producers
        submitList(producersList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProducersViewHolder {
        val binding = StatisticItemFragmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProducersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProducersViewHolder, position: Int) {
        holder.bind(producersList[position])
    }

    override fun getItemCount() = producersList.size

    inner class ProducersViewHolder(private val binding: StatisticItemFragmentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(producer: ProducerStatistic) = with(itemView) {
            binding.apply {
                statisticNameView.text = producer.producer
                statisticRatingView.text = producer.rating.toString()
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ProducerStatistic>() {
        override fun areItemsTheSame(oldItem: ProducerStatistic, newItem: ProducerStatistic) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: ProducerStatistic, newItem: ProducerStatistic) =
            oldItem == newItem
    }
}