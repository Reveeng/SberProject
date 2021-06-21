package com.example.sberproject.ui.map

import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sberproject.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        val currentPos = LatLng(56.83556279777945, 60.61052534309914)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}