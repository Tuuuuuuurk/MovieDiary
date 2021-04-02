package com.example.moviediary.data

import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmDao {

    fun getFilmsList(searchQuery: String, sortOrder: SortOrder): Flow<List<Film>> =
            when(sortOrder) {
                SortOrder.BY_NAME -> getFilmsListByName(searchQuery)
                SortOrder.BY_YEAR -> getFilmsListByYear(searchQuery)
                SortOrder.BY_GENRE -> getFilmsListByGenre(searchQuery)
                SortOrder.BY_PRODUCER -> getFilmsListByProducer(searchQuery)
            }

    @Query("SELECT * FROM films WHERE name LIKE '%' || :searchQuery || '%'")
    fun getFilmsListByName(searchQuery: String): Flow<List<Film>>

    @Query("SELECT * FROM films WHERE name LIKE '%' || :searchQuery || '%' ORDER BY year_of_issue DESC")
    fun getFilmsListByYear(searchQuery: String): Flow<List<Film>>

    @Query("SELECT * FROM films WHERE genre LIKE '%' || :searchQuery || '%'")
    fun getFilmsListByGenre(searchQuery: String): Flow<List<Film>>

    @Query("SELECT * FROM films WHERE id IN (SELECT film_id FROM producers WHERE name LIKE '%' || :searchQuery || '%' )")
    fun getFilmsListByProducer(searchQuery: String): Flow<List<Film>>

    @Query("SELECT genre, AVG(rating) AS rating FROM films GROUP BY genre")
    fun getGenreStatistic(): List<GenreStatistic>

    @Query("SELECT pr.name AS producer, AVG(fl.rating) AS rating FROM films fl JOIN producers pr ON (fl.id=pr.film_id) GROUP BY pr.name")
    fun getProducerStatistic(): List<ProducerStatistic>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(film: Film)

    @Update
    suspend fun update(film: Film)

    @Delete
    suspend fun delete(film: Film)
}