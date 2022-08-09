package com.example.sberproject.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sberproject.*
import com.example.sberproject.databinding.FragmentFirstInstructionBinding
import com.example.sberproject.ui.articles.InstructionCallback
import com.example.sberproject.ui.map.MapsFragment
import kotlinx.coroutines.runBlocking

class FirstInstructionFragment : Fragment(), InstructionCallback {
    companion object {
        const val BARCODE = "barcode"
        const val TRASH_TYPE = "trash type"
    }

    private var _binding: FragmentFirstInstructionBinding? = null
    private val binding get() = _binding!!

    //    private lateinit var trashType: TrashType
    private var barcode: String? = null
    private var trashType: TrashType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(BARCODE)?.let {
            barcode = it
//            trashType = getSerializable(MapsFragment.TRASH_TYPE) as TrashType
        }
        (arguments?.getSerializable(TRASH_TYPE) as TrashType?)?.let {
            trashType = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstInstructionBinding.inflate(inflater, container, false)

//        val instruction = Instruction(
//            "",
//            TrashType.PLASTIC,
//            "http://158.101.217.50/static/do.svg",
//            "http://158.101.217.50/static/sdacha.svg",
//            ""
//        )
        runBlocking {
            barcode?.let {
                val instruction = RetrofitClient.INSTRUCTION_API.getInstruction(it)
                if (instruction.trashType == TrashType.OTHER) {
                    findNavController().navigate(R.id.undefinedTrashTypeFragment)
                }
                binding.slideShow.adapter =
                    InstructionAdapter(this@FirstInstructionFragment, instruction)
                binding.indicator.setViewPager(binding.slideShow)
                binding.buildRouteButton.setOnClickListener {
                    findNavController().navigate(
                        R.id.navigation_maps,
                        bundleOf(MapsFragment.TRASH_TYPE to instruction.trashType)
                    )
                }
            }
            trashType?.let { tt ->
                if (!Util.trashTypesForInstruction.contains(tt)) {
                    findNavController().navigate(
                        R.id.navigation_maps,
                        bundleOf(MapsFragment.TRASH_TYPE to tt)
                    )
                }
                else{
                    val instruction =
                        RetrofitClient.INSTRUCTION_API.getInstructionWithoutBarcode(tt.toString())
                    binding.slideShow.adapter =
                        InstructionAdapter(this@FirstInstructionFragment, instruction)
                    binding.indicator.setViewPager(binding.slideShow)
                    binding.buildRouteButton.setOnClickListener {
                        findNavController().navigate(
                            R.id.navigation_maps,
                            bundleOf(MapsFragment.TRASH_TYPE to tt)
                        )
                    }
                }
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivityCallback?)?.setActionBarTitle("Инструкция")
    }

    override fun makeButtonVisible() {
        binding.buildRouteButton.visibility = View.VISIBLE
    }
}