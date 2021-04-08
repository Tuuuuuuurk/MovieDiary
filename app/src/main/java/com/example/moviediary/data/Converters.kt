package com.example.moviediary.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.math.sqrt

class Converters @Inject constructor() {

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

    fun resizeImage(inputImage: Bitmap): Bitmap {
        var imageBytes = fromBitmap(inputImage)
        val compressionRatio = sqrt(100000.0 / imageBytes.size)
        val bitmap = toBitmap(imageBytes)!!
        val resized = Bitmap.createScaledBitmap(bitmap, (bitmap.width * compressionRatio).toInt(), (bitmap.height * compressionRatio).toInt(), true)
        imageBytes = fromBitmap(resized)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

}