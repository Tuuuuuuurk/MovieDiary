package com.example.moviediary.data

import androidx.room.ColumnInfo

data class GenreStatistic (
    @ColumnInfo(name = "genre")
    var genre: String? = null,

    @ColumnInfo(name = "rating")
    var rating: Float? = null
)