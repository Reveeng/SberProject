package com.example.sberproject.ui.articles

import retrofit2.http.*

interface ArticlesApi {
    @Headers("Content-Type: application/json")
    @POST("{page}")
    suspend fun getArticles(@Path("page") number: Int, @Body time: Time): List<Article>
}