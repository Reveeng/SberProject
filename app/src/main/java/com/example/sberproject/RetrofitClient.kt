package com.example.sberproject

import com.example.sberproject.ui.articles.ArticlesApi
import com.example.sberproject.ui.map.data.RecyclingPlacesApi
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val RECYCLING_PLACES_SERVICE by lazy {
        val baseUrl = "http://158.101.217.50/city/"
        val gson = GsonBuilder()
            .setLenient()
            .registerTypeAdapter(RecyclingPlace::class.java, RecyclingPlaceDeserializer())
            .create()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(RecyclingPlacesApi::class.java)
    }

    val ARTICLES_SERVICE by lazy {
        val gson = GsonBuilder().setLenient().create()

        Retrofit.Builder()
            .baseUrl("http://158.101.217.50/news/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ArticlesApi::class.java)
    }
}