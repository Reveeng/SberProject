package com.example.sberproject

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Picture
import android.media.Image
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.sberproject.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI

class MainActivity : AppCompatActivity(), MainActivityCallback {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_scanner,
                R.id.navigation_articles,
                R.id.navigation_maps,
//                R.id.navigation_account
            )
        )
        navView.setupWithNavController(navController)

        //binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        setSupportActionBar(binding.toolbar)

        binding.accButton.setOnClickListener {
            navController.navigate(R.id.accountFragment)
        }
        binding.setButton.setOnClickListener {
            navController.navigate(R.id.navigation_setting)
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.navView.visibility = View.GONE
        binding.accButton.visibility = View.GONE
        binding.setButton.visibility = View.GONE

        /*val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .build()

        val objectDetector = ObjectDetection.getClient(options)

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.tink)
        val reses = mutableListOf<DetectedObject>()
        val t = objectDetector.process(InputImage.fromBitmap(bitmap, 0))
        t.addOnCompleteListener{
            if(it.isSuccessful){
                val r = it.result
                r.forEach{x -> reses.add(x)}
            }
        }*/
    }

    override fun onStart() {
        super.onStart()

    }

    override fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun login() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_scanner,
                R.id.navigation_articles,
                R.id.navigation_maps,
//                R.id.navigation_account
            )
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.navView.visibility = View.VISIBLE
        binding.accButton.visibility = View.VISIBLE
        binding.setButton.visibility = View.VISIBLE
    }

    override fun logout() {
        binding.navView.visibility = View.GONE
        binding.accButton.visibility = View.GONE
        binding.setButton.visibility = View.GONE
    }
}