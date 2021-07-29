package com.example.sberproject.ui.map

import com.example.sberproject.RecyclingPlace
import retrofit2.http.GET

interface RecyclingPlacesApi {
    @GET()
    suspend fun getRecyclingPlaces(): List<RecyclingPlace>
}

