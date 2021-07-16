package com.example.sberproject.ui.map

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.model.Route
import com.akexorcist.googledirection.util.DirectionConverter
import com.akexorcist.googledirection.util.execute
import com.example.sberproject.R
import com.example.sberproject.Util
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import java.util.*


class MapsFragment : Fragment() {
//    companion object {
//        const val TRASH_TYPE = "trash type"
//    }

    private lateinit var viewModel: MapsViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var defaultLocation = LatLng(56.83556279777945, 60.61052534309914)
    private var permissionId = 52

    private fun fetchLocation() {
        val task: Task<Location> = fusedLocationProviderClient.lastLocation
        if (
            context?.let {
                ActivityCompat.checkSelfPermission(
                    it.applicationContext,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED ||
            context?.let {
                ActivityCompat.checkSelfPermission(
                    it.applicationContext,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
            return
        }
        task.addOnSuccessListener {
            if (it != null) {
                val current = LatLng(it.latitude, it.longitude)
                viewModel.setCurrentLocation(current)
            }
        }
    }

    //function that allows us to get user permission
    private fun requestPermission() {
        context?.let {
            ActivityCompat.requestPermissions(
//                it.applicationContext as Activity,
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), permissionId
            )
        }
    }

    // function that checks if the Location service is enabled on device
    private fun isLocationEnabled(): Boolean {

        val locationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    //get the full address
    private fun getCityName(lat: Double, long: Double): String {
        var cityName: String = ""
        var countryName = ""
        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, long, 3)

        cityName = address[0].locality
        countryName = address[0].countryName
        Log.d("Debug:", "Your City: $cityName ; your Country $countryName")
        return cityName
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //we use it just for debugging
        if (requestCode == permissionId) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Debug", "You have the Permission")
            }
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        viewModel.currentLocation.observe(viewLifecycleOwner, {
            googleMap.run {
                addMarker(
                    MarkerOptions().title("Your position")
                        .position(it)
                )
                moveCamera(CameraUpdateFactory.zoomTo(12.0F))
                moveCamera(CameraUpdateFactory.newLatLng(it))
            }
        })

        viewModel.recyclingPlaces.observe(viewLifecycleOwner, { recyclingPlaces ->
            googleMap.run {
                recyclingPlaces.map {
                    val icon = if (Util.trashTypeToMarker.containsKey(it.trashTypes))
                        BitmapDescriptorFactory.fromResource(Util.trashTypeToMarker[it.trashTypes]!!)
                    else BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
                    addMarker(MarkerOptions().position(it.coordinates).title(it.name).icon(icon))
                }
            }
        })

        viewModel.routeToNearbyRecyclingPlace.observe(viewLifecycleOwner, { (start, end) ->
            GoogleDirection.withServerKey(getString(R.string.google_maps_key))
                .from(start)
                .to(end)
                .execute(
                    onDirectionSuccess = { direction ->
                        direction?.let {
                            onDirectionSuccess(googleMap, it)
                        }
                    }
                )
        })
    }

    private fun onDirectionSuccess(googleMap: GoogleMap, direction: Direction) {
        val route = direction.routeList[0]
        val directionPositionList = route.legList[0].directionPoint
        val line = DirectionConverter.createPolyline(
            requireContext(),
            directionPositionList,
            10,
            Color.parseColor("#67a9db")
        )
        googleMap.addPolyline(line)
        googleMap.addPolyline(line.width(5f).color(Color.parseColor("#aed6f5")))
        setCameraWithCoordinationBounds(googleMap, route)
    }

    private fun setCameraWithCoordinationBounds(googleMap: GoogleMap, route: Route) {
        val southwest = route.bound.southwestCoordination.coordination
        val northeast = route.bound.northeastCoordination.coordination
        val bounds = LatLngBounds(southwest, northeast)
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                100
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MapsViewModel(Util.recyclingPlaces) as T
            }
        }).get(MapsViewModel::class.java)
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

//        requestPermission()
//        getLastLocation()

        fetchLocation()
        mapFragment?.getMapAsync(callback)
//        if (savedInstanceState != null) {
//            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
//            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
//        }
    }
}