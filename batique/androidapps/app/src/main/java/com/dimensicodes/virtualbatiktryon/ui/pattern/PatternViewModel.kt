package com.dimensicodes.virtualbatiktryon.ui.pattern

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dimensicodes.virtualbatiktryon.data.source.BatikRepository
import com.dimensicodes.virtualbatiktryon.data.source.local.remote.response.BatikItem
import com.dimensicodes.virtualbatiktryon.data.source.local.remote.response.OriginItem

class PatternViewModel (private val batikRepository: BatikRepository) : ViewModel(){
    fun getBatik(): LiveData<ArrayList<BatikItem>> = batikRepository.getAllBatik()
    fun getOrigin(): LiveData<ArrayList<OriginItem>> = batikRepository.getAllOriginBatik()
}