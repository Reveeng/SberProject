package com.example.sberproject.ui.articles

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Article(
    val url: String,
    @SerializedName("header") val title: String,
    @SerializedName("image url") val imageUrl: String
) : Serializable