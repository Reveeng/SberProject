package com.example.sberproject.ui.articles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.sberproject.MainActivityCallback
import com.example.sberproject.R
import com.example.sberproject.RetrofitClient
import com.example.sberproject.databinding.FragmentArticlesBinding
import com.example.sberproject.view.fragments.BrowserFragment

class ArticlesFragment : Fragment() {
    private var _binding: FragmentArticlesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ArticlesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                //return ArticlesViewModel(RetrofitClient.ARTICLES_SERVICE) as T
                return ArticlesViewModel() as T
            }
        }).get(ArticlesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticlesBinding.inflate(inflater, container, false)
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1))
                    viewModel.loadMoreArticles()
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.articles.observe(viewLifecycleOwner) { articles ->
            if (binding.recyclerView.adapter == null)
                binding.recyclerView.adapter = ArticleAdapter(articles) { articleOnClick(it) }
            binding.recyclerView.adapter?.notifyItemRangeInserted(articles.size - 10, 10)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivityCallback?)?.setActionBarTitle("Новости")
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