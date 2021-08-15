package com.example.sberproject.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.model.Route
import com.akexorcist.googledirection.util.DirectionConverter
import com.akexorcist.googledirection.util.execute
import com.example.sberproject.R
import com.example.sberproject.RecyclingPlace
import com.example.sberproject.TrashType
import com.example.sberproject.Util
import com.example.sberproject.databinding.FragmentMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task


class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    MapFragmentCallback {
    companion object {
        const val TRASH_TYPE = "trash type"

        @JvmStatic
        fun newInstance(trashType: TrashType) = MapsFragment().apply {
            arguments = Bundle().apply {
                putSerializable(TRASH_TYPE, trashType)
            }
        }
    }

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap
    private var trashType: TrashType? = null
    private lateinit var viewModel: MapsViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var defaultLocation = LatLng(56.83556279777945, 60.61052534309914)
    private var permissionId = 52

    private var routeLine: Polyline? = null

    private val markerToRecyclingPlace by lazy {
        mutableMapOf<Marker, RecyclingPlace>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getSerializable(TRASH_TYPE)?.let {
            trashType = it as TrashType
        }
        viewModel = ViewModelProvider(
            requireActivity(),
            MapsViewModelFactory()
        ).get(MapsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
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

        viewModel.recyclingPlaceToShow.observe(viewLifecycleOwner, {
            showInfoSheetAboutRecyclingPlace(it)
        })
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
        }

        viewModel.recyclingPlaces.observe(viewLifecycleOwner, { recyclingPlaces ->
            googleMap.run {
                clear()
//                routeLine?.let {
//                    addPolyline(it.)
//                }
                recyclingPlaces.map {
                    val icon = if (Util.trashTypeToMarker.containsKey(it.trashTypes))
                        BitmapDescriptorFactory.fromResource(Util.trashTypeToMarker[it.trashTypes]!!)
                    else BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
                    val marker = addMarker(
                        MarkerOptions().position(it.coordinates).title(it.name).icon(icon)
                    )
                    markerToRecyclingPlace[marker] = it
                }
            }
        })

        viewModel.routeToRecyclingPlace.observe(viewLifecycleOwner, { (start, end) ->
            routeLine?.remove()
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

        googleMap.setOnMarkerClickListener(this)
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
        routeLine = googleMap.addPolyline(line)
        setCameraWithCoordinationBounds(googleMap, route)
    }

    private fun setCameraWithCoordinationBounds(googleMap: GoogleMap, route: Route) {
        val southwest = route.bound.southwestCoordination.coordination
        val northeast = route.bound.northeastCoordination.coordination
        val bounds = LatLngBounds(southwest, northeast)
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                250
            )
        )
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        markerToRecyclingPlace[marker]?.let {
            showInfoSheetAboutRecyclingPlace(it)
        }
        return true
    }

    private fun showInfoSheetAboutRecyclingPlace(recyclingPlace: RecyclingPlace) {
        childFragmentManager.commit {
            replace(R.id.trash_types_list, RecyclingPlaceInfoFragment.newInstance(recyclingPlace))
        }
    }

    override fun clickOnBuildRoute(recyclingPlace: RecyclingPlace) {
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
                    viewModel.setSourceAndDestination(current, recyclingPlace.coordinates)
                }
            }
        } else
            requestPermission()
    }

    override fun clickOnCloseInfoSheet() {
        childFragmentManager.commit {
            replace(R.id.trash_types_list, TrashTypesListFragment())
        }
    }
}