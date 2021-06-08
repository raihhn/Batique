package com.dimensicodes.virtualbatiktryon.data.source.local.remote

import com.dimensicodes.virtualbatiktryon.data.source.local.remote.response.BatikItem
import com.dimensicodes.virtualbatiktryon.data.source.local.remote.response.OriginItem
import com.dimensicodes.virtualbatiktryon.util.JsonHelper
import java.util.ArrayList

class RemoteDataSourceJson private constructor(private val jsonHelper: JsonHelper){

    companion object{
        @Volatile
        private var instance : RemoteDataSourceJson? = null

        fun getInsance(helper: JsonHelper):RemoteDataSourceJson=
            instance ?: synchronized(this){
                instance?: RemoteDataSourceJson(helper).apply { instance=this }
            }
    }

    fun getAllBatik(callback: LoadBatikCallback) {
        callback.onAllBatikReceived( jsonHelper.loadBatik())
    }

    fun getAllOriginBatik(callback: LoadOriginBatikCallback) {
        callback.onAllOriginBatikReceived(jsonHelper.loadOrigin())
    }

    interface LoadBatikCallback {
        fun onAllBatikReceived(loadBatik: ArrayList<BatikItem>)

    }

    interface LoadOriginBatikCallback {
        fun onAllOriginBatikReceived(loadOrigin: ArrayList<OriginItem>)

    }
}