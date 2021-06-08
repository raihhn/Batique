package com.dimensicodes.virtualbatiktryon.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.dimensicodes.virtualbatiktryon.ui.detail.DetailFragment
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ExportImageHelper(private val context: Context) {
    companion object {
        private const val TAG = "ExportHelper"
    }

    private fun getScreenView(view: View): Bitmap? {
        var screenView: Bitmap? = null
        try {
            screenView = Bitmap.createBitmap(
                view.measuredWidth,
                view.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(screenView)
            view.draw(canvas)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "getScreenView: Gagal Screecshot karena : ${e.message}")
        }
        return screenView
    }

    fun saveToStorage(view: View) {
        val getView = getScreenView(view)
        val fileName = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val image = File(imageDirectory, fileName)
            fos = FileOutputStream(image)
        }
        fos.use {
            getView?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(context, "Disimpan di Galeri", Toast.LENGTH_SHORT).show()
        }
    }
}