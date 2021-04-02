package com.example.moviediary.ui.statistic

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.moviediary.data.FilmDao
import com.example.moviediary.data.GenreStatistic
import com.example.moviediary.data.ProducerDao
import com.example.moviediary.data.ProducerStatistic
import com.example.moviediary.ui.filmslist.FilmsListViewModel
import com.example.moviediary.util.flattenToList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class StatisticViewModel @ViewModelInject constructor(
    private val filmDao: FilmDao,
    private val producerDao: ProducerDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val genresList: List<GenreStatistic> = filmDao.getGenreStatistic()
    val producersList: List<ProducerStatistic> = filmDao.getProducerStatistic()

    fun setAllLists() = viewModelScope.launch {
        statisticsEventChannel.send(StatisticsEvent.SetAdaptersByStatistic)
    }

    private val statisticsEventChannel = Channel<StatisticsEvent>()
    val statisticsEvent = statisticsEventChannel.receiveAsFlow()

    sealed class StatisticsEvent {
        object SetAdaptersByStatistic: StatisticsEvent()
    }
}