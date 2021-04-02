package com.example.moviediary.ui.addeditfilm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moviediary.databinding.ProducerItemFragmentBinding

class ProducersListAdapter
    : ListAdapter<String, ProducersListAdapter.ProducersViewHolder>(ProducersListAdapter.DiffCallback()) {

    private var producersList: MutableList<String> = ArrayList()

    fun setProducers(producers: MutableList<String>) {
        producersList = producers
        submitList(producersList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProducersViewHolder {
        val binding = ProducerItemFragmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProducersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProducersViewHolder, position: Int) {
        holder.bind(producersList[position])
    }

    override fun getItemCount() = producersList.size

    inner class ProducersViewHolder(private val binding: ProducerItemFragmentBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                this.addEditFilmProducerView.addTextChangedListener {
                    producersList[position] = it.toString()
                }
            }
        }

        fun bind(producer: String) = with(itemView) {
            binding.apply {
                addEditFilmProducerView.setText(producer)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) =
                oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String) =
                oldItem == newItem
    }
}