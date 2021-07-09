package com.example.sberproject

import com.google.android.gms.maps.model.LatLng

data class RecyclingPlace(val name: String, val coordinates: LatLng, val trashTypes: Set<TrashType>)