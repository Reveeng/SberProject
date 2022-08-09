package com.example.sberproject.view.fragments

import com.example.sberproject.TrashType
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Instruction(
    val barcode: String?,
    @SerializedName("trash type") val trashType: TrashType?,
    val before: String,
    val after: String,
    val instruction: String
): Serializable

