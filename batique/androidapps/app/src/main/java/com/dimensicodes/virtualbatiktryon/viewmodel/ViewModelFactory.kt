package com.dimensicodes.virtualbatiktryon.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dimensicodes.virtualbatiktryon.data.source.BatikRepository
import com.dimensicodes.virtualbatiktryon.di.InjectionJson
import com.dimensicodes.virtualbatiktryon.ui.pattern.PatternViewModel

class ViewModelFactory private constructor(val mBatikRepository: BatikRepository) : ViewModelProvider.NewInstanceFactory(){
    companion object {
        @Volatile
        private var instance : ViewModelFactory? = null

        fun getInstance(context: Context):ViewModelFactory=
            instance ?: synchronized(this){
                instance?: ViewModelFactory(InjectionJson.providerRepository(context)).apply { instance =this }
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when{
            modelClass.isAssignableFrom(PatternViewModel::class.java)->{
                return PatternViewModel(mBatikRepository) as T
            }
            else -> throw Throwable("Unknown ViewModel class:"+modelClass.name)
        }
    }
}