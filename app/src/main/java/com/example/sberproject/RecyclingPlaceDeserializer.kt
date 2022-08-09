package com.example.sberproject

import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class RecyclingPlaceDeserializer : JsonDeserializer<RecyclingPlace> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): RecyclingPlace {
        val name = json.asJsonObject.get("name").asString
        val information = json.asJsonObject.get("information").asString
        val address = json.asJsonObject.get("address")
        val lat = address.asJsonObject.get("lat").asDouble
        val lng = address.asJsonObject.get("lng").asDouble
        val coordinates = LatLng(lat, lng)
        val trashTypes = json.asJsonObject.get("trashtypes").asJsonArray.toList()
            .map { enumValueOf<TrashType>(it.asString) }.toSet()
        return RecyclingPlace(name, information, coordinates, trashTypes)
    }
}