package com.example.sberproject.ui.articles

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ArticlesApi {
    @Headers("Content-Type: application/json")
    @POST("{page}")
    suspend fun getArticles(@Path("page") number: Int, @Body time: Time): List<Article>
}