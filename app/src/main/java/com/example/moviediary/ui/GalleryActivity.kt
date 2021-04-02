package com.example.moviediary.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import com.example.moviediary.R

class GalleryActivity : Activity() {

    private val REQUEST_CODE_GALLERY = 1

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        openGalleryForImage()

        val data = Intent()
        data.putExtra("bitmapUri", imageUri)
        setResult(RESULT_OK, data)
        finish()
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY)
                imageUri = data?.data
        }
    }
}