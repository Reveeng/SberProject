package com.example.sberproject.ui.lenta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sberproject.databinding.FragmentLentaBinding
import com.example.sberproject.R

class LentaFragment : Fragment() {

    private var _binding: FragmentLentaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle =  Bundle()
        _binding = FragmentLentaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.accButton.setOnClickListener{
            findNavController().navigate(R.id.navigation_account)

        }
        binding.setButton.setOnClickListener{
            findNavController().navigate(R.id.navigation_setting)
        }
        binding.stat1.setOnClickListener{
            bundle.putString("stat","https://www.ecodao.ru/what-is-ecology-adout/")
            binding.stat1.setBackgroundResource(R.drawable.news_border_pressed)
            findNavController().navigate(R.id.navigation_browser, bundle)
        }
        binding.stat2.setOnClickListener{
            bundle.putString("stat","https://trends.rbc.ru/trends/green/5d696a8c9a7947741b7e954d")
            findNavController().navigate(R.id.navigation_browser, bundle)
        }
        binding.stat3.setOnClickListener{
            bundle.putString("stat","https://nplus1.ru/material/2018/03/22/landfill-gases")
            findNavController().navigate(R.id.navigation_browser, bundle)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}