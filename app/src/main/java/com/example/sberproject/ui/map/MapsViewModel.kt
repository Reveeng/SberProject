package com.example.sberproject.ui.map

import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sberproject.RecyclingPlace
import com.example.sberproject.TrashType
import com.example.sberproject.Util
import com.example.sberproject.ui.map.data.RecyclingPlacesRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class MapsViewModel(
//    private val recyclingPlacesApi: RecyclingPlacesApi
    private val repository: RecyclingPlacesRepository
) : ViewModel() {
    private val mutableRecyclingPlaces by lazy {
        MutableLiveData<List<RecyclingPlace>>()
    }
    val recyclingPlaces: LiveData<List<RecyclingPlace>> = mutableRecyclingPlaces
    private val mutableRouteToRecyclingPlace by lazy {
        MutableLiveData<Pair<LatLng, LatLng>>()
    }
    val routeToRecyclingPlace: LiveData<Pair<LatLng, LatLng>> =
        mutableRouteToRecyclingPlace
    private val mutableRecyclingPlaceInfoToShow by lazy {
        MutableLiveData<RecyclingPlace>()
    }
    val recyclingPlaceToShow: LiveData<RecyclingPlace> = mutableRecyclingPlaceInfoToShow

    private val trashTypes by lazy {
        mutableSetOf<TrashType>()
    }

    private lateinit var recyclingPlacesList: List<RecyclingPlace>

    fun setSourceAndDestination(source: LatLng, destination: LatLng) {
        mutableRouteToRecyclingPlace.value = source to destination
    }

    fun findNearbyRecyclingPlaceFromStart(source: LatLng) {
        val nearbyRecyclingPlace = getNearbyRecyclingPlace(source)
        setSourceAndDestination(source, nearbyRecyclingPlace.coordinates)
        mutableRecyclingPlaceInfoToShow.value = nearbyRecyclingPlace
    }

    fun setTrashType(trashType: TrashType) {
        mutableRecyclingPlaces.value =
            recyclingPlacesList.filter { it.trashTypes.contains(trashType) }
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

    fun setCity(city: String) {
        Util.cityNames[city]?.let {
            viewModelScope.launch {
                mutableRecyclingPlaces.value = repository.getRecyclingPlaces(it)
            }
        }
    }
}