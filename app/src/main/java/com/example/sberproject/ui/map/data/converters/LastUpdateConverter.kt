package com.example.sberproject.ui.map.data.converters

import androidx.room.TypeConverter
import com.example.sberproject.ui.map.data.LastUpdate

class LastUpdateConverter {
    @TypeConverter
    fun fromLastUpdate(lastUpdate: LastUpdate): String = lastUpdate.lastUpdate

    @TypeConverter
    fun toLastUpdate(data: String): LastUpdate = LastUpdate(data)
}