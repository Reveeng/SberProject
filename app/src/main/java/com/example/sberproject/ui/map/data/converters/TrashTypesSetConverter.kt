package com.example.sberproject.ui.map.data.converters

import androidx.room.TypeConverter
import com.example.sberproject.TrashType

class TrashTypesSetConverter {
    @TypeConverter
    fun fromTrashTypesSet(trashTypesSet: Set<TrashType>): String {
        return trashTypesSet.map { it.toString() }.reduce { a, b -> "$a,$b" }
    }

    @TypeConverter
    fun toTrashTypesSet(data: String): Set<TrashType> {
        return data.split(',').map { TrashType.fromString(it) }.toSet()
    }
}