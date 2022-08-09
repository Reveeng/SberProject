package com.example.sberproject.ui.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sberproject.TrashType
import com.example.sberproject.Util
import com.example.sberproject.databinding.FragmentTrashTypesListBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class TrashTypesListFragment : Fragment() {
    private var _binding: FragmentTrashTypesListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MapsViewModel
    private val viewToNotSelectedToSelected by lazy {
        mutableMapOf<View, Pair<Drawable, Drawable>>()
    }
    private val viewToTrashType by lazy {
        mutableMapOf<View, TrashType>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(),
            MapsViewModelFactory(requireContext())
        ).get(MapsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrashTypesListBinding.inflate(inflater, container, false)
        addTrashTypesViews()
        addInvisibleViews()
        binding.trashTypesListTitle.setOnClickListener {
            val bottomSheetBehavior = BottomSheetBehavior.from(binding.root.parent as View)
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            else bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.root.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
    }

    private fun addTrashTypesViews() {
        for (i in TrashType.values().indices) {
            val name = TrashType.values()[i].toStringUI()
            val icon = Util.trashTypeToIcon[TrashType.values()[i]]!!
            val textView = TextView(requireContext())
            textView.text = name
            val b = BitmapFactory.decodeResource(resources, icon)
            val drawable = Bitmap.createBitmap(b, 0, b.height / 2, b.width, b.height / 2)
                .toDrawable(resources)
            textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
            textView.textSize = 10f
            val layout = when {
                i < 5 -> binding.trashTypes1
                i in 5..9 -> binding.trashTypes2
                else -> binding.trashTypes3
            }
            val space = Space(requireContext())
            space.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply { weight = 1f }
            val colorfulDrawable = Bitmap.createBitmap(b, 0, 0, b.width, b.height / 2)
                .toDrawable(resources)
            viewToNotSelectedToSelected[textView] = drawable to colorfulDrawable
            viewToTrashType[textView] = TrashType.values()[i]
            textView.setOnClickListener { trashTypeOnClick(it) }
            layout.addView(textView)
            if (i != 4 && i != 9) layout.addView(space)
        }
    }

    private fun addInvisibleViews() {
        for (i in 0 until TrashType.values().size % 5) {
            val invisibleImage = ImageView(requireContext())
            invisibleImage.visibility = View.INVISIBLE
            val b = Bitmap.createBitmap(79, 79, Bitmap.Config.ARGB_8888)
            invisibleImage.setImageDrawable(b.toDrawable(resources))
            binding.trashTypes3.addView(invisibleImage)
            if (i != TrashType.values().size % 5 - 1) {
                val space = Space(requireContext())
                space.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ).apply { weight = 1f }
                binding.trashTypes3.addView(space)
            }
        }
    }

    private fun trashTypeOnClick(view: View) {
        val textView = view as TextView
        viewToNotSelectedToSelected[view]?.let { (bw, colorful) ->
            viewToTrashType[view]?.let { trashType ->
                textView.compoundDrawables.forEach {
                    if (it == bw) {
                        viewModel.addTrashType(trashType)
                        textView.setCompoundDrawablesWithIntrinsicBounds(null, colorful, null, null)
                    } else if (it == colorful) {
                        viewModel.removeTrashType(trashType)
                        textView.setCompoundDrawablesWithIntrinsicBounds(null, bw, null, null)
                    }
                }
            }
        }
    }
}