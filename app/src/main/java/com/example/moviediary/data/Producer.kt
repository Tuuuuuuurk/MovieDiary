package com.example.moviediary.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "producers", foreignKeys = [ForeignKey(entity = Film::class, parentColumns = ["id"], childColumns = ["film_id"], onDelete = CASCADE)])
@Parcelize
data class Producer (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val film_id: Int = 0
) : Parcelable {}