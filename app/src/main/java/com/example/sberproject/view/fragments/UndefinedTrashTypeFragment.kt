package com.example.sberproject.view.fragments

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
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sberproject.R
import com.example.sberproject.TrashType
import com.example.sberproject.Util
import com.example.sberproject.databinding.FragmentUndefinedTrashTypeBinding

class UndefinedTrashTypeFragment : Fragment() {
    private var _binding: FragmentUndefinedTrashTypeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UndefinedTrashTypeViewModel
    private val viewToNotSelectedToSelected by lazy {
        mutableMapOf<View, Pair<Drawable, Drawable>>()
    }
    private val viewToTrashType by lazy {
        mutableMapOf<View, TrashType>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UndefinedTrashTypeViewModel() as T
            }
        }).get(UndefinedTrashTypeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUndefinedTrashTypeBinding.inflate(inflater, container, false)
        addTrashTypesViews()
        addInvisibleViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.trashType.observe(viewLifecycleOwner, { trashType ->
            binding.buildRouteButton.text =
                if (Util.trashTypesForInstruction.contains(trashType)) "Инструкция" else "Маршрут"
            binding.buildRouteButton.visibility = View.VISIBLE
            binding.buildRouteButton.setOnClickListener {
                findNavController().navigate(
                    R.id.navigation_first_instruction,
                    bundleOf(FirstInstructionFragment.TRASH_TYPE to trashType)
//                    R.id.navigation_maps,
//                    bundleOf(MapsFragment.TRASH_TYPE to trashType)
                )
            }
        })
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
//                        viewModel.addTrashType(trashType)
                        viewModel.setTrashType(trashType)
                        makeOtherIconsNotSelected(view)
//                        binding.trashTypes1.children.forEach { other ->
//                            if (other != view && other !is Space) {
//                                (other as TextView?)?.setCompoundDrawablesWithIntrinsicBounds(null, viewToNotSelectedToSelected[other]!!.first, null, null)
//                            }
//                        }
//                        binding.trashTypes2.children.forEach { other ->
//                            if (other != view && other !is Space) {
//                                (other as TextView?)?.setCompoundDrawablesWithIntrinsicBounds(null, viewToNotSelectedToSelected[other]!!.first, null, null)
//                            }
//                        }
//                        binding.trashTypes3.children.forEach { other ->
//                            if (other != view && other !is Space) {
//                                (other as TextView?)?.setCompoundDrawablesWithIntrinsicBounds(null, viewToNotSelectedToSelected[other]!!.first, null, null)
//                            }
//                        }
                        textView.setCompoundDrawablesWithIntrinsicBounds(null, colorful, null, null)
                    }
//                    else if (it == colorful) {
////                        viewModel.removeTrashType(trashType)
//                        textView.setCompoundDrawablesWithIntrinsicBounds(null, bw, null, null)
//                    }
                }
            }
        }
    }

    private fun makeOtherIconsNotSelected(stayingSelected: View) {
        makeOtherIconsNotSelected(binding.trashTypes1, stayingSelected)
        makeOtherIconsNotSelected(binding.trashTypes2, stayingSelected)
        makeOtherIconsNotSelected(binding.trashTypes3, stayingSelected)
    }

    private fun makeOtherIconsNotSelected(linearLayout: LinearLayout, stayingSelected: View) {
        linearLayout.children.forEach {
            if (it is TextView && it != stayingSelected)
                it.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    viewToNotSelectedToSelected[it]!!.first,
                    null,
                    null
                )
        }
    }
}