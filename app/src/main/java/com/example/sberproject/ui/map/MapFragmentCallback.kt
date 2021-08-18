package com.example.sberproject.ui.map

import com.example.sberproject.RecyclingPlace

interface MapFragmentCallback {
    fun clickOnBuildRoute(recyclingPlace: RecyclingPlace)
    fun clickOnCloseInfoSheet()
}