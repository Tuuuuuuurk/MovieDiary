package com.example.moviediary.ui.filmslist

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.moviediary.data.*
import com.example.moviediary.ui.ADD_FILM_RESULT_OK
import com.example.moviediary.ui.EDIT_FILM_RESULT_OK
import com.example.moviediary.ui.addeditfilm.AddEditFilmViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class FilmsListViewModel @ViewModelInject constructor(
    private val filmDao: FilmDao,
    private val producerDao: ProducerDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val searchQuery = state.getLiveData("searchQuery", "")

    private val preferencesFlow = preferencesManager.preferencesFlow

    private val filmsListEventChannel = Channel<FilmsListEvent>()
    val filmsListEvent = filmsListEventChannel.receiveAsFlow()

    private val filmsList = combine(
        searchQuery.asFlow(),
        preferencesFlow
    ) { sQ, pF ->
        Pair(sQ, pF)
    }.flatMapLatest { (sQ, pF) ->
        filmDao.getFilmsList(sQ, pF.sortOrder)
    }

    val allList = combine(
        filmsList,
        producerDao.getProducersList()
    ){ films, producers ->
        Pair(films, producers)
    }.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onFilmSelected(film: Film, producers: Array<Producer>) = viewModelScope.launch {
        filmsListEventChannel.send(FilmsListEvent.NavigateToEditFilmScreen(film, producers))
    }

    fun onFilmItemSwiped(film: Film) = viewModelScope.launch {
        filmDao.delete(film)
        filmsListEventChannel.send(FilmsListEvent.ShowUndoDeleteNoteMessage(film))
    }

    fun onUndoDeleteClick(film: Film) = viewModelScope.launch  {
        filmDao.insert(film)
    }

    fun onAddNewFilmClick() = viewModelScope.launch {
        filmsListEventChannel.send(FilmsListEvent.NavigateToAddFilmScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_FILM_RESULT_OK -> showFilmSavedConfirmationMessage("Фильм успешно добавлен")
            EDIT_FILM_RESULT_OK -> showFilmSavedConfirmationMessage("Фильм успешно обновлен")
        }
    }

    private fun showFilmSavedConfirmationMessage(text: String) = viewModelScope.launch {
        filmsListEventChannel.send(FilmsListEvent.ShowFilmSavedConfirmationMessage(text))
    }

    sealed class FilmsListEvent {
        object NavigateToAddFilmScreen : FilmsListEvent()
        data class ShowUndoDeleteNoteMessage(val film: Film) : FilmsListEvent()
        data class NavigateToEditFilmScreen(val film: Film, val producers: Array<Producer>) : FilmsListEvent()
        data class ShowFilmSavedConfirmationMessage(val msg: String) : FilmsListEvent()
    }
}