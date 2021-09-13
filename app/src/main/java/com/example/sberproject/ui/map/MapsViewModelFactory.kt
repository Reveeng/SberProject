package com.example.sberproject.ui.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sberproject.RetrofitClient
import com.example.sberproject.ui.map.data.RecyclingPlacesDatabase
import com.example.sberproject.ui.map.data.RecyclingPlacesRepository

class MapsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val repository = RecyclingPlacesRepository(
            RetrofitClient.RECYCLING_PLACES_SERVICE,
            RecyclingPlacesDatabase.getDatabase(context).recyclingPlacesDao()
        )
        @Suppress("UNCHECKED_CAST")
        return MapsViewModel(repository) as T
    }
}