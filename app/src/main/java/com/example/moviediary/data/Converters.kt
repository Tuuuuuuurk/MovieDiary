package com.example.moviediary.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.room.TypeConverter
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import java.io.ByteArrayOutputStream

class Converters {

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray?): Bitmap? {
        return if (byteArray != null)
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        else
            null
    }

}