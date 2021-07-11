package com.example.sberproject.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sberproject.RecyclingPlace
import com.google.android.gms.maps.model.LatLng

class MapsViewModel(
    recyclingPlacesList: List<RecyclingPlace>
) : ViewModel() {
    private val mutableCurrentLocation by lazy {
        MutableLiveData<LatLng>()
    }
    val currentLocation: LiveData<LatLng> = mutableCurrentLocation
    private val mutableRecyclingPlaces by lazy {
        MutableLiveData<List<RecyclingPlace>>()
    }
    val recyclingPlaces: LiveData<List<RecyclingPlace>> = mutableRecyclingPlaces

    init {
        mutableRecyclingPlaces.value = recyclingPlacesList
    }

    fun setCurrentLocation(coordinates: LatLng) {
        mutableCurrentLocation.value = coordinates
    }
}