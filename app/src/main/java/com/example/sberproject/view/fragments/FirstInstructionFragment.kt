package com.example.sberproject.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.sberproject.R
import com.example.sberproject.databinding.FragmentFirstInstructionBinding

class FirstInstructionFragment : Fragment() {
    private var _binding: FragmentFirstInstructionBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstInstructionBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // Inflate the layout for this fragment

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = Bundle()
        bundle.putBoolean("MyArg", true)

        binding.btnInFrstInstr.setOnClickListener{
            findNavController().navigate(R.id.navigation_maps, bundle)
        }


    }
}