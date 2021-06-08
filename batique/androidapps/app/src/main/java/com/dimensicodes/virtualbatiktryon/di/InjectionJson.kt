package com.dimensicodes.virtualbatiktryon.di

import android.content.Context
import com.dimensicodes.virtualbatiktryon.data.source.BatikRepository
import com.dimensicodes.virtualbatiktryon.data.source.local.remote.BatikJsonDataSource
import com.dimensicodes.virtualbatiktryon.data.source.local.remote.RemoteDataSourceJson
import com.dimensicodes.virtualbatiktryon.util.JsonHelper

object InjectionJson {
    fun providerRepository(context: Context):BatikRepository{
        val remoteDataSourceJson = RemoteDataSourceJson.getInsance(JsonHelper(context))
        return BatikRepository.getInstance(remoteDataSourceJson)
    }
}