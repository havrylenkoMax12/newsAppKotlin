package org.havrylenko.vrgsoftapp.ui.category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.havrylenko.vrgsoftapp.R
import org.havrylenko.vrgsoftapp.databinding.FragmentCategoryBinding
import org.havrylenko.vrgsoftapp.models.Category
import org.havrylenko.vrgsoftapp.ui.adapters.CategoryAdapter
import org.havrylenko.vrgsoftapp.ui.adapters.NewsAdapter
import org.havrylenko.vrgsoftapp.utils.Resource

@AndroidEntryPoint
class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val mBinding get() = _binding!!

    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupObservers()
        setupCategories()
    }

    private fun setupRecyclerViews() {

        categoryAdapter = CategoryAdapter()
        mBinding.recyclerViewCategories.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        newsAdapter = NewsAdapter()
        mBinding.recyclerViewNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        categoryAdapter.setOnItemClickListener { category ->
            viewModel.getNewsByCategory(category.id)
        }


        newsAdapter.setOnItemClickListener { article ->
            val bundle = bundleOf("article" to article)
            view?.findNavController()?.navigate(
                R.id.action_categoryFragment_to_detailsFragment,
                bundle
            )
        }
    }

    private fun setupObservers() {
        viewModel.categoryNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        response.message.let {
                            Log.e("MainFragment", "Search Error: $it")
                        }
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun setupCategories() {
        val categories = listOf(
            Category("general", "General", true),
            Category("business", "Business", false),
            Category("entertainment", "Entertainment", false),
            Category("health", "Health", false),
            Category("science", "Science", false),
            Category("sports", "Sports", false),
            Category("technology", "Technology", false)
        )

        categoryAdapter.differ.submitList(categories)

        // Load general category by default
        viewModel.getNewsByCategory("general")
    }

    private fun showProgressBar() {
        mBinding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        mBinding.progressBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}