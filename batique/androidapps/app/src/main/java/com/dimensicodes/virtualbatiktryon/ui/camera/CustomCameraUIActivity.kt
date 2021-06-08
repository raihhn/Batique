package com.dimensicodes.virtualbatiktryon.ui.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dimensicodes.virtualbatiktryon.MainActivity
import com.dimensicodes.virtualbatiktryon.R
import com.dimensicodes.virtualbatiktryon.camera.core.Camera2
import com.dimensicodes.virtualbatiktryon.camera.core.Converters
//import com.kumastudio.cameraapp.ui.chooseclothes
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_custom_camera_ui.*

class CustomCameraUIActivity : AppCompatActivity() {
    companion object{
        const val TAG = "CustomCameraUIActivity"
        const val EXTRA_MESSAGE = "CAMERA_IMAGE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_camera_ui)
        init()
    }
    private lateinit var camera2: Camera2
    private var disposable: Disposable? = null

    private fun init() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )

            initCamera2Api()
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 3)
            else initCamera2Api()

        }

    }
    fun makeArray() : List<Int>{
        val newlist = mutableListOf<Int>()
        for(i in 0..500){
            newlist.add(i)
        }
        return newlist
    }
    private fun initCamera2Api() {

        camera2 = Camera2(this, camera_view)

        iv_rotate_camera.setOnClickListener {
            camera2.switchCamera()
        }

        iv_capture_image.setOnClickListener { v ->
            camera2.takePhoto {
                Toast.makeText(v.context, "Saving Picture", Toast.LENGTH_SHORT).show()
                val arrayX = listOf<Int>()
                val arrayY = listOf<Int>()
                disposable = Converters.convertBitmapToFile(it,arrayX,arrayY) { file ->
                    Toast.makeText(v.context, "Saved Picture Path ${file.path}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,MainActivity::class.java).apply {
                        putExtra(EXTRA_MESSAGE, file.path)
                    }
                    startActivity(intent)
                }
            }
        }

        iv_camera_flash_on.setOnClickListener {
            camera2.setFlash(Camera2.FLASH.ON)
            it.alpha = 1f
            iv_camera_flash_auto.alpha = 0.4f
            iv_camera_flash_off.alpha = 0.4f
        }


        iv_camera_flash_auto.setOnClickListener {
            iv_camera_flash_off.alpha = 0.4f
            iv_camera_flash_on.alpha = 0.4f
            it.alpha = 1f
            camera2.setFlash(Camera2.FLASH.AUTO)
        }

        iv_camera_flash_off.setOnClickListener {
            camera2.setFlash(Camera2.FLASH.OFF)
            it.alpha = 1f
            iv_camera_flash_on.alpha = 0.4f
            iv_camera_flash_auto.alpha = 0.4f

        }

    }



    override fun onPause() {
        //  cameraPreview.pauseCamera()
        camera2.close()
        super.onPause()
    }

    override fun onResume() {
        // cameraPreview.resumeCamera()
        camera2.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        if (disposable != null)
            disposable!!.dispose()
        super.onDestroy()
    }
}