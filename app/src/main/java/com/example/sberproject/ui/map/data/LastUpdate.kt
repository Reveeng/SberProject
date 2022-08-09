package com.example.sberproject.ui.map.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LastUpdate(@SerializedName("last update") val lastUpdate: String) : Serializable