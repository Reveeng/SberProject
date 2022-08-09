package com.example.sberproject.ui.map

import com.example.sberproject.RecyclingPlace
import com.google.maps.android.clustering.ClusterItem

class RecyclingPlacesCluster(val recyclingPlace: RecyclingPlace) : ClusterItem {

    override fun getPosition() = recyclingPlace.coordinates

    override fun getTitle() = ""

    override fun getSnippet() = ""
}

