package com.example.sberproject.view.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sberproject.TrashType

class UndefinedTrashTypeViewModel: ViewModel() {
    private val mutableTrashType by lazy{
        MutableLiveData<TrashType>()
    }
    val trashType: LiveData<TrashType> = mutableTrashType

    fun setTrashType(trashType: TrashType){
        mutableTrashType.value = trashType
    }
}