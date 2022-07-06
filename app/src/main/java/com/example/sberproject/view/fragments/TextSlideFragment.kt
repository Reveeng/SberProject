package com.example.sberproject.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sberproject.R
import com.example.sberproject.databinding.FragmentTextSlideBinding

class TextSlideFragment : Fragment() {
    private var _binding: FragmentTextSlideBinding? = null
    private val binding get() = _binding!!
    private lateinit var text: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(INSTRUCTION_TEXT)?.let {
            text = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTextSlideBinding.inflate(inflater, container, false)
        binding.text.text = text
        return binding.root
    }

    companion object {
        const val INSTRUCTION_TEXT = "instruction text"

        @JvmStatic
        fun newInstance(text: String) =
            TextSlideFragment().apply {
                arguments = Bundle().apply {
                    putString(INSTRUCTION_TEXT, text)
                }
            }
    }
}