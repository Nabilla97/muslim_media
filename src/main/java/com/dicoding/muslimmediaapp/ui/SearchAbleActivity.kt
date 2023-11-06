package com.dicoding.muslimmediaapp.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.muslimmediaapp.NewsViewModel
import com.dicoding.muslimmediaapp.R
import com.dicoding.muslimmediaapp.adapter.NewsAdapter
import com.dicoding.muslimmediaapp.databinding.ActivitySearchAbleBinding

class SearchAbleActivity : AppCompatActivity() {
    private var _binding: ActivitySearchAbleBinding? = null
    private val binding get() = _binding as ActivitySearchAbleBinding

    private var _searchViewModel: NewsViewModel? = null
    private val searchViewModel get() = _searchViewModel as NewsViewModel

    private var querySearch: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchAbleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _searchViewModel = ViewModelProvider(this)[NewsViewModel::class.java]
        binding.loadingView.root.visibility = View.VISIBLE

        handleIntent(intent)

        searchViewModel.searchNews.observe(this) {
            binding.apply {
                if (it.articles?.size == 0){
                    tvNoNews.text = getString(R.string.no_news_text)
                    tvNoNews.visibility = View.VISIBLE
                } else {
                    rvSearchResult.apply {
                        val menuAdapter = NewsAdapter()
                        menuAdapter.setData(it.articles)
                        adapter = menuAdapter
                        layoutManager = LinearLayoutManager(this@SearchAbleActivity)
                        visibility = View.VISIBLE
                    }
                }
            }
            binding.loadingView.root.visibility = View.GONE
        }

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent){
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)
                ?.also { query ->
                    querySearch = query
                    binding.apply {
                        rvSearchResult.visibility = View.GONE
                        loadingView.root.visibility = View.VISIBLE
                        tvNoNews.visibility = View.INVISIBLE
                        searchView.setQuery("", false)
                        searchView.queryHint = query
                        searchView.clearFocus()
                    }
                   doMySearch(query)
                }
        }
    }

    private fun doMySearch(q: String){
        searchViewModel.searchNews(q)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
