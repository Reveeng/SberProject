package com.example.sberproject.ui.map.data.converters

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng

class LatLngConverter {
    @TypeConverter
    fun fromLatLng(latLng: LatLng): String = "${latLng.latitude},${latLng.longitude}"

    @TypeConverter
    fun toLatLng(data: String): LatLng {
        val latLng = data.split(',').map { it.toDouble() }
        return LatLng(latLng[0], latLng[1])
    }
}