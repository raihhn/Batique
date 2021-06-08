package com.dimensicodes.virtualbatiktryon.ui.pattern

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import coil.request.ImageRequest
import com.dimensicodes.virtualbatiktryon.R
import com.dimensicodes.virtualbatiktryon.data.dataupload.domainConnect
import com.dimensicodes.virtualbatiktryon.data.source.local.remote.response.BatikItem
import com.dimensicodes.virtualbatiktryon.data.source.local.remote.response.OriginItem
import com.dimensicodes.virtualbatiktryon.databinding.FragmentPatternBinding
import com.dimensicodes.virtualbatiktryon.ui.detail.DetailFragment
import com.dimensicodes.virtualbatiktryon.ui.gender.GenderFragment
import com.dimensicodes.virtualbatiktryon.viewmodel.ViewModelFactory
import com.dimensicodes.virtualbatiktryon.data.dataupload.UploadUtility
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import coil.imageLoader
import java.io.*

class PatternFragment : Fragment() {
    private lateinit var patternFragmentBinding : FragmentPatternBinding
    private lateinit var patternViewModel: PatternViewModel
    private val batikList = ArrayList<BatikItem>()
    private val originBatikList = ArrayList<OriginItem>()
    private val mOrigin = ArrayList<String>()
    private lateinit var rvAdapter : GridBatikAdapter
    private lateinit var image: ImageView
    private lateinit var imageData: Bitmap
    private lateinit var clothesImage: Bitmap
    private lateinit var normalMapImage : Bitmap
    private lateinit var batikImage : Bitmap
    var batikGetSelectedNow : String? = null
    private var imagePath: String? = null
    private val resultPhoto = MutableLiveData<Bitmap>()
    private var batikSelected = MutableLiveData<BatikItem>()
    private var batik = BatikItem()
    companion object{
        private const val TAG = "PatternFragment"
        const val EXTRA_MESSAGE = "CAMERA_IMAGE"
        const val EXTRA_GENDER= "extra gender"
        const val EXTRA_BATIK= "extra batik"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        patternFragmentBinding = FragmentPatternBinding.inflate(inflater,container,false)
        return patternFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        patternViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity()))[PatternViewModel::class.java]

        patternFragmentBinding.rvBatik.setHasFixedSize(true)
        patternViewModel.getOrigin().observe(viewLifecycleOwner,{origin->
            //Log.d(TAG, "onViewCreated: origin ->$origin")
            originBatikList.addAll(origin)
            for (i in origin.indices){
                mOrigin.add(origin[i].name!!)
            }
            spinnerBatikList()
        })

        imagePath = arguments?.getString(EXTRA_MESSAGE)
        Log.d(GenderFragment.TAG, "onViewCreated argument: $imagePath")
        Picasso.with(context).load("file:///$imagePath").into(
            object : Target {
                override fun onBitmapFailed(errorDrawable: Drawable) {}
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    bitmap?.let{
                        imageData = bitmap
                    }
                    image = view.rootView.findViewById(R.id.img_source)
                    image.setImageBitmap(bitmap)
                }
            }
        )
        batikSelected.observe(viewLifecycleOwner,{ data ->
            batik=data
        })

        patternFragmentBinding.btnProcess.setOnClickListener {
            Picasso.with(context).load(batikGetSelectedNow).into(
                object : Target {
                    override fun onBitmapFailed(errorDrawable: Drawable) {}
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        if (bitmap != null) {
                            imageData = bitmap.copy(Bitmap.Config.ARGB_8888, true)//image yang difoto
                            clothesImage = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                            normalMapImage = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                            batikImage = bitmap
                            uploadImage(bitmapToFile(imageData,"$imagePath"),imageData)
                        }
                    }
                }
            )
            val mDetailFragment = DetailFragment()
            lateinit var bitmap: Bitmap
            val mBundle = Bundle()
            var gender = arguments?.getString(EXTRA_GENDER)
            resultPhoto.observe(viewLifecycleOwner, { batik ->
                bitmap = batik
                mBundle.putParcelable(EXTRA_MESSAGE, bitmap)
            })
            val batikName = batik.name.toString()
            mBundle.putString(EXTRA_GENDER, gender)
            mBundle.putString(EXTRA_BATIK, batikName)
            mDetailFragment.arguments = mBundle
            val mFragmentManager = fragmentManager
            mFragmentManager?.beginTransaction()?.apply {
                replace(
                    R.id.frame_container,
                    mDetailFragment,
                    DetailFragment::class.java.simpleName
                )
                addToBackStack(null)
                commit()
            }
        }
    }

    private fun spinnerBatikList() {
        val adapter = ArrayAdapter(activity as Context, android.R.layout.simple_spinner_dropdown_item, mOrigin)
        patternFragmentBinding.searchBox.adapter = adapter
        patternFragmentBinding.searchBox.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                batikList.clear()
                patternViewModel.getBatik().observe(viewLifecycleOwner,{ batik ->
                    //Log.d(TAG, "onViewCreated: batik -> $batik")
                    for (i in batik.indices){
                        val batikOrigin = ArrayList<BatikItem>()
                        for (k in batik[i].origin!!){
                            if (k==originBatikList[position].id){
                                batikOrigin.add(batik[i])
                            }
                        }
                        //Log.d(TAG, "onItemSelected batik origin: $batikOrigin")
                            batikList.addAll(batikOrigin)
                            showRecyclerView()
                    }

                })
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    private fun showRecyclerView(){
        patternFragmentBinding.rvBatik.layoutManager = GridLayoutManager(context, 4)
        rvAdapter = GridBatikAdapter()
        rvAdapter.listBatik.addAll(batikList)
        patternFragmentBinding.rvBatik.adapter = rvAdapter
        rvAdapter.setOnItemClickCallBack(object : GridBatikAdapter.OnitemCLickCallback{
            override fun onItemClicked(data: BatikItem) {
                batikGetSelectedNow = data.imagePath!!
                batikSelected.postValue(data)
            }
        })
    }

    fun uploadImage(sourceFile: File?, realPhoto: Bitmap){
        sourceFile?.let{
            val runFunc = { filename:String ->
                processBatik(activity as Activity,filename.substringAfterLast('/'),realPhoto)
            }
            UploadUtility(activity as Activity).uploadFile(it, null, runFunc)
        }
    }
    fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? { // File name like "image.png"
        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = File(fileNameToSave)
            file.createNewFile()
            Log.d(TAG, "bitmapToFile file path: ${Environment.getExternalStorageDirectory().toString() + File.separator + fileNameToSave}")
            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }
    private fun Bitmap.getBatikMasking(clothes:Bitmap, normal:Bitmap, batik:Bitmap, backgroundColor:Int = Color.WHITE):Bitmap?{
        Log.d(TAG, "real photo start")
        val bitmap = copy(config,true)
        Log.d(TAG, "real photo $width $height")
        Log.d(TAG,"cloth photo "+clothes.width+" "+clothes.height)
        Log.d(TAG,"normals map photo"+normal.width+" "+normal.height)
        var alpha:Int
        var pixel:Int
        val newBatik = batik.resizeByWidth(width)
        Log.d(TAG,""+newBatik.width+" "+newBatik.height)
        Log.d(TAG,"batik painting start")
        // scan through all pixels
        for (x in 0 until width){
            for (y in 0 until height){
                pixel = clothes.getPixel(x,y)
                alpha = Color.alpha(pixel)
                if (alpha > 100){
                    val normalHsv = FloatArray(3)
                    Color.colorToHSV(normal.getPixel(x,y), normalHsv)
                    val kecerahanbatik = ((normalHsv[0]-120)/255)*normalHsv[2]
                    val batikHsv = FloatArray(3)
                    Color.colorToHSV(newBatik.getPixel(if(x<newBatik.width) x else (x%newBatik.width),if(y<newBatik.height) y else (y%newBatik.height)), batikHsv)
                    batikHsv[2] = kecerahanbatik
                    bitmap.setPixel(x,y, Color.HSVToColor(batikHsv))
                }
            }
        }
        Log.d(TAG,"image changed")
        return bitmap
    }
    private fun Bitmap.resizeByWidth(width:Int):Bitmap{
        val ratio:Float = this.width.toFloat() / this.height.toFloat()
        val height:Int = Math.round(width / ratio)
        val newWidth = Math.round(height / ratio)
        return Bitmap.createScaledBitmap(
            this,
            newWidth,
            height,
            false
        )
    }
    private fun Bitmap.resizeWidthHeight(width: Int,height: Int) : Bitmap{
        return Bitmap.createScaledBitmap(
            this,
            width,
            height,
            false
        )
    }

    private fun getImageBitmapUrl(url:String, func: (m: Drawable?) -> Unit){
        val request = ImageRequest.Builder(requireContext())
            .data(url)
            .target { drawable ->
                func(drawable)
            }
            .build()
        context?.imageLoader?.enqueue(request)

    }
    fun paintBatikImage(){
        Log.d(TAG,"genkan matt")
        val clothesImage = clothesImage.copy(Bitmap.Config.ARGB_8888,true)
        Log.d(TAG,"batik loaded")
        val normalImage = normalMapImage.copy(Bitmap.Config.ARGB_8888,true)
        val batikCopy = batikImage.copy(Bitmap.Config.ARGB_8888,true)
        val paintbatik = imageData.resizeWidthHeight(clothesImage.width,clothesImage.height).getBatikMasking(clothesImage,normalImage,batikCopy)
        image.setImageBitmap(paintbatik)
    }
    private fun processBatik(actIn: Activity, name:String, realPhoto: Bitmap){
        val batikImagePath = batik.imagePath
        val inputSystem  = context?.assets
        var inputStream : InputStream?= null
        try {
            inputStream = batikImagePath?.let { inputSystem?.open(it) }
        }catch (e: IOException){
            e.printStackTrace()
        }
        var cloth = BitmapFactory.decodeStream(inputStream)
        var normals = BitmapFactory.decodeStream(inputStream)
        val url = domainConnect.serverURL

        val toggleMessage = UploadUtility(actIn)
        val batikPhoto = BitmapFactory.decodeStream(inputStream)
        val preparePaintBatik = {
            toggleMessage.toggleProgressDialog(true,"paint batik")
            clothesImage = cloth
            normalMapImage = normals
            paintBatikImage()
            toggleMessage.toggleProgressDialog(false)
        }
        val getNormal = {
            Log.d(TAG,"get normal map start")
            UploadUtility(activity as Activity).getUrlfromWeb("normalmap/$name",{
                val urlstr = it
                Timer().schedule(2000) {
                    getImageBitmapUrl(urlstr,{
                        it?.let{
                            Log.d(TAG,"get normal map end")
                            normals = it.toBitmap()
                            resultPhoto.postValue(normals)
                            Log.d(TAG,normals.height.toString())
                            preparePaintBatik()
                        }
                    })
                }
            })
        }
        val rembg = {
            Log.d(TAG,"remove background start")
            UploadUtility(activity as Activity).getUrlfromWeb("rembg/$name",{
                val urlstr = it
                Timer().schedule(2000) {
                    getImageBitmapUrl(urlstr, {
                        it?.let {
                            cloth = it.toBitmap()
                            resultPhoto.postValue(cloth)
                            getNormal()
                        }
                    })
                }
            })
        }
        Log.d(TAG, "get clothes from getcloth/$name")
        val getclothes = {
            UploadUtility(activity as Activity).getUrlfromWeb("getcloth/$name",{
                val urlstr = it
                Timer().schedule(2000){
                    getImageBitmapUrl(urlstr,{
                        it?.let{
                            rembg()
                        }
                    })
                }
            })
        }
        getclothes()
    }

}