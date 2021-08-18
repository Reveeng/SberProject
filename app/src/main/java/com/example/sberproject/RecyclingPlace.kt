package com.example.sberproject

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

data class RecyclingPlace(
    val name: String,
    val information: String,
    val coordinates: LatLng,
    val trashTypes: Set<TrashType>
) : Serializable