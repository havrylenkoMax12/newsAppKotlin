package org.havrylenko.vrgsoftapp.ui.category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.havrylenko.vrgsoftapp.data.api.NewsRepository
import org.havrylenko.vrgsoftapp.models.NewsResponse
import org.havrylenko.vrgsoftapp.utils.Resource
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    val categoryNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var categoryNewsPage = 1
    private var categoryNewsResponse: NewsResponse? = null
    private var currentCategory = "general"

    init {
        getNewsByCategory("general")
    }

    fun getNewsByCategory(category: String) = viewModelScope.launch {
        safeGetNewsByCategory(category)
    }

    private suspend fun safeGetNewsByCategory(category: String) {
        categoryNews.postValue(Resource.Loading())
        try {
            if (currentCategory != category) {
                categoryNewsPage = 1
                categoryNewsResponse = null
                currentCategory = category
            }

            val response = newsRepository.getNewsByCategory(
                category = category,
                pageNumber = categoryNewsPage
            )
            categoryNews.postValue(handleCategoryNewsResponse(response))
        } catch (t: Throwable) {
            categoryNews.postValue(Resource.Error("Network Failure"))
        }
    }

    private fun handleCategoryNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                categoryNewsPage++
                if (categoryNewsResponse == null) {
                    categoryNewsResponse = resultResponse
                } else {
                    val oldArticles = categoryNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(categoryNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}