package com.example.sberproject.ui.map

import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sberproject.RecyclingPlace
import com.example.sberproject.TrashType
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class MapsViewModel(
    private val recyclingPlacesList: List<RecyclingPlace>
//    private val recyclingPlacesApi: RecyclingPlacesApi
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
//        viewModelScope.launch {
//            mutableRecyclingPlaces.value = recyclingPlacesApi.getRecyclingPlaces()
//        }
        mutableRecyclingPlaces.value = recyclingPlacesList
    }

    fun setSourceAndDestination(source: LatLng, destination: LatLng) {
        mutableRouteToNearbyRecyclingPlace.value = source to destination
    }

    fun findNearbyRecyclingPlaceFromStart(source: LatLng) {
        val nearbyRecyclingPlace = getNearbyRecyclingPlace(source)
        setSourceAndDestination(source, nearbyRecyclingPlace.coordinates)
    }

    fun setTrashType(trashType: TrashType) {
        mutableRecyclingPlaces.value =
            recyclingPlacesList.filter { it.trashTypes.contains(trashType) }
    }

    private val trashTypes by lazy {
        mutableSetOf<TrashType>()
    }

    fun addTrashType(trashType: TrashType) {
        trashTypes.add(trashType)
        mutableRecyclingPlaces.value = recyclingPlacesList.filter {
            it.trashTypes.intersect(
                trashTypes
            ).isNotEmpty()
        }
    }

    fun removeTrashType(trashType: TrashType) {
        trashTypes.remove(trashType)
        if (trashTypes.isEmpty())
            mutableRecyclingPlaces.value = recyclingPlacesList
        else
            mutableRecyclingPlaces.value = recyclingPlacesList.filter {
                it.trashTypes.intersect(
                    trashTypes
                ).isNotEmpty()
            }
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