package com.example.sberproject.ui.map.data

import com.example.sberproject.RecyclingPlace
import retrofit2.http.*

interface RecyclingPlacesApi {
    @GET("{city}")
    suspend fun getRecyclingPlaces(@Path("city") city: String): List<RecyclingPlace>

    @Headers("Content-Type: application/json")
    @POST("{city}")
    suspend fun getUpdates(
        @Path("city") city: String,
        @Body lastUpdate: LastUpdate
    ): List<RecyclingPlace>
}

