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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.havrylenko.vrgsoftapp.R
import org.havrylenko.vrgsoftapp.databinding.FragmentFavouriteBinding
import org.havrylenko.vrgsoftapp.models.Article
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

        setupBackButton()
        setupRecyclerView()
        setupSwipeToDelete()
        setupObservers()

        viewModel.getFavoriteArticles()
    }

    private fun setupBackButton() {
        mBinding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {

        newsAdapter = NewsAdapter(showDeleteButton = true)
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

        newsAdapter.setOnDeleteClickListener { article ->
            removeArticleWithUndo(article)
        }
    }

    private fun removeArticleWithUndo(article: Article) {
        viewModel.removeFromFavorites(article)

        Snackbar.make(mBinding.root, "Article removed from favorites", Snackbar.LENGTH_LONG)
            .setAction("UNDO") {
                viewModel.addToFavorites(article)
            }
            .show()
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                removeArticleWithUndo(article)
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(mBinding.recyclerViewFavorites)
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