package org.havrylenko.vrgsoftapp.ui.favourite

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
import org.havrylenko.vrgsoftapp.databinding.FragmentFavouriteBinding
import org.havrylenko.vrgsoftapp.ui.adapters.NewsAdapter

@AndroidEntryPoint
class FavouriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private val mBinding get() = _binding!!

    private val viewModel by viewModels<FavouriteViewModel>()
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()

        viewModel.getFavoriteArticles()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        mBinding.recyclerViewFavorites.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        newsAdapter.setOnItemClickListener { article ->
            val bundle = bundleOf("article" to article)
            view?.findNavController()?.navigate(
                R.id.action_favouriteFragment_to_detailsFragment,
                bundle
            )
        }
    }

    private fun setupObservers() {
        viewModel.favoriteArticles.observe(viewLifecycleOwner, Observer { articles ->
            if (articles.isEmpty()) {
                showEmptyState()
            } else {
                hideEmptyState()
                newsAdapter.differ.submitList(articles)
            }
            hideProgressBar()
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                showProgressBar()
            } else {
                hideProgressBar()
            }
        })
    }

    private fun showEmptyState() {
        mBinding.recyclerViewFavorites.visibility = View.GONE
        mBinding.emptyStateLayout.visibility = View.VISIBLE
    }

    private fun hideEmptyState() {
        mBinding.recyclerViewFavorites.visibility = View.VISIBLE
        mBinding.emptyStateLayout.visibility = View.GONE
    }

    private fun showProgressBar() {
        mBinding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        mBinding.progressBar.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavoriteArticles()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}