package com.example.sberproject.ui.map

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.SettingsSlicesContract.KEY_LOCATION
import android.provider.SettingsSlicesContract.KEY_CAMERA_POSITION
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.sberproject.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*

class MapsFragment : Fragment() {

    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null

    lateinit var  fusedLocationProviderClient: FusedLocationProviderClient

    var defaultLocation = LatLng(56.83556279777945, 60.61052534309914)
    private var locationPermissionGranted = false

    private var lastKnownLocation: Location? = null
    private var likelyPlaceNames: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAddresses: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAttributions: Array<List<*>?> = arrayOfNulls(0)
    private var likelyPlaceLatLngs: Array<LatLng?> = arrayOfNulls(0)
//    lateinit var  locationRequest: LocationRequest
//    lateinit var  locationCallback: LocationCallback
//
//    just an int that must be unique
//    private var PERMISSION_ID = 52

//    private fun getLocationPermission() {
//    /*
//     * Request location permission, so that we can get the location of the
//     * device. The result of the permission request is handled by a callback,
//     * onRequestPermissionsResult.
//     */
//    if (ContextCompat.checkSelfPermission(requireContext(),
//            android.Manifest.permission.ACCESS_FINE_LOCATION)
//        == PackageManager.PERMISSION_GRANTED) {
//        locationPermissionGranted = true
//    } else {
//        ActivityCompat.requestPermissions(this, arrayOf(android.fest.permission.ACCESS_FINE_LOCATION),
//            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
//    }
//}



    // function that checks the uses permission
//    fun fetchLocation(): LatLng{
//        val task: Task<Location> = fusedLocationProviderClient.lastLocation
//        if(
//            context?.let { ActivityCompat.checkSelfPermission(it.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED ||
//                    context?.let { ActivityCompat.checkSelfPermission(it.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) } != PackageManager.PERMISSION_GRANTED
//            ) {
//            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 52)
//            return
//        }
//        task.addOnSuccessListener {
//            if(it != null) {
//                return@addOnSuccessListener LatLng(it.latitude, it.longitude)
//            }
//        }
//        //so if this function returns false, we need then to request the permission
//    }

    //function that allows us to get user permission
    fun requestPermission(){
        context?.let {
            ActivityCompat.requestPermissions(
                it.applicationContext as Activity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_ID
            )
        }
    }

    // function that checks if the Location service is enabled on device
    private fun isLocationEnabled(): Boolean {

        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    //function that allows us to get the last location
//    @SuppressLint("MissingPermission")
//    fun getLastLocation(){
//        //first we check the permission
//        if (checkLocationPermission()) {
//            if (isLocationEnabled()) {
//                //get the location
//                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
//                    val location: Location? = task.result
//                    if (location == null) {
//                        //we will get the new user location
//                        NewLocationData()
//                    } else {
//                        Log.d("Debug:", "Your Location:" + location.longitude)
////                        textView.text =
////                            "You Current Location is : Long: " + location.longitude + " , Lat: " + location.latitude + "\n" + getCityName(
////                                location.latitude,
////                                location.longitude
////                            )
//                        var currPos =  LatLng(location.longitude, location.latitude)
//                    }
//                }
//            } else {
//                Toast.makeText(
//                    requireContext(),
//                    "Пожалуйста разрешите доступ к локации устройства",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        } else {
//            requestPermission()
//        }
//    }

    //a function that will update the user location
//    @SuppressLint("MissingPermission")
//    fun NewLocationData(){
//        val locationRequest =  LocationRequest()
//        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        locationRequest.interval = 0
//        locationRequest.fastestInterval = 0
//        locationRequest.numUpdates = 1
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
//        fusedLocationProviderClient!!.requestLocationUpdates(
//            locationRequest,locationCallback, Looper.myLooper()
//        )
//    }


//    val locationCallback = object : LocationCallback(){
//        override fun onLocationResult(locationResult: LocationResult) {
//            val lastLocation: Location = locationResult.lastLocation
//            Log.d("Debug:","your last last location: "+ lastLocation.longitude.toString())
//            var currPos: LatLng = LatLng(lastLocation.longitude, lastLocation.latitude) // + "\n" + getCityName(lastLocation.latitude,lastLocation.longitude)
//        }
//    }

    //get the full address
    private fun getCityName(lat: Double,long: Double):String{
        var cityName:String = ""
        var countryName = ""
        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
        val Adress = geoCoder.getFromLocation(lat,long,3)

        cityName = Adress.get(0).locality
        countryName = Adress.get(0).countryName
        Log.d("Debug:","Your City: " + cityName + " ; your Country " + countryName)
        return cityName
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //we use it just for debugging
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Debug", "You have the Permission")
            }
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->

        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        //Geocoder() geocoder = new Geocoder(context, Locale.getDefault());

        //var currentPos = Geocoder.getFromLocation(latitude, longitude, 1)

//        var currPos = Location() currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        var currentPos = LatLng(56.83556279777945, 60.61052534309914)
//        val arrayOfMarkers: Array<Marker>

        val curPos: Marker = googleMap.addMarker(MarkerOptions()
            .position(currentPos)
            .title("Your position"))
        val nemus: Marker = googleMap.addMarker(MarkerOptions()
            .position(LatLng(56.8407395692402, 60.593118629081964))
            .title("Немузей мусора")
            .icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
        val predp: Marker = googleMap.addMarker(MarkerOptions()
            .position(LatLng(56.83293148535164, 60.61301988490218))
            .title("Предприятие комплексного решения проблем промышленных отходов")
            .icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_CYAN)))
        val monast: Marker = googleMap.addMarker(MarkerOptions()
            .position(LatLng(56.822856527309504, 60.59851815236338))
            .title("Ново-тихвинский женский монастырь")
            .icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
        val line: Polyline = googleMap.addPolyline(PolylineOptions()
            .add(
                LatLng(56.83529218503051, 60.61094518304741),
                LatLng(56.833666581963925, 60.59570049330751),
                LatLng(56.839178319738956, 60.59400247705653),
                LatLng(56.83956076465086, 60.594364554036794),
                LatLng(56.840530518092145, 60.594152302007345),
                LatLng(56.84082417086563, 60.59406490411435),
                LatLng(56.84094709459853, 60.593877622915066),
                LatLng(56.8412202570042, 60.59330329390392),
                LatLng(56.84068758846683, 60.59330329390392)
            )
            .color(-65536))
        if (arguments?.getBoolean("MyArg") == true){
            line.isVisible =  true
            nemus.showInfoWindow()
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(14.0F))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(56.83715334529192, 60.5989840370588)))
        }
        else{
            line.isVisible = false
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(13.0F))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentPos))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
//        requestPermission()
//        getLastLocation()

        mapFragment?.getMapAsync(callback)
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }
    }
}

