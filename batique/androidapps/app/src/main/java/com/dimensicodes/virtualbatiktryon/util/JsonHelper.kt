package com.dimensicodes.virtualbatiktryon.util

import android.content.Context
import android.util.Log
import com.dimensicodes.virtualbatiktryon.data.source.local.remote.response.BatikItem
import com.dimensicodes.virtualbatiktryon.data.source.local.remote.response.OriginItem
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class JsonHelper(private val context: Context) {

    companion object{
        private const val TAG = "JsonHelper"
    }

    private fun parsingFileToString(fileName: String): String? {
        return try {
            val `is` = context.assets.open(fileName)
            val buffer = ByteArray(`is`.available())
            `is`.read(buffer)
            `is`.close()
            String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun loadBatik(): ArrayList<BatikItem> {
        val list = ArrayList<BatikItem>()
        try {
            val responseObject = JSONObject(parsingFileToString("batik.json").toString())
            val listArray = responseObject.getJSONArray("batik")
            for (i in 0 until listArray.length()){
                val batik = listArray.getJSONObject(i)
                //Log.d(TAG, "loadBatik: isi json $batik")

                val listOrigin= ArrayList<String>()

                val originResponse = batik.getJSONArray("origin")
                for (k in 0 until  originResponse.length()){
                    val origin = originResponse.getString(k)
                    listOrigin.add(origin)
                }
                val imageRoot = "file:///android_asset/"
                val batikResponse = BatikItem(
                    batik.getString("id"),
                    batik.getString("name"),
                    listOrigin,
                    imageRoot+batik.getString("imagePath")
                )
                list.add(batikResponse)
                //Log.d(TAG, "loadBatik:after json $list & ")
            }
        }catch (e : JSONException){
            e.printStackTrace()
        }
        //Log.d(TAG, "loadBatik: $list")
        return list
    }
    fun loadOrigin():ArrayList<OriginItem>{
        val list = ArrayList<OriginItem>()
        try {
            val responseObject = JSONObject(parsingFileToString("origin_batik.json").toString())
            val listArray = responseObject.getJSONArray("origin")
            for (i in 0 until listArray.length()){
                val origin = listArray.getJSONObject(i)
                val originResponse = OriginItem(
                    origin.getString("id"),
                    origin.getString("name")
                )
                list.add(originResponse)
            }
        }catch (e:JSONException){
            e.printStackTrace()
        }
        return list
    }
}