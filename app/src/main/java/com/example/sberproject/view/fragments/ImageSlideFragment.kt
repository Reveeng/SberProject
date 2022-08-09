package com.example.sberproject.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.sberproject.R
import com.example.sberproject.databinding.FragmentImageSlideBinding

class ImageSlideFragment : Fragment() {
    private lateinit var imageUrl: String
    private var _binding: FragmentImageSlideBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(IMAGE_URL)?.let{
            imageUrl = "http://158.101.217.50/image/$it"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageSlideBinding.inflate(inflater, container, false)
        Glide.with(binding.root)
            .load(imageUrl)
            .into(binding.image)
        return binding.root
    }

    companion object {
        const val IMAGE_URL = "image url"

        @JvmStatic
        fun newInstance(imageUrl: String) =
            ImageSlideFragment().apply {
                arguments = Bundle().apply {
                    putString(IMAGE_URL, imageUrl)
                }
            }
    }
}