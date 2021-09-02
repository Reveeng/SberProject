package com.example.sberproject.ui.scan

import android.os.Bundle
import com.google.mlkit.vision.barcode.Barcode
import okhttp3.*
import java.io.IOException
import kotlin.properties.Delegates

class BarcodeHandler(private val TTCallback:(bundle: Bundle)->Unit){

    var bundle = Bundle()

    private var barcodeArray: MutableList<String> = arrayListOf()

    var needToScan: Boolean by Delegates.observable(false){_,_,new->
        if (!new) {
            haveCodeInDb = new
            barcodeArray.clear()
        }
    }

    //if db contain barcode stop scanning
    private var haveCodeInDb: Boolean = false

    //http client
    private val client = OkHttpClient()

    fun addNewBarcodes(barcodes: MutableList<Barcode>){
        if (needToScan and !haveCodeInDb) {
             barcodes.forEach barcodeloop@{
                it?.rawValue.let{ barcode->
                    if (barcode.length != 13) return@barcodeloop
                    if (barcodeArray.size <= 10) barcodeArray.add(barcode)
                    else {
                        println("all items $barcodeArray")
                        val uniqueList = barcodeArray.distinct()
                        uniqueList.forEach { barcode ->
                            getGoodsByCode(barcode)
                        }
                        println("unique items $uniqueList")
                        }
                    }
                }
            }
        }

    private fun getGoodsByCode(barcode: String) {
        val request = Request.Builder()
            .url("http://158.101.217.50/fullinfo/$barcode")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                haveCodeInDb = true
                val strResponse: String = response.body!!.string()
                bundle.putString("info", strResponse)
                TTCallback(bundle)
            }
        })
    }
}