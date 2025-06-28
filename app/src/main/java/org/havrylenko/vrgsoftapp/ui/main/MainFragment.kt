package org.havrylenko.vrgsoftapp.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.havrylenko.vrgsoftapp.R
import org.havrylenko.vrgsoftapp.databinding.FragmentMainBinding
import org.havrylenko.vrgsoftapp.ui.adapters.NewsAdapter
import org.havrylenko.vrgsoftapp.utils.Resource

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val mBinding get() = _binding!!

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setupSearchView()

        newsAdapter.setOnItemClickListener {
            val bundle = bundleOf("article" to it)
            view.findNavController().navigate(
                R.id.action_mainFragment_to_detailsFragment,
                bundle
            )
        }

        viewModel.newsLiveData.observe(viewLifecycleOwner) { response ->
            if (!viewModel.isCurrentlySearching()) {
                handleNewsResponse(response)
            }
        }

        viewModel.searchNewsLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    mBinding.pagProgressBar.visibility = View.GONE
                    if (response.data == null) {

                        viewModel.newsLiveData.value?.let { newsResponse ->
                            if (newsResponse is Resource.Success) {
                                newsResponse.data?.let {
                                    newsAdapter.differ.submitList(it.articles)
                                    updateTitleText(false)
                                }
                            }
                        }
                    } else {
                        response.data.let {
                            newsAdapter.differ.submitList(it.articles)
                            updateTitleText(true, it.articles.size)
                        }
                    }
                }
                is Resource.Error -> {
                    mBinding.pagProgressBar.visibility = View.GONE
                    response.message?.let {
                        Log.e("MainFragment", "Search Error: $it")
                    }
                }
                is Resource.Loading -> {
                    mBinding.pagProgressBar.visibility = View.VISIBLE
                }
                null -> {}
            }
        }
    }

    private fun setupSearchView() {
        var searchJob: Job? = null

        mBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.trim().isNotEmpty()) {
                        viewModel.searchNews(it.trim())
                    } else {
                        viewModel.clearSearch()
                    }
                }
                mBinding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Отменяем предыдущий поиск
                searchJob?.cancel()

                if (newText.isNullOrBlank()) {
                    viewModel.clearSearch()
                    return true
                }

                // Запускаем поиск с задержкой 500ms
                searchJob = lifecycleScope.launch {
                    delay(500)
                    if (newText.trim().isNotEmpty()) {
                        viewModel.searchNews(newText.trim())
                    }
                }

                return true
            }
        })

        // Обработка кнопки очистки поиска
        mBinding.searchView.setOnCloseListener {
            viewModel.clearSearch()
            false
        }
    }

    private fun handleNewsResponse(response: Resource<org.havrylenko.vrgsoftapp.models.NewsResponse>) {
        when (response) {
            is Resource.Success -> {
                mBinding.pagProgressBar.visibility = View.GONE
                response.data?.let {
                    newsAdapter.differ.submitList(it.articles)
                }
            }
            is Resource.Error -> {
                mBinding.pagProgressBar.visibility = View.GONE
                response.message?.let {
                    Log.e("MainFragment", "Error: $it")
                }
            }
            is Resource.Loading -> {
                mBinding.pagProgressBar.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTitleText(isSearching: Boolean, resultsCount: Int = 0) {
        if (isSearching) {
            mBinding.titleText.text = "Search results ($resultsCount)"
            mBinding.popularNewsText.text = "Found articles"
        } else {
            mBinding.titleText.text = "All news"
            mBinding.popularNewsText.text = "Latest news"
        }
    }

    private fun initAdapter() {
        newsAdapter = NewsAdapter()
        mBinding.newsAdapter.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}