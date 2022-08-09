package com.example.sberproject.ui.map.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [City::class, RecyclingPlaceEntity::class], version = 1, exportSchema = false)
abstract class RecyclingPlacesDatabase : RoomDatabase() {
    abstract fun recyclingPlacesDao(): RecyclingPlacesDao

    companion object {
        private var INSTANCE: RecyclingPlacesDatabase? = null

        fun getDatabase(context: Context): RecyclingPlacesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecyclingPlacesDatabase::class.java,
                    "recycling_places_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}