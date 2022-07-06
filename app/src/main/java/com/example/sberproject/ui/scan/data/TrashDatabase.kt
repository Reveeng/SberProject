package com.example.sberproject.ui.scan.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Trash::class], version = 1, exportSchema = false)
abstract class TrashDatabase: RoomDatabase() {
    abstract fun trashDao(): TrashDao

    companion object{
        private var INSTANCE: TrashDatabase? = null

        fun getDatabase(context: Context): TrashDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrashDatabase::class.java,
                    "trash_database").build()
                INSTANCE = instance
                instance
            }
        }
    }
}