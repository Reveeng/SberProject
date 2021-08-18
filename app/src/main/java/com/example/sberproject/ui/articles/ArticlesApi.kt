package com.example.sberproject.ui.articles

import com.example.sberproject.ui.articles.Article
import retrofit2.http.GET

interface ArticlesApi {
    @GET()
    suspend fun getArticles(): List<Article>
}