package com.example.sberproject.ui.map.data

import androidx.room.Embedded
import androidx.room.Relation

data class CityWithRecyclingPlaces(
    @Embedded val city: City,
    @Relation(
        parentColumn = "name",
        entity = RecyclingPlaceEntity::class,
        entityColumn = "city"
    ) val recyclingPlaces: List<RecyclingPlaceEntity>
)