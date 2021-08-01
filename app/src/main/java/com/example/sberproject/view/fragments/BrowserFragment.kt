package com.example.sberproject.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sberproject.R
import com.example.sberproject.databinding.FragmentBrowserBinding


class BrowserFragment : Fragment() {
    companion object {
        const val PAGE_URL = "page url"
    }

    private var _binding: FragmentBrowserBinding? = null
    private val binding get() = _binding!!
    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(PAGE_URL)?.let {
            url = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrowserBinding.inflate(inflater, container, false)
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                request?.let {
                    val bundle = Bundle().apply {
                        putString(PAGE_URL, it.url.toString())
                    }
                    findNavController().navigate(R.id.navigation_browser, bundle)
                }
                return false
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webView.loadUrl(url)
    }
}