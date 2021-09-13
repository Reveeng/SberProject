package com.example.sberproject.ui.map.data

import androidx.room.*

@Dao
interface RecyclingPlacesDao {
    @Transaction
    @Query("SELECT * FROM cities_table")
    suspend fun getCitiesWithRecyclingPlaces(): List<CityWithRecyclingPlaces>

    @Insert
    suspend fun insertRecyclingPlace(recyclingPlaceEntity: RecyclingPlaceEntity)

    @Insert
    suspend fun insertCity(city: City)

    @Transaction
    suspend fun insert(city: City, recyclingPlaces: List<RecyclingPlaceEntity>) {
        insertCity(city)
        recyclingPlaces.forEach {
            insertRecyclingPlace(it)
        }
    }

    @Delete
    suspend fun deleteCity(city: City)

    @Delete
    suspend fun deleteRecyclingPlace(recyclingPlace: RecyclingPlaceEntity)

    @Transaction
    suspend fun deleteCityWithRecyclingPlaces(cityWithRecyclingPlaces: CityWithRecyclingPlaces) {
        deleteCity(cityWithRecyclingPlaces.city)
        cityWithRecyclingPlaces.recyclingPlaces.forEach { deleteRecyclingPlace(it) }
    }
}