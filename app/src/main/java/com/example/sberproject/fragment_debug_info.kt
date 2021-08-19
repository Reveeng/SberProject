package com.example.sberproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class fragment_debug_info : Fragment() {

    private lateinit var container: ConstraintLayout

    companion object {
        const val BARCODE = "barcode"
        const val TRASH_TYPE = "trash type"
        const val RAW_INFO = "rawinfo"
        const val INFO = "info"
        @JvmStatic
        fun newInstance(barcode: String, rawinfo: String,info: String, trashType: String) =
            fragment_debug_info().apply {
                arguments = Bundle().apply {
                    putString(BARCODE, barcode)
                    putString(TRASH_TYPE, trashType)
                    putString(RAW_INFO,rawinfo)
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
        var barcodeT: TextView? = null
        var rawinfoT: TextView? = null
        var infoT: TextView? = null
        var trashTypeT: TextView? = null
        barcodeT = container.findViewById(R.id.barcode)
        rawinfoT = container.findViewById(R.id.rawInfo)
        infoT = container.findViewById(R.id.decodeInfo)
        trashTypeT = container.findViewById(R.id.trashtype)

        arguments?.getString(BARCODE)?.let {
            barcodeT.text = it
        }
        arguments?.getString(RAW_INFO)?.let {
            rawinfoT.text = it
        }
        arguments?.getString(INFO)?.let {
            infoT.text = it
        }
        arguments?.getString(TRASH_TYPE)?.let {
            trashTypeT.text = it
        }
    }


}