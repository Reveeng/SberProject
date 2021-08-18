package com.example.sberproject.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import com.example.sberproject.RecyclingPlace
import com.example.sberproject.Util
import com.example.sberproject.databinding.FragmentRecyclingPlaceInfoBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class RecyclingPlaceInfoFragment : Fragment() {
    private var _binding: FragmentRecyclingPlaceInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclingPlace: RecyclingPlace
    private var callback: MapFragmentCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = parentFragment as MapFragmentCallback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recyclingPlace = it.getSerializable(RECYCLING_PLACE) as RecyclingPlace
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecyclingPlaceInfoBinding.inflate(inflater, container, false)
        binding.name.text = recyclingPlace.name
        binding.information.text = recyclingPlace.information
        binding.name.setOnClickListener {
            val bottomSheetBehavior = BottomSheetBehavior.from(binding.root.parent as View)
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            else bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        binding.closeInfoSheet.setOnClickListener {
            callback?.clickOnCloseInfoSheet()
        }
        binding.buildRouteButton.setOnClickListener {
            callback?.clickOnBuildRoute(recyclingPlace)
        }
        setTrashTypeIcons()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.root.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setTrashTypeIcons() {
        var i = 0
        recyclingPlace.trashTypes.forEach { trashType ->
            Util.trashTypeToIcon[trashType]?.let { r ->
                val image = ImageView(requireContext())
                val b = BitmapFactory.decodeResource(resources, r)
                image.setImageDrawable(
                    Bitmap.createBitmap(b, 0, 0, b.width, b.height / 2)
                        .toDrawable(resources)
                )
                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                val displayMetrics = resources.displayMetrics
                val size = 25f
                params.width =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        size,
                        displayMetrics
                    )
                        .toInt()
                params.height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    size,
                    displayMetrics
                )
                    .toInt()
                image.layoutParams = params
//                if (i < 7)
                binding.trashTypes.addView(image)
//                else
//                    binding.trashTypes2.addView(image)
                i++
            }
        }
    }

    companion object {
        const val RECYCLING_PLACE = "recycling place"

        @JvmStatic
        fun newInstance(recyclingPlace: RecyclingPlace) =
            RecyclingPlaceInfoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(RECYCLING_PLACE, recyclingPlace)
                }
            }
    }
}