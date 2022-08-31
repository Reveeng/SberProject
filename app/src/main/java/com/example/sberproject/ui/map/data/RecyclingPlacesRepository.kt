package com.example.sberproject.ui.map.data

import android.util.Log
import com.example.sberproject.RecyclingPlace
import com.example.sberproject.TrashType
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class RecyclingPlacesRepository(
    private val recyclingPlacesDao: RecyclingPlacesDao
) {
    suspend fun getThem(city: String) = flow {
        val citiesWithRecyclingPlaces = recyclingPlacesDao.getCitiesWithRecyclingPlaces()
        val requiredCity = citiesWithRecyclingPlaces.firstOrNull { it.city.name == city }
        val recyclingPlacesFromFirebase = mutableListOf<RecyclingPlace>()
        val cityDocument = Firebase.firestore.collection("cities").document(city).get().await()
        val lastFirebaseUpdate = (cityDocument["last_update"] as Timestamp).toDate()
        requiredCity?.city?.lastUpdate?.let {
            if (it.after(lastFirebaseUpdate))
                emit(requiredCity.recyclingPlaces.map { x ->
                    RecyclingPlace(
                        x.name,
                        x.information,
                        x.coordinates,
                        x.trashTypes
                    )
                })
        }
        requiredCity?.let {
            recyclingPlacesDao.deleteCityWithRecyclingPlaces(it)
        }
        val placesCollection =
            Firebase.firestore.collection("cities").document(city).collection("places").get()
                .await()
        placesCollection.documents.forEach { doc ->
            val comment = doc["comment"] as String
            val name = doc["name"] as String
            val position = doc["position"] as GeoPoint
            val types = doc["types"] as ArrayList<Int>
            val recyclingPlace = RecyclingPlace(
                name,
                comment,
                LatLng(position.latitude, position.longitude),
                types.map { x -> TrashType.fromInt(x) }.toSet()
            )
            recyclingPlacesFromFirebase.add(recyclingPlace)
        }
        recyclingPlacesDao.insert(
            City(city, lastFirebaseUpdate),
            recyclingPlacesFromFirebase.map { x ->
                RecyclingPlaceEntity(
                    x.name,
                    x.information,
                    x.coordinates,
                    x.trashTypes,
                    city
                )
            })
        emit(recyclingPlacesFromFirebase)
    }
}