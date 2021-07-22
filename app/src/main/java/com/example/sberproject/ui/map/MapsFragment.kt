package com.example.sberproject.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.model.Route
import com.akexorcist.googledirection.util.DirectionConverter
import com.akexorcist.googledirection.util.execute
import com.example.sberproject.R
import com.example.sberproject.TrashType
import com.example.sberproject.Util
import com.example.sberproject.databinding.RecyclingPlaceInfoBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task


class MapsFragment : Fragment(), OnMapReadyCallback {
    companion object {
        const val TRASH_TYPE = "trash type"

        @JvmStatic
        fun newInstance(trashType: TrashType) = MapsFragment().apply {
            arguments = Bundle().apply {
                putSerializable(TRASH_TYPE, trashType)
            }
        }
    }

    private lateinit var googleMap: GoogleMap
    private var trashType: TrashType? = null
    private lateinit var viewModel: MapsViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var defaultLocation = LatLng(56.83556279777945, 60.61052534309914)
    private var permissionId = 52

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getSerializable(TRASH_TYPE)?.let {
            trashType = it as TrashType
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
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
        mapFragment?.getMapAsync(this)
        trashType?.let {
            viewModel.setTrashType(it)
        }
    }

    private fun requestPermission() {
        context?.let {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), permissionId
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        enableMyLocation()
        googleMap.run {
            moveCamera(CameraUpdateFactory.zoomTo(12.0F))
            moveCamera(CameraUpdateFactory.newLatLng(defaultLocation))
            setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                override fun getInfoWindow(marker: Marker): View? {
                    return null
                }

                override fun getInfoContents(marker: Marker): View {
                    val binding = RecyclingPlaceInfoBinding.inflate(layoutInflater)
                    binding.recyclingPlaceName.text = marker.title
                    val trashTypes = Util.recyclingPlaceToTrashTypes[marker.title]
                    var i = 0
                    trashTypes?.forEach { trashType ->
                        Util.trashTypeToIcon[trashType]?.let { r ->
                            val image = ImageView(requireContext())
                            val b = BitmapFactory.decodeResource(resources, r)
                            image.setImageDrawable(
                                Bitmap.createBitmap(b, 0, 0, b.width, b.height / 2)
                                    .toDrawable(resources)
                            )
                            if (i < 6)
                                binding.trashTypes1.addView(image)
                            else
                                binding.trashTypes2.addView(image)
                            i++
                        }
                    }
                    return binding.root
                }
            })
        }

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
                    onDirectionSuccess = {
                        it?.let {
                            onDirectionSuccess(googleMap, it)
                        }
                    }
                )
        })
    }

    private fun enableMyLocation() {
        if (!::googleMap.isInitialized) return
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            val task: Task<Location> = fusedLocationProviderClient.lastLocation
            task.addOnSuccessListener {
                if (it != null) {
                    val current = LatLng(it.latitude, it.longitude)
                    trashType?.let {
                        viewModel.findNearbyRecyclingPlaceFromStart(current)
                    }
                }
            }
        } else
            requestPermission()
    }

    private fun onDirectionSuccess(googleMap: GoogleMap, direction: Direction) {
        val route = direction.routeList[0]
        val directionPositionList = route.legList[0].directionPoint
        val line = DirectionConverter.createPolyline(
            requireContext(),
            directionPositionList,
            10,
            Color.parseColor("#aed6f5")
        )
        googleMap.addPolyline(line)
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
}