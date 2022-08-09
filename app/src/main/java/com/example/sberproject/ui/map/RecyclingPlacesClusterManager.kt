package com.example.sberproject.ui.map

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.ClusterManager

class RecyclingPlacesClusterManager(
    context: Context,
    googleMap: GoogleMap,
    private val onClick: (Marker) -> Boolean
) : ClusterManager<RecyclingPlacesCluster>(context, googleMap) {
    override fun onMarkerClick(marker: Marker): Boolean = onClick(marker)
}