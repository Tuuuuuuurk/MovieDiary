package com.example.moviediary.data

import android.nfc.Tag
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProducerDao {

    @Query("SELECT * FROM producers WHERE film_id = :film_id")
    fun getProducersListByFilmId(film_id: Int): Flow<List<Producer>>

    @Query("SELECT * FROM producers")
    fun getProducersList(): Flow<List<Producer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(producer: Producer)

    @Update
    suspend fun update(producer: Producer)

    @Delete
    suspend fun delete(producer: Producer)
}