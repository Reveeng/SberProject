package com.example.sberproject.ui.articles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ArticlesViewModel(
    private val articlesApi: ArticlesApi
) : ViewModel() {
    private val mutableArticles by lazy {
        MutableLiveData<MutableList<Article>>()
    }
    val articles: LiveData<MutableList<Article>> = mutableArticles

    private var page = 0
    private val time by lazy {
        Time(
            SimpleDateFormat(
                "yyyy-MM-dd hh:mm:ss",
                Locale.getDefault()
            ).format(Calendar.getInstance().time)
        )
    }

    init {
        viewModelScope.launch {
            mutableArticles.value = mutableListOf()
            addArticles()
        }
    }

    fun loadMoreArticles() {
        viewModelScope.launch {
            addArticles()
        }
    }

    private suspend fun addArticles() {
        mutableArticles.value?.addAll(articlesApi.getArticles(page, time))
        page++
        mutableArticles.value = mutableArticles.value
    }
}