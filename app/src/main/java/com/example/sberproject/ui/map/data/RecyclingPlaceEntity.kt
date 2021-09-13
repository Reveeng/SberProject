package com.example.sberproject.ui.map.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.sberproject.TrashType
import com.example.sberproject.ui.map.data.converters.LatLngConverter
import com.example.sberproject.ui.map.data.converters.TrashTypesSetConverter
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "recycling_place_table")
@TypeConverters(LatLngConverter::class, TrashTypesSetConverter::class)
data class RecyclingPlaceEntity(
    val name: String,
    val information: String,
    val coordinates: LatLng,
    val trashTypes: Set<TrashType>,
    val city: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)