package com.example.sberproject.ui.articles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ArticlesViewModel(
//    private val articlesApi: ArticlesApi
    private val articlesList: List<Article>
) : ViewModel() {
    private val mutableArticles by lazy {
        MutableLiveData<List<Article>>()
    }
    val articles: LiveData<List<Article>> = mutableArticles

    init {
//        viewModelScope.launch {
//            mutableArticles.value = articlesApi.getArticles()
//        }
        mutableArticles.value = articlesList
    }
}