package com.example.sberproject.view.fragments

import retrofit2.http.GET
import retrofit2.http.Path

interface InstructionApi {
    @GET("{barcode}")
    suspend fun getInstruction(@Path("barcode") barcode: String): Instruction

    @GET("{trash-type}")
    suspend fun getInstructionWithoutBarcode(@Path("trash-type") trashType: String): Instruction
}