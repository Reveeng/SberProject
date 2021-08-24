package com.example.sberproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.sberproject.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), MainActivityCallback {

    private lateinit var binding: ActivityMainBinding

    //    lateinit var  fusedLocationProviderClient: FusedLocationProviderClient
//    lateinit var  locationRequest: LocationRequest
//    // just an int that must be unique
//    private var PERMISSION_ID = 52
//
//    // function that checks the uses permission
//    private fun CheckPermission():Boolean{
//        if(
//            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
//                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
//            ) {
//            return true
//        }
//        return false
//    }
//
//    //function that allows us to get user permission
//    private  fun RequestPermission(){
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_ID
//        )
//    }
//
//    // function that checks if the Location service is enabled on device
//    private fun isLocationEnabled():Boolean{
//
//        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        //we use it just for debugging
//        if(requestCode == PERMISSION_ID){
//            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                Log.d("Debug", "You have the Permission")
//            }
//        }
//    }
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

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        setSupportActionBar(binding.toolbar)

        binding.accButton.setOnClickListener {
            navController.navigate(R.id.navigation_account)
        }
        binding.setButton.setOnClickListener {
            navController.navigate(R.id.navigation_setting)
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }
}