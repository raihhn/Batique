package com.dimensicodes.virtualbatiktryon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dimensicodes.virtualbatiktryon.ui.gender.GenderFragment

class MainActivity : AppCompatActivity() {
    private lateinit var mGenderFragment: GenderFragment

    companion object{
        const val TAG = "MainActivity"
        const val EXTRA_MESSAGE = "CAMERA_IMAGE"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val imagePath = intent.getStringExtra(EXTRA_MESSAGE)

        Log.d(TAG, "onCreate: $imagePath")

        val mFragmentManager = supportFragmentManager
        mGenderFragment = GenderFragment()
        val mBundle =Bundle()
        mBundle.putString(GenderFragment.EXTRA_MESSAGE,imagePath)
        mGenderFragment.arguments = mBundle
        val fragment = mFragmentManager.findFragmentByTag(GenderFragment::class.java.simpleName)

        if (fragment !is GenderFragment){
            mFragmentManager
                .beginTransaction()
                .add(R.id.frame_container, mGenderFragment, GenderFragment::class.java.simpleName)
                .commit()
        }
    }
}