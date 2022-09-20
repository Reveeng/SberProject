package com.example.sberproject.ui.map


import org.osmdroid.config.Configuration
import org.osmdroid.config.Configuration.*
import android.Manifest
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
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
import com.example.sberproject.*
import com.example.sberproject.databinding.FragmentMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay
import java.util.*


class MapsFragment : Fragment() {
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

    private var permissionId = 52

    private var routeLine: Polyline? = null

    private lateinit var clusterManager: ClusterManager<RecyclingPlacesCluster>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        trashType = TrashType.PLASTIC
        arguments?.getSerializable(TRASH_TYPE)?.let {
            trashType = it as TrashType
        }
        viewModel = ViewModelProvider(
            requireActivity(),
            MapsViewModelFactory(requireContext())
        ).get(MapsViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        binding.map.setMultiTouchControls(true);
        val mapController = binding.map.controller
        mapController.setZoom(14.0)
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PERMISSION_GRANTED
        ) {
            println("Location Permission GRANTED")
            val locationOverlay =
                MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), binding.map);
            locationOverlay.enableMyLocation();
            binding.map.overlays.add(locationOverlay)
        } else {
            println("Location Permission DENIED")
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
        val startPoint = GeoPoint(56.86885040376578, 60.5573143561283)
        mapController.setCenter(startPoint)
        runBlocking { viewModel.setCity("Екатеринбург") }
        viewModel.recyclingPlaces.observe(viewLifecycleOwner) {
            binding.map.overlays.removeIf { x -> x is Marker}
            it.forEach { rp ->
                val marker = Marker(binding.map)
                marker.position = GeoPoint(rp.coordinates.latitude, rp.coordinates.longitude)
                Util.trashTypeToMarker[rp.trashTypes]?.let {
                    marker.icon = ContextCompat.getDrawable(requireContext(), it)
                }
                marker.setOnMarkerClickListener { m, mv ->
                    showInfoSheetAboutRecyclingPlace(rp)
                    true
                }
                binding.map.overlays.add(marker)
            }
            /*val overlay = ItemizedOverlayWithFocus(items, object: ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                override fun onItemSingleTapUp(index:Int, item:OverlayItem):Boolean {
                    //do something
                    return true
                }
                override fun onItemLongPress(index:Int, item:OverlayItem):Boolean {
                    return false
                }
            }, requireContext())
            overlay.setFocusItemsOnTap(true);
            currentOverlay = overlay
            binding.map.overlays.add(overlay)*/
            binding.map.invalidate()
        }

        val roadManager = OSRMRoadManager(requireContext(), BuildConfig.APPLICATION_ID)
        GlobalScope.launch {
            val road = roadManager.getRoad(
                arrayListOf(
                    GeoPoint(56.86885040376578, 60.5573143561283),
                    GeoPoint(56.839519979823166, 60.598710011969644)
                )
            )
            val roadOverly = RoadManager.buildRoadOverlay(road)
            binding.map.overlays.add(roadOverly)
            binding.map.invalidate()
        }
        //viewModel.setRoad(roadManager, GeoPoint(56.86885040376578, 60.5573143561283), GeoPoint(56.839519979823166, 60.598710011969644))


        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /*val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        mapFragment?.getMapAsync(this)

        viewModel.recyclingPlaceToShow.observe(viewLifecycleOwner, {
            showInfoSheetAboutRecyclingPlace(it)
        })

        viewModel.clickOnCloseInfoSheet.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {
                clickOnCloseInfoSheet()
            }
        })

        viewModel.clickOnBuildRoute.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let{ recyclingPlace ->
                clickOnBuildRoute(recyclingPlace)
            }
        })

        viewModel.resetRoute.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let{
                resetRoute()
            }
        })*/
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivityCallback?)?.setActionBarTitle("Карта")
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

    /*override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        enableMyLocation()
        googleMap.run {
            clusterManager =
                RecyclingPlacesClusterManager(requireActivity(), this) { onMarkerClick(it) }
            clusterManager.renderer =
                RecyclingPlacesClusterRender(requireActivity(), this, clusterManager)
            setOnCameraIdleListener(clusterManager)
            setOnMarkerClickListener(clusterManager)
        }
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
            googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.style_json
                )
            )

        viewModel.recyclingPlaces.observe(viewLifecycleOwner, { recyclingPlaces ->
            googleMap.run {
                clear()
                clusterManager.clearItems()
                recyclingPlaces.forEach {
                    clusterManager.addItem(RecyclingPlacesCluster(it))
                }
                clusterManager.cluster()
            }
        })

        viewModel.routeToRecyclingPlace.observe(viewLifecycleOwner, {
               /* it.getContentIfNotHandled()?.let{ */(start, end) ->
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
               // }
        })

        googleMap.setOnMarkerClickListener(this)
    }*/

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
                    val city = Geocoder(requireContext(), Locale.getDefault()).getFromLocation(
                        it.latitude,
                        it.longitude,
                        1
                    )[0].locality


                    val current = LatLng(it.latitude, it.longitude)

                    runBlocking {

                        viewModel.setCity(city)

                        trashType?.let {
                            viewModel.setTrashType(it)
                            viewModel.findNearbyRecyclingPlaceFromStart(current)
                        }
                    }


                    googleMap.run {
                        moveCamera(CameraUpdateFactory.zoomTo(12.0F))
                        moveCamera(CameraUpdateFactory.newLatLng(current))
                    }
//                    trashType?.let {
//                        viewModel.findNearbyRecyclingPlaceFromStart(current)
//                    }
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

    /*override fun onMarkerClick(marker: Marker): Boolean {
        Util.markerToRecyclingPlace[marker]?.let {
            showInfoSheetAboutRecyclingPlace(it)
        }
        return true
    }*/

    private fun showInfoSheetAboutRecyclingPlace(recyclingPlace: RecyclingPlace) {
        childFragmentManager.commit {
            replace(R.id.trash_types_list, RecyclingPlaceInfoFragment.newInstance(recyclingPlace))
        }
    }

    private fun clickOnBuildRoute(recyclingPlace: RecyclingPlace) {
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

    private fun clickOnCloseInfoSheet() {
        childFragmentManager.commit {
            replace(R.id.trash_types_list, TrashTypesListFragment())
        }
    }

    private fun resetRoute() {
        routeLine?.remove()
        childFragmentManager.commit {
            replace(R.id.trash_types_list, TrashTypesListFragment())
        }
    }
}