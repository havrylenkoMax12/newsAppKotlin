package org.havrylenko.vrgsoftapp.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.havrylenko.vrgsoftapp.data.api.NewsRepository
import org.havrylenko.vrgsoftapp.models.NewsResponse
import org.havrylenko.vrgsoftapp.utils.Resource
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: NewsRepository): ViewModel() {

    val newsLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchNewsLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    var newsPage = 1
    var searchPage = 1
    var currentSearchQuery: String? = null
    private var isSearching = false

    init {
        getNews("us")
    }

    private fun getNews(countryCode: String) =
        viewModelScope.launch {
            newsLiveData.postValue(Resource.Loading())
            val response = repository.getNews(countryCode = countryCode, pageNumber = newsPage)
            if (response.isSuccessful) {
                response.body().let { res ->
                    newsLiveData.postValue(Resource.Success(res))
                }
            } else {
                newsLiveData.postValue(Resource.Error(message = response.message()))
            }
        }

    fun searchNews(query: String) {
        if (query.isBlank()) {
            clearSearch()
            return
        }

        isSearching = true
        currentSearchQuery = query
        searchPage = 1

        viewModelScope.launch {
            searchNewsLiveData.postValue(Resource.Loading())
            val response = repository.getSearchNews(query = query, pageNumber = searchPage)
            if (response.isSuccessful) {
                response.body().let { res ->
                    searchNewsLiveData.postValue(Resource.Success(res))
                }
            } else {
                searchNewsLiveData.postValue(Resource.Error(message = response.message()))
            }
        }
    }

    fun clearSearch() {
        isSearching = false
        currentSearchQuery = null
        searchPage = 1
        searchNewsLiveData.postValue(Resource.Success(null))
    }

    fun isCurrentlySearching(): Boolean = isSearching

    fun refreshNews() {
        if (isSearching && !currentSearchQuery.isNullOrBlank()) {
            searchNews(currentSearchQuery!!)
        } else {
            newsPage = 1
            getNews("us")
        }
    }
}