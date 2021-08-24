package com.example.sberproject.ui.map

import com.example.sberproject.RecyclingPlace
import retrofit2.http.GET
import retrofit2.http.Path

interface RecyclingPlacesApi {
    @GET("{city}")
    suspend fun getRecyclingPlaces(@Path("city") city: String): List<RecyclingPlace>
}