//class MyLocationDemoActivity : AppCompatActivity(), GoogleMap.OnMyLocationButtonClickListener,
//    GoogleMap.OnMyLocationClickListener, OnMapReadyCallback,
//    ActivityCompat.OnRequestPermissionsResultCallback {
//    /**
//     * Flag indicating whether a requested permission has been denied after returning in
//     * [.onRequestPermissionsResult].
//     */
//    private var permissionDenied = false
//    private lateinit var map: GoogleMap
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_maps)
//        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
//        mapFragment?.getMapAsync(this)
//    }
//
//    override fun onMapReady(googleMap: GoogleMap?) {
//        map = googleMap ?: return
//        googleMap.setOnMyLocationButtonClickListener(this)
//        googleMap.setOnMyLocationClickListener(this)
//        enableMyLocation()
//    }
//
//    /**
//     * Enables the My Location layer if the fine location permission has been granted.
//     */
//    private fun enableMyLocation() {
//        if (!::map.isInitialized) return
//        // [START maps_check_location_permission]
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
//            == PackageManager.PERMISSION_GRANTED) {
//            map.isMyLocationEnabled = true
//        } else {
//            // Permission to access the location is missing. Show rationale and request permission
//            MapsFragment.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
//                android.Manifest.permission.ACCESS_FINE_LOCATION, true
//            )
//        }
//        // [END maps_check_location_permission]
//    }
//
//    override fun onMyLocationButtonClick(): Boolean {
//        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
//        // Return false so that we don't consume the event and the default behavior still occurs
//        // (the camera animates to the user's current position).
//        return false
//    }
//
//    override fun onMyLocationClick(location: Location) {
//        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG).show()
//    }
//
//    // [START maps_check_location_permission_result]
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
//            return
//        }
//        if (isPermissionGranted(permissions, grantResults, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
//            // Enable the my location layer if the permission has been granted.
//            enableMyLocation()
//        } else {
//            // Permission was denied. Display an error message
//            // [START_EXCLUDE]
//            // Display the missing permission error dialog when the fragments resume.
//            permissionDenied = true
//            // [END_EXCLUDE]
//        }
//    }
//
//    // [END maps_check_location_permission_result]
//    override fun onResumeFragments() {
//        super.onResumeFragments()
//        if (permissionDenied) {
//            // Permission was not granted, display error dialog.
//            showMissingPermissionError()
//            permissionDenied = false
//        }
//    }
//
//    /**
//     * Displays a dialog with error message explaining that the location permission is missing.
//     */
//    private fun showMissingPermissionError() {
//        newInstance(true).show(supportFragmentManager, "dialog")
//    }
//
//    companion object {
//        /**
//         * Request code for location permission request.
//         *
//         * @see .onRequestPermissionsResult
//         */
//        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
//    }
//}