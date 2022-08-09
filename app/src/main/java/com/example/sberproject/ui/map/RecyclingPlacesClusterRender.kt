package com.example.sberproject.ui.map

import android.content.Context
import com.example.sberproject.Util
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class RecyclingPlacesClusterRender(
    context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<RecyclingPlacesCluster>
) : DefaultClusterRenderer<RecyclingPlacesCluster>(context, map, clusterManager) {
    override fun onBeforeClusterItemRendered(
        item: RecyclingPlacesCluster,
        markerOptions: MarkerOptions
    ) {
        val icon = if (Util.trashTypeToMarker.containsKey(item.recyclingPlace.trashTypes))
            BitmapDescriptorFactory.fromResource(Util.trashTypeToMarker[item.recyclingPlace.trashTypes]!!)
        else BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
        markerOptions.position(item.recyclingPlace.coordinates).title(item.recyclingPlace.name)
            .icon(icon)
    }

    override fun onClusterItemRendered(clusterItem: RecyclingPlacesCluster, marker: Marker) {
        super.onClusterItemRendered(clusterItem, marker)
        Util.markerToRecyclingPlace[marker] = clusterItem.recyclingPlace
    }
}