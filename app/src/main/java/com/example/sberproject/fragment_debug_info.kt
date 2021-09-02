package com.example.sberproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class fragment_debug_info : Fragment() {

    private lateinit var container: ConstraintLayout

    companion object {
        const val INFO = "info"
        @JvmStatic
        fun newInstance(info: String) =
            fragment_debug_info().apply {
                arguments = Bundle().apply {
                    putString(INFO,info)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_debug_info, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        container = view as ConstraintLayout
        var infoT: TextView = container.findViewById(R.id.decodeInfo)
        arguments?.getString(INFO)?.let {
            infoT.text = it
        }
    }


}