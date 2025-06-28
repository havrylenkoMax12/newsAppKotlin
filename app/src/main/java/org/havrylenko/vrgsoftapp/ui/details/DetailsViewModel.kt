package org.havrylenko.vrgsoftapp.ui.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.havrylenko.vrgsoftapp.data.api.NewsRepository
import org.havrylenko.vrgsoftapp.models.Article

@HiltViewModel
class DetailsViewModel @Inject constructor(private val  repository: NewsRepository): ViewModel() {

    init {
        getSavedArticles()
    }

    fun getSavedArticles() = viewModelScope.launch(Dispatchers.IO) {
        val res = repository.getFavoriteArticles()
        Log.d("DetailsViewModel", "DB size: ${res.size}")
        repository.getFavoriteArticles()
    }

    fun saveFavoriteArticle(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        repository.addToFavourite(article = article)
    }
}