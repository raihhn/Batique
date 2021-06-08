package com.dimensicodes.virtualbatiktryon.data.source

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dimensicodes.virtualbatiktryon.data.source.local.remote.BatikJsonDataSource
import com.dimensicodes.virtualbatiktryon.data.source.local.remote.RemoteDataSourceJson
import com.dimensicodes.virtualbatiktryon.data.source.local.remote.RemoteDataSourceJson.LoadOriginBatikCallback
import com.dimensicodes.virtualbatiktryon.data.source.local.remote.RemoteDataSourceJson.LoadBatikCallback
import com.dimensicodes.virtualbatiktryon.data.source.local.remote.response.BatikItem
import com.dimensicodes.virtualbatiktryon.data.source.local.remote.response.OriginItem

class BatikRepository private constructor(private val remoteDataSourceJson: RemoteDataSourceJson):BatikJsonDataSource{
    companion object{
        private const val TAG = " BatikRepository"
        @Volatile
        private var instance : BatikRepository? =null

        fun getInstance(remoteData:RemoteDataSourceJson):BatikRepository =
            instance ?: synchronized(this){
                instance ?: BatikRepository(remoteData).apply { instance =this }
            }
    }

    override fun getAllBatik(): LiveData<ArrayList<BatikItem>> {
        val batikResult = MutableLiveData<ArrayList<BatikItem>>()
        remoteDataSourceJson.getAllBatik(object : LoadBatikCallback{
            override fun onAllBatikReceived(loadBatik: ArrayList<BatikItem>) {
                val batikList = ArrayList<BatikItem>()
                for (response in loadBatik){
                    val batik = BatikItem(
                        response.id,
                        response.name,
                        response.origin,
                        response.imagePath
                    )
                    batikList.add(batik)
                }
                batikResult.postValue(batikList)
                Log.d(TAG, "onAllBatikReceived: $batikList")
            }

        })
        return batikResult
    }

    override fun getAllOriginBatik(): LiveData<ArrayList<OriginItem>> {
        val originBatikResult = MutableLiveData<ArrayList<OriginItem>>()
        remoteDataSourceJson.getAllOriginBatik(object :LoadOriginBatikCallback{
            override fun onAllOriginBatikReceived(loadOrigin: ArrayList<OriginItem>) {
                val originList = ArrayList<OriginItem>()
                for (response in loadOrigin){
                    val origin = OriginItem(
                        response.id,
                        response.name
                    )
                    originList.add(origin)
                }
                originBatikResult.postValue(originList)
            }
        })
        return originBatikResult
    }
}