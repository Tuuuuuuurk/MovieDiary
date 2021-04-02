package com.example.moviediary.ui.statistic

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviediary.R
import com.example.moviediary.data.GenreStatistic
import com.example.moviediary.databinding.StatisticFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class StatisticFragment : Fragment(R.layout.statistic_fragment){

    private val statisticViewModel: StatisticViewModel by viewModels()

    private val genresStatisticListAdapter: GenresStatisticListAdapter = GenresStatisticListAdapter()

    private val producersStatisticListAdapter: ProducersStatisticListAdapter = ProducersStatisticListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = StatisticFragmentBinding.bind(view)

        binding.apply {
            statisticGenreListView.apply {
                adapter = genresStatisticListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            statisticProducerListView.apply {
                adapter = producersStatisticListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        genresStatisticListAdapter.setStatistic(statisticViewModel.genresList)
        producersStatisticListAdapter.setStatistic(statisticViewModel.producersList)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            statisticViewModel.statisticsEvent.collect { event ->
                when (event) {
                    is StatisticViewModel.StatisticsEvent.SetAdaptersByStatistic -> {
                        genresStatisticListAdapter.notifyDataSetChanged()
                        producersStatisticListAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

    }
}