package org.havrylenko.vrgsoftapp.ui.favourite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.havrylenko.vrgsoftapp.data.api.NewsRepository
import org.havrylenko.vrgsoftapp.models.Article
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    val favoriteArticles: MutableLiveData<List<Article>> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()

    fun getFavoriteArticles() = viewModelScope.launch(Dispatchers.IO) {
        isLoading.postValue(true)
        try {
            val articles = repository.getFavoriteArticles()
            favoriteArticles.postValue(articles)
        } catch (e: Exception) {
            favoriteArticles.postValue(emptyList())
        } finally {
            isLoading.postValue(false)
        }
    }

    fun removeFromFavorites(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteFromFavourite(article)
        getFavoriteArticles()
    }
}