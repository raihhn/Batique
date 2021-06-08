package com.dimensicodes.virtualbatiktryon.data.source.local.remote

import androidx.lifecycle.LiveData
import com.dimensicodes.virtualbatiktryon.data.source.local.remote.response.BatikItem
import com.dimensicodes.virtualbatiktryon.data.source.local.remote.response.OriginItem

interface BatikJsonDataSource {
    fun getAllBatik():LiveData<ArrayList<BatikItem>>
    fun getAllOriginBatik():LiveData<ArrayList<OriginItem>>
}