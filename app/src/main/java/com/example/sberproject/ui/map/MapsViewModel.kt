package com.example.sberproject.ui.map

import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sberproject.RecyclingPlace
import com.example.sberproject.TrashType
import com.google.android.gms.maps.model.LatLng

class MapsViewModel(
    private val recyclingPlacesList: List<RecyclingPlace>
) : ViewModel() {
    private val mutableRecyclingPlaces by lazy {
        MutableLiveData<List<RecyclingPlace>>()
    }
    val recyclingPlaces: LiveData<List<RecyclingPlace>> = mutableRecyclingPlaces
    private val mutableRouteToNearbyRecyclingPlace by lazy {
        MutableLiveData<Pair<LatLng, LatLng>>()
    }
    val routeToNearbyRecyclingPlace: LiveData<Pair<LatLng, LatLng>> =
        mutableRouteToNearbyRecyclingPlace

    init {
        mutableRecyclingPlaces.value = recyclingPlacesList
    }

    fun findNearbyRecyclingPlaceFromStart(start: LatLng) {
        val nearbyRecyclingPlace = getNearbyRecyclingPlace(start)
        mutableRouteToNearbyRecyclingPlace.value = start to nearbyRecyclingPlace.coordinates
    }

    fun setTrashType(trashType: TrashType) {
        mutableRecyclingPlaces.value =
            recyclingPlacesList.filter { it.trashTypes.contains(trashType) }
    }

    private fun getNearbyRecyclingPlace(start: LatLng): RecyclingPlace {
        val startLocation = Location(LocationManager.GPS_PROVIDER).apply {
            latitude = start.latitude
            longitude = start.longitude
        }
        var min: Pair<RecyclingPlace, Float>? = null
        mutableRecyclingPlaces.value?.forEach {
            val dist = Location(LocationManager.GPS_PROVIDER).apply {
                latitude = it.coordinates.latitude
                longitude = it.coordinates.longitude
            }.distanceTo(startLocation)
            if (min == null)
                min = it to dist
            else min?.let { pair ->
                if (dist < pair.second)
                    min = it to dist
            }
        }
        min?.let {
            return it.first
        }
        throw Exception("There are no recycling places")
    }
}