package com.example.moviediary.data

import android.graphics.Bitmap
import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "films")
@Parcelize
data class Film(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        val name: String = "",
        val genre: String = "",
        val year_of_issue: Long = System.currentTimeMillis(),
        val poster: Bitmap?,
        val status: String = "Буду смотреть",
        val rating: Int? = null,
) : Parcelable {
    val yearOfIssueFormatted: String
        @RequiresApi(Build.VERSION_CODES.O) get() = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(year_of_issue))
    val yearOfIssueFormattedAsYear: String
        @RequiresApi(Build.VERSION_CODES.O) get() = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date(year_of_issue))
    val yearOfIssueFormattedAsMonth: String
        @RequiresApi(Build.VERSION_CODES.O) get() = SimpleDateFormat("MM", Locale.getDefault()).format(Date(year_of_issue))
    val yearOfIssueFormattedAsDay: String
        @RequiresApi(Build.VERSION_CODES.O) get() = SimpleDateFormat("dd", Locale.getDefault()).format(Date(year_of_issue))
}