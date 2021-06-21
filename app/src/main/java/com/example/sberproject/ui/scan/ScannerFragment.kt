package com.example.sberproject.ui.scan

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.sberproject.databinding.FragmentScannerBinding
import com.example.sberproject.R

private const val CAMERA_REQUEST_CODE = 101

class ScannerFragment : Fragment() {

    private var _binding: FragmentScannerBinding? = null

    private lateinit var codeScaner: CodeScanner
    private val binding get() = _binding!!

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupPermissions()
        codeScaner()
        return root
    }

    private fun codeScaner(){
        val context: Context = this.context ?: return
        codeScaner = CodeScanner(context,binding.scannerView)
        codeScaner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

//            decodeCallback = DecodeCallback{
//                findNavController().navigate(R.id.navigation_first_instruction)
//            }
            errorCallback = ErrorCallback {
                (context as Activity).runOnUiThread{
                    Log.e("Main", "Camera initializing error ${it.message}")
                }
            }
            binding.scannerView.setOnClickListener{
                findNavController().navigate(R.id.navigation_first_instruction)
                codeScaner.startPreview()
            }



        }
    }

    override fun onResume() {
        super.onResume()
        codeScaner.startPreview()
    }

    override fun onPause() {
        codeScaner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions(){
        val context: Context = this.context ?: return
        val permission = ContextCompat.checkSelfPermission(context,
        android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }
    private fun makeRequest(){
        requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    val context: Context = this.context ?: return
                    Toast.makeText(context, "Мы не сможем отсканировать штрих код без разрешения использовать камеру",
                        Toast.LENGTH_SHORT).show()
                }else{
                    //succes
                }
            }
        }


    }
}