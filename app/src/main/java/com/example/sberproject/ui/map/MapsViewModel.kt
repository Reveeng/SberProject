package com.example.sberproject.ui.map

import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sberproject.RecyclingPlace
import com.google.android.gms.maps.model.LatLng

class MapsViewModel(
    private val recyclingPlacesList: List<RecyclingPlace>
) : ViewModel() {
    private val mutableCurrentLocation by lazy {
        MutableLiveData<LatLng>()
    }
    val currentLocation: LiveData<LatLng> = mutableCurrentLocation
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

    fun setCurrentLocation(coordinates: LatLng) {
        mutableCurrentLocation.value = coordinates
        val nearbyRecyclingPlace = getNearbyRecyclingPlace(coordinates)
        mutableRouteToNearbyRecyclingPlace.value = coordinates to nearbyRecyclingPlace.coordinates
    }

    private fun getNearbyRecyclingPlace(start: LatLng): RecyclingPlace {
        val startLocation = Location(LocationManager.GPS_PROVIDER).apply {
            latitude = start.latitude
            longitude = start.longitude
        }
        var min: Pair<RecyclingPlace, Float>? = null
        recyclingPlacesList.forEach {
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