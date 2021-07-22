package com.example.sberproject.ui.scan

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sberproject.R
import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.CountDownTimer
import android.text.Html
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.widget.Button
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import okhttp3.*
import java.util.concurrent.Executors
import kotlin.IllegalStateException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ScannerFragment : Fragment() {


//object that need for scaner
    private var cameraSelector: CameraSelector? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var previewView: PreviewView? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    //container for fragment
    private lateinit var container: RelativeLayout
    //button on screen
    private var scanButton: Button ? = null
    //just property that signal app that button pressed
    private var needToScan: Boolean = false
    //this property block sending soap request if new barcode equal to old barcode
    private var prevBarcode: String = ""

    private val soapBegin: String = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"+
            "<SOAP-ENV:Envelope "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" "+
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:tns=\"http://service.uhtt.ru/\">"+
            "<SOAP-ENV:Body>"
    private val soapEnd: String = "</SOAP-ENV:Body>"+
            "</SOAP-ENV:Envelope>"
    private var token: String = ""
    //ml kit poorly distinguished number like 1,7,4 so i just store all unique barcodes from 10 frames
    private var barcodeArray: MutableList<String> =  arrayListOf()
    //frame counter
    private var frameCount:Int = 0
    //if db contain barcode stop scanning
    private var haveCodeInDb:Boolean = false
    //every 60 second need to refresh uhtt token
    private val timer: CountDownTimer = object : CountDownTimer(60000, 1000){
        override fun onTick(millisUntilFinished: Long) {
            println(millisUntilFinished/1000)
        }
        override fun onFinish() {
            getUHTTToken()
            this.start()
        }
    }
    //http client
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?  = inflater.inflate(R.layout.fragment_scanner,container,false)

    @SuppressLint("MissingPermission", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        container = view as RelativeLayout
        previewView = container.findViewById(R.id.preview_view)
        scanButton = container.findViewById(R.id.scanBtn)
        //trigger scanning by touch button
        scanButton?.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN){

                this.needToScan = true
                println(this.needToScan)
            }
            if (event.action == MotionEvent.ACTION_UP){
                this.needToScan = false
                println(this.needToScan)
            }
            false
        }
        setupCamera()
        getUHTTToken()
        timer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    private val screenAspectRatio: Int
        get() {
            // Get screen metrics used to setup camera for full screen resolution
            val metrics = DisplayMetrics().also { previewView?.display?.getRealMetrics(it) }
            return aspectRatio(
                metrics.widthPixels,
                metrics.heightPixels
            )
        }
    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun setupCamera(){
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        ViewModelProvider(
            this,ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
            .get(CameraXViewModel::class.java)
            .processCameraProvider
            .observe(viewLifecycleOwner,
                Observer { provider: ProcessCameraProvider? ->
                    cameraProvider = provider
                    if (isCameraPermissionGranted()){
                        bindCameraUseCases()
                    } else {
                        this.activity?.let {
                            ActivityCompat.requestPermissions(
                                it,
                                arrayOf(Manifest.permission.CAMERA),
                                PERMISSION_CAMERA_REQUEST
                            )
                        }
                    }
                })
    }
    private fun bindCameraUseCases() {
        bindPreviewUseCase()
        bindAnalyseUseCase()
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }
        previewUseCase = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(previewView!!.display.rotation)
            .build()
        previewUseCase!!.setSurfaceProvider(previewView!!.surfaceProvider)
        try {
            cameraProvider!!.bindToLifecycle(
                /* lifecycleOwner= */this,
                cameraSelector!!,
                previewUseCase
            )
        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, illegalStateException.message.toString())
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, illegalArgumentException.message.toString())
        }
    }

    private fun bindAnalyseUseCase(){
        val barcodeScanner : BarcodeScanner = BarcodeScanning.getClient()
        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }
        analysisUseCase = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(previewView!!.display.rotation)
            .build()
        val cameraExecutor = Executors.newSingleThreadExecutor()
        analysisUseCase?.setAnalyzer(cameraExecutor,
            ImageAnalysis.Analyzer {imageProxy ->
                processImageProxy(
                    barcodeScanner,
                    imageProxy
                )
            }
        )
        try {
            cameraProvider!!.bindToLifecycle(
                /* lifecycleOwner= */this,
                cameraSelector!!,
                analysisUseCase
            )
        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, illegalStateException.message.toString())
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, illegalArgumentException.message.toString())
        }
    }
    //called when ml kit see barcode
    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy
    ){
        val inputImage =
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                if (this.needToScan && !haveCodeInDb) {
                    barcodes.forEach {
                        if (frameCount < 10) {
                            if (prevBarcode != it.rawValue && it.rawValue.length == 13) {
                                frameCount++
                                if (!barcodeArray.contains(it.rawValue)) {
                                    barcodeArray.add(it.rawValue)
                                }
                            }
                        }
                        if (frameCount == 10) {
                            frameCount++
                            if(barcodeArray.isEmpty()){
                                frameCount = 0
                            }
                            else{
                                checkBarcodes()
                            }
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.e(TAG, it.message.toString())
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun checkBarcodes(){
        println(barcodeArray.size)
        barcodeArray.forEach {
            getGoodsByCode(it)
        }
    }
    private fun getUHTTToken(){
        val postBody:String = this.soapBegin+
                "<tns:auth>"+
                    "<email xsi:type='xsd:string'>ntesla2016@yandex.ru</email>"+
                    "<password xsi:type='xsd:string'>KOKS4212</password>"+
                "</tns:auth>"+
                this.soapEnd
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
                var strResponse :String  = response.body!!.string()
                val regex = Regex("<token>.*</token>")
                val token: String? = regex.find(strResponse)?.value?.removePrefix("<token>")?.removeSuffix("</token>")
                updateToken(token)
            }
        })
    }
    private fun updateToken(token: String?){
        if (token != null) {
            this.token = token
        }
    }

    private fun getGoodsByCode(barcode:String){
        val postBody:String = this.soapBegin+
                "<tns:getGoodsByCode>"+
                "<token xsi:type='xsd:string'>"+this.token+"</token>"+
                "<code xsi:type='xsd:string'>"+barcode+"</code>"+
                "</tns:getGoodsByCode>"+
                this.soapEnd
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
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                haveCodeInDb = true
                val strResponse: String = response.body!!.string()
                val regex = Regex("<Name>.*</Name>")
                val goods: String? = regex.find(strResponse)?.value?.removePrefix("<Name>")?.removeSuffix("</Name>")
                println(Html.fromHtml(goods, Html.FROM_HTML_MODE_LEGACY))
            }
        })
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_CAMERA_REQUEST){
            if ( isCameraPermissionGranted()){
                bindCameraUseCases()
            }
            else {
                Log.e(TAG, "no camera permission")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    private fun isCameraPermissionGranted():Boolean{
        return ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val TAG = "TAG"
        private const val PERMISSION_CAMERA_REQUEST = 1
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
        private val MEDIA_TYPE_MARKDOWN = "text/xml; charset=iso-8859-1".toMediaType()
    }
}