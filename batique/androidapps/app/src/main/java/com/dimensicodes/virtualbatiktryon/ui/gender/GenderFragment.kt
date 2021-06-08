package com.dimensicodes.virtualbatiktryon.ui.gender

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.dimensicodes.virtualbatiktryon.R
import com.dimensicodes.virtualbatiktryon.databinding.FragmentGenderBinding
import com.dimensicodes.virtualbatiktryon.ui.pattern.PatternFragment
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

class GenderFragment : Fragment(), View.OnClickListener {

    private lateinit var genderFragmentBinding: FragmentGenderBinding
    lateinit var image: ImageView
    lateinit var imageData: Bitmap
    lateinit var clothesImage: Bitmap
    lateinit var normalMapImage: Bitmap
    private var imagePath: String? = null

    companion object {
        const val TAG = "GenderFragment"
        const val EXTRA_MESSAGE = "CAMERA_IMAGE"
        const val EXTRA_GENDER= "extra gender"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        genderFragmentBinding = FragmentGenderBinding.inflate(inflater, container, false)
        return genderFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imagePath = arguments?.getString(EXTRA_MESSAGE)
        Log.d(TAG, "onViewCreated argument: $imagePath")
        Picasso.with(context).load("file:///$imagePath").into(
            object : Target {
                override fun onBitmapFailed(errorDrawable: Drawable) {}
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
//                    if (bitmap != null) {
//                        imageData = bitmap.copy(Bitmap.Config.ARGB_8888, true);//image yang difoto
//                        clothesImage = bitmap.copy(Bitmap.Config.ARGB_8888, true)
//                        normalMapImage = bitmap.copy(Bitmap.Config.ARGB_8888, true)
//                        UploadImage(bitmapToFile(bitmap,"image.png"),imageData)
//                    }
                    image = view.rootView.findViewById(R.id.img_source)
                    image.setImageBitmap(bitmap)
                }
            }
        )
        genderFragmentBinding.btnFemale1.setOnClickListener(this)
        genderFragmentBinding.btnFemale2.setOnClickListener(this)
        genderFragmentBinding.btnMale1.setOnClickListener(this)
        genderFragmentBinding.btnMale2.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_female_1 -> nextFragment("Female Short")
            R.id.btn_female_2 -> nextFragment("Female Long")
            R.id.btn_male_1 -> nextFragment("Male Short")
            R.id.btn_male_2 -> nextFragment("Male Long")
        }
    }

    private fun nextFragment(code: String) {
        val mPatternFragment = PatternFragment()
        val mBundle = Bundle()
        mBundle.putString(EXTRA_MESSAGE, imagePath)
        mBundle.putString(EXTRA_GENDER, code)
        mPatternFragment.arguments = mBundle

        val mFragmentManager = fragmentManager
        mFragmentManager?.beginTransaction()?.apply {
            replace(R.id.frame_container, mPatternFragment, PatternFragment::class.java.simpleName)
            addToBackStack(null)
            commit()
        }
    }
}