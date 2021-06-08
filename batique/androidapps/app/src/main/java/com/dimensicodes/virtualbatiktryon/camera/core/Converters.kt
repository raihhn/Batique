package com.dimensicodes.virtualbatiktryon.camera.core


import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object Converters {


    // This subscription needs to be disposed off to release the system resources primarily held for purpose.

    @JvmStatic   // this annotation is required for caller class written in Java to recognize this method as static
    fun convertBitmapToFile(bitmap: Bitmap,arrayX:List<Int>,arrayY:List<Int>, onBitmapConverted: (File) -> Unit): Disposable {
        return Single.fromCallable {
            compressBitmap(bitmap,arrayX,arrayY)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    Log.i("convertedPicturePath", it.path)
                    onBitmapConverted(it)
                }
            }, { it.printStackTrace() })
    }
    private fun compressBitmap(bitmap: Bitmap,arrayPosx:List<Int> = listOf(),arrayPosy:List<Int> = listOf()): File? {
        //create a file to write bitmap data
        try {
            val myStuff =
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "Android Custom Camera"
                )
            if (!myStuff.exists())
                myStuff.mkdirs()
            val namepitcure = "Mobin-" + System.currentTimeMillis() + ".jpeg"
            val picture = File(myStuff, namepitcure)

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            for (y in arrayPosy){
                for (x in arrayPosx){
                    bitmap.setPixel(x,y,bitmap.getPixel(0,1))
                }
            }

            bitmap.compress(Bitmap.CompressFormat.PNG, 100 /*ignored for PNG*/, bos)
            var bitmapData = bos.toByteArray()
            //write the bytes in file
            val fos = FileOutputStream(picture)
            fos.write(bitmapData)
            fos.flush()
            fos.close()
            return picture
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }


}
