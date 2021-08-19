package com.example.sberproject.ui.scan

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import com.google.mlkit.vision.barcode.Barcode
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import kotlin.properties.Delegates
import com.example.sberproject.TrashType

class BarcodeHandler(private val TTCallback:(bundle: Bundle)->Unit){

    private val keyValue =
        arrayOf(
            //key value for paper
            arrayOf("книга", "тетрадь"),
            //key value for plastic
            arrayOf("пл.{1}бут", "п.{1}б", "пэт")
            //TODO: add other key words for trash type
        )
    var bundle = Bundle()

    private val soapBegin: String = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
            "<SOAP-ENV:Envelope " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
            "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "xmlns:tns=\"http://service.uhtt.ru/\">" +
            "<SOAP-ENV:Body>"
    private val soapEnd: String = "</SOAP-ENV:Body>" +
            "</SOAP-ENV:Envelope>"

    private var Utoken: String = ""
    var trashType: TrashType by Delegates.observable(TrashType.OTHER){_,_,new->
        bundle.putString("trash type", new.toString())
        TTCallback(bundle)
    }

    private var barcodeArray: MutableList<String> = arrayListOf()
    var needToScan: Boolean by Delegates.observable(false){_,_,new->
        if (!new) {
            haveCodeInDb = new
            barcodeArray.clear()
        }
    }

    //frame counter
    private var frameCount: Int = 0

    //if db contain barcode stop scanning
    private var haveCodeInDb: Boolean = false

    //http client
    private val client = OkHttpClient()
    init {
        getUHTTToken()
    }

    //every 60 second need to refresh uhtt token
    val timer: CountDownTimer = object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            println(millisUntilFinished / 1000)
        }

        override fun onFinish() {
            getUHTTToken()
            this.start()
        }
    }

    private fun getUHTTToken() {
        val postBody: String = this.soapBegin +
                "<tns:auth>" +
                "<email xsi:type='xsd:string'>ntesla2016@yandex.ru</email>" +
                "<password xsi:type='xsd:string'>KOKS4212</password>" +
                "</tns:auth>" +
                this.soapEnd
        val request = Request.Builder()
            .url("http://www.uhtt.ru/dispatcher/ws/iface")
            .post(postBody.toRequestBody(BarcodeHandler.MEDIA_TYPE_MARKDOWN))
            .header("Content-Type", "text/xml;charset=ISO-8859-1")
            .addHeader("SOAPAction", "")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val strResponse: String = response.body?.string().toString()
                val regex = Regex("<token>.*</token>")
                val token: String? = regex.find(strResponse)?.value?.removePrefix("<token>")
                    ?.removeSuffix("</token>")
                token?.let{
                    println(it)
                    Utoken = it
                }
            }
        })
    }

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
        val postBody: String = soapBegin +
                "<tns:getGoodsByCode>" +
                "<token xsi:type='xsd:string'>" + Utoken + "</token>" +
                "<code xsi:type='xsd:string'>" + barcode + "</code>" +
                "</tns:getGoodsByCode>" +
                soapEnd
        val request = Request.Builder()
            .url("http://www.uhtt.ru/dispatcher/ws/iface")
            .post(postBody.toRequestBody(MEDIA_TYPE_MARKDOWN))
            .header("Content-Type", "text/xml;charset=ISO-8859-1")
            .addHeader("SOAPAction", "")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                haveCodeInDb = true
                val strResponse: String = response.body!!.string()
                println("response is $strResponse")
                val regex = Regex("<Name>.*</Name>")
                var goods: String? =
                    regex.find(strResponse)?.value?.removePrefix("<Name>")?.removeSuffix("</Name>")

                if (goods != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        bundle.putString("barcode", barcode)
                        bundle.putString("rawinfo", goods)
                        goods = Html.fromHtml(goods, Html.FROM_HTML_MODE_LEGACY).toString()
                        bundle.putString("info", goods)
                    }
                    trashType =  findTrashType(goods)
                    println("trashType is $trashType")
                    needToScan = false
                }
            }
        })
    }
    private fun findTrashType(goods: String): TrashType {
        var trashIndex: Int = -1
        for ((index, values) in keyValue.withIndex()) {
            for (oneString in values) {
                val regex = Regex(oneString, RegexOption.IGNORE_CASE)
                if (regex.containsMatchIn(goods)){
                    trashIndex = index
                    break
                }
            }
        }
        return if (trashIndex != -1)
            TrashType.fromInt(trashIndex)
        else TrashType.OTHER
    }

    companion object {
        private val MEDIA_TYPE_MARKDOWN = "text/xml; charset=iso-8859-1".toMediaType()
    }
}