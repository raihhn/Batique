package com.dimensicodes.virtualbatiktryon.ui.detail

import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.dimensicodes.virtualbatiktryon.R
import com.dimensicodes.virtualbatiktryon.databinding.FragmentDetailBinding
import com.dimensicodes.virtualbatiktryon.util.ExportImageHelper

class DetailFragment : Fragment() {

    private lateinit var detailFragmentBinding: FragmentDetailBinding
    private var imagePath : Bitmap? = null

    companion object {
        private const val TAG = "DetailFragment"
        const val EXTRA_MESSAGE = "CAMERA_IMAGE"
        const val EXTRA_GENDER= "extra gender"
        const val EXTRA_BATIK= "extra batik"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        detailFragmentBinding = FragmentDetailBinding.inflate(inflater, container, false)
        return detailFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        imagePath = arguments?.getParcelable(EXTRA_MESSAGE)
        val gender = arguments?.getString(EXTRA_GENDER)
        val batik = arguments?.getString(EXTRA_BATIK)

        view.rootView.findViewById<ImageView>(R.id.img_source).setImageBitmap(imagePath)
        detailFragmentBinding.tvPattern.text = batik
        detailFragmentBinding.tvStyleType.text = gender

        detailFragmentBinding.btnSave.setOnClickListener {
            detailFragmentBinding.btnSave.visibility = View.GONE
            val export = ExportImageHelper(context as Activity)
            export.saveToStorage(view.rootView.findViewById(R.id.mainView))
            detailFragmentBinding.btnSave.visibility = View.VISIBLE
        }

    }


}