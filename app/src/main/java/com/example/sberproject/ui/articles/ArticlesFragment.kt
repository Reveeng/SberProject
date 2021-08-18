package com.example.sberproject.ui.articles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sberproject.R
import com.example.sberproject.Util
import com.example.sberproject.databinding.FragmentArticlesBinding
import com.example.sberproject.view.fragments.BrowserFragment

class ArticlesFragment : Fragment() {
    private var _binding: FragmentArticlesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ArticlesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ArticlesViewModel(Util.articles) as T
//                return ArticlesViewModel(RetrofitClient.ARTICLES_SERVICE) as T
            }
        }).get(ArticlesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticlesBinding.inflate(inflater, container, false)
        binding.accButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_account)
        }
        binding.setButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_setting)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.articles.observe(viewLifecycleOwner, { articles ->
            binding.recyclerView.adapter = ArticleAdapter(articles) { articleOnClick(it) }
        })
    }

    private fun articleOnClick(article: Article) {
        val bundle = Bundle().apply {
            putString(BrowserFragment.PAGE_URL, article.url)
        }
        findNavController().navigate(R.id.navigation_browser, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}