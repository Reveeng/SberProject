package com.example.sberproject.ui.map

import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sberproject.TrashType
import com.example.sberproject.Util
import com.example.sberproject.databinding.FragmentTrashTypesListBinding
import com.example.sberproject.databinding.GridViewLayoutItemBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class TrashTypesListFragment : Fragment() {
    private var _binding: FragmentTrashTypesListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(),
            MapsViewModelFactory(requireContext())
        ).get(MapsViewModel::class.java)
    }

    private val checked by lazy {
        mutableListOf<TrashType>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrashTypesListBinding.inflate(inflater, container, false)
        binding.grid.adapter = TrashItemAdapter(
            requireContext(),
            Util.trashTypeToIcon.map { it.key }.toList()
        ) { binding, trashType ->
            if (checked.contains(trashType)) {
                changeCardView(binding, Color.WHITE, Color.BLACK, Util.trashTypeToIcon[trashType]!!)
                checked.remove(trashType)
                viewModel.removeTrashType(trashType)
            } else {
                changeCardView(
                    binding,
                    resources.getColor(Util.trashTypeToColor[trashType]!!),
                    Color.WHITE,
                    Util.trashTypeToIcon[trashType]!!
                )
                checked.add(trashType)
                viewModel.addTrashType(trashType)
            }
        }
        binding.trashTypesListTitle.setOnClickListener {
            val bottomSheetBehavior = BottomSheetBehavior.from(binding.root.parent as View)
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            else bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        return binding.root
    }

    private fun changeCardView(
        binding: GridViewLayoutItemBinding,
        backgroundColor: Int,
        textAndImageColor: Int,
        icon: Int
    ) {
        binding.trashTypeCard.setCardBackgroundColor(backgroundColor)
        binding.trashName.setTextColor(textAndImageColor)
        val unwrapped = ContextCompat.getDrawable(requireContext(), icon)
        val wrapped = DrawableCompat.wrap(unwrapped!!)
        DrawableCompat.setTint(wrapped, textAndImageColor)
        binding.trashImage.setImageDrawable(wrapped)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.root.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
    }
}