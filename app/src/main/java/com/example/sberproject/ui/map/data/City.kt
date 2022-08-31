package com.example.sberproject.ui.map.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.sberproject.ui.map.data.converters.DateConverter
import java.util.*

@Entity(tableName = "cities_table")
@TypeConverters(DateConverter::class)
data class City(
    @PrimaryKey val name: String,
    val lastUpdate: Date
)