package com.example.moviediary.data

import androidx.room.ColumnInfo

data class ProducerStatistic (
    @ColumnInfo(name = "producer")
    var producer: String? = null,

    @ColumnInfo(name = "rating")
    var rating: Float? = null
)