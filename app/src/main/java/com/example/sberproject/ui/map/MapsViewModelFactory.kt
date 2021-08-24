package com.example.sberproject.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sberproject.RetrofitClient

class MapsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MapsViewModel(RetrofitClient.RECYCLING_PLACES_SERVICE) as T
    }
}