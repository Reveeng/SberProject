package com.example.sberproject.ui.map.data

import com.example.sberproject.RecyclingPlace
import java.text.SimpleDateFormat
import java.util.*

class RecyclingPlacesRepository(
    private val recyclingPlacesApi: RecyclingPlacesApi,
    private val recyclingPlacesDao: RecyclingPlacesDao
) {
    suspend fun getRecyclingPlaces(city: String): List<RecyclingPlace> {
        val citiesWithRecyclingPlaces = recyclingPlacesDao.getCitiesWithRecyclingPlaces()
        val requiredCity = citiesWithRecyclingPlaces.firstOrNull { it.city.name == city }
        val now = SimpleDateFormat(
            "yyyy-MM-dd hh:mm:ss",
            Locale.getDefault()
        ).format(Calendar.getInstance().time)
        requiredCity?.let { cityWithRecyclingPlace ->
            val updates =
                recyclingPlacesApi.getUpdates(city, cityWithRecyclingPlace.city.lastUpdate)
            if (updates.isEmpty()) {
                return cityWithRecyclingPlace.recyclingPlaces.map {
                    RecyclingPlace(it.name, it.information, it.coordinates, it.trashTypes)
                }
            }
            val lastUpdate = LastUpdate(now)
            recyclingPlacesDao.deleteCityWithRecyclingPlaces(cityWithRecyclingPlace)
            recyclingPlacesDao.insert(City(city, lastUpdate), updates.map {
                RecyclingPlaceEntity(
                    it.name,
                    it.information,
                    it.coordinates,
                    it.trashTypes,
                    city
                )
            })
            return updates
        }
        val recyclingPlacesFromServer = recyclingPlacesApi.getRecyclingPlaces(city)
        recyclingPlacesDao.insert(City(city, LastUpdate(now)), recyclingPlacesFromServer.map {
            RecyclingPlaceEntity(
                it.name,
                it.information,
                it.coordinates,
                it.trashTypes,
                city
            )
        })
        return recyclingPlacesFromServer
    }
}