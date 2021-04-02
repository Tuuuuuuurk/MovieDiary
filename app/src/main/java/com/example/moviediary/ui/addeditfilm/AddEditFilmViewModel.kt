package com.example.moviediary.ui.addeditfilm

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.moviediary.data.Film
import com.example.moviediary.data.FilmDao
import com.example.moviediary.data.Producer
import com.example.moviediary.data.ProducerDao
import com.example.moviediary.ui.ADD_FILM_RESULT_OK
import com.example.moviediary.ui.EDIT_FILM_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.abs
import java.util.Timer as Timer1

class AddEditFilmViewModel @ViewModelInject constructor(
        private val filmDao: FilmDao,
        private val producerDao: ProducerDao,
        @Assisted private val state: SavedStateHandle,
        private val application: Application
) : ViewModel() {

    val film = state.get<Film>("film")
    var producers = state.get<Array<Producer>>("producers")

    var filmProducers = getFilmProducersNames()

    var bitmapPic: Bitmap? = null


    var dateLong: Long = film?.year_of_issue ?: 0
    var dateYear: Int = film?.yearOfIssueFormattedAsYear?.toInt() ?: 2021
    var dateMonth: Int = film?.yearOfIssueFormattedAsMonth?.toInt() ?: 1
    var dateDay: Int = film?.yearOfIssueFormattedAsDay?.toInt() ?: 1

    var filmName = state.get<String>("filmName") ?: film?.name ?: ""
        set(value) {
            field = value
            state.set("filmName", value)
        }
    var filmGenre = state.get<String>("filmGenre") ?: film?.genre ?: ""
        set(value) {
            field = value
            state.set("filmGenre", value)
        }
    var filmDate = state.get<String>("filmDate") ?: film?.yearOfIssueFormatted ?: ""
        set(value) {
            field = value
            state.set("filmDate", value)
        }
    var filmPoster = state.get<Bitmap>("filmPoster") ?: film?.poster ?: bitmapPic
        set(value) {
            field = value
            state.set("filmPoster", value)
        }
    var filmStatus = state.get<String>("filmStatus") ?: film?.status ?: "Буду смотреть"
        set(value) {
            field = value
            state.set("filmStatus", value)
        }
    var filmRating = state.get<Int>("filmRating") ?: film?.rating ?: 0
        set(value) {
            field = value
            state.set("filmRating", value)
        }

    private val addEditFilmEventChannel = Channel<AddEditFilmEvent>()
    val addEditFilmEvent = addEditFilmEventChannel.receiveAsFlow()

    private fun getFilmProducersNames() : MutableList<String>{
        val names : MutableList<String> = arrayListOf()
        producers?.forEach {
            names.add(it.name)
        }
        return names
    }

    private fun getBitmapNullPicture(): Bitmap? {
        var bitmap: Bitmap? = null
        viewModelScope.launch {
            bitmap = getBitmap("", application.baseContext)
        }
        return bitmap
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onAddFilmClick() {
        if (filmName.isBlank() || filmGenre.isBlank()) {
            showInvalidInputMessage("Поле не может быть пустым")
            return
        }

        if (film != null) {
            val updatedFilm = film.copy(
                    name = filmName,
                    genre = filmGenre,
                    year_of_issue = dateLong,
                    poster = filmPoster,
                    status = filmStatus,
                    rating = filmRating.toString().toInt())

            updatedFilm(updatedFilm)

            producers?.forEach {
                deleteProducer(it)
            }
            producers = arrayOf()
            filmProducers.forEach {
                val newProducer = Producer(name = it, film_id = updatedFilm.id)
                producers = producers!!.plus(newProducer)
                createProducer(newProducer)
            }
            state.set("producers", producers)
        }
        else
        {
            val newFilm = Film(
                    name = filmName,
                    genre = filmGenre,
                    year_of_issue = dateLong,
                    poster = filmPoster,
                    status = filmStatus,
                    rating = filmRating.toString().toInt())
            newFilm.id = abs(newFilm.hashCode())

            createFilm(newFilm)

            producers = arrayOf()
            filmProducers.forEach {
                val newProducer = Producer(name = it, film_id = newFilm.id)
                producers = producers!!.plus(newProducer)
                createProducer(newProducer)
            }
        }
    }

    fun onAddProducerClick() = viewModelScope.launch {
        filmProducers.add("")
        addEditFilmEventChannel.send(AddEditFilmEvent.UpdateProducersListView)
    }

    fun onDateChanged() = viewModelScope.launch {
        addEditFilmEventChannel.send(AddEditFilmEvent.ChangeDateView)
    }

    private fun createProducer(producer: Producer) = viewModelScope.launch {
        producerDao.insert(producer)
    }

    private fun deleteProducer(producer: Producer) = viewModelScope.launch {
        producerDao.delete(producer)
    }

    private fun createFilm(newFilm: Film) = viewModelScope.launch {
        filmDao.insert(newFilm)
        addEditFilmEventChannel.send(AddEditFilmEvent.NavigateBackWithResult(ADD_FILM_RESULT_OK))
    }

    private fun updatedFilm(updatedFilm: Film) = viewModelScope.launch {
        filmDao.update(updatedFilm)
        addEditFilmEventChannel.send(AddEditFilmEvent.NavigateBackWithResult(EDIT_FILM_RESULT_OK))
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addEditFilmEventChannel.send(AddEditFilmEvent.ShowInvalidInputMessage(text))
    }

    sealed class AddEditFilmEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditFilmEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditFilmEvent()
        object ChangeDateView : AddEditFilmEvent()
        object UpdateProducersListView : AddEditFilmEvent()
    }

    suspend fun getBitmap(url: String, context: Context): Bitmap {
        val loading = ImageLoader(context)
        val request = ImageRequest.Builder(context)
                .data(url)
                .build()

        val result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }


}