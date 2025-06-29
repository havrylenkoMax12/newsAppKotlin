package org.havrylenko.vrgsoftapp.data.api

import org.havrylenko.vrgsoftapp.data.db.ArticleDao
import org.havrylenko.vrgsoftapp.models.Article
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsService: NewsService,
    private val articleDao: ArticleDao
) {
    suspend fun getNews(countryCode: String, pageNumber: Int) =
        newsService.getTopHeadlines(country = countryCode, page = pageNumber)

    suspend fun getNewsByCategory(category: String, pageNumber: Int, countryCode: String = "us") =
        newsService.getTopHeadlinesByCategory(
            country = countryCode,
            category = category,
            page = pageNumber
        )

    suspend fun getSearchNews(query: String, pageNumber: Int, sortBy: String = "publishedAt") =
        newsService.searchNews(query = query, page = pageNumber, sortBy = sortBy)

    suspend fun getSearchNewsOld(query: String, pageNumber: Int) =
        newsService.getEverything(query = query, page = pageNumber)

    suspend fun getFavoriteArticles(): List<Article> = articleDao.getAllArticles()

    suspend fun addToFavourite(article: Article) = articleDao.insert(article = article)

    suspend fun deleteFromFavourite(article: Article) = articleDao.delete(article = article)

    suspend fun isArticleFavorite(url: String): Boolean {
        return articleDao.getArticleByUrl(url) != null
    }

    suspend fun deleteFromFavouriteByUrl(url: String) = articleDao.deleteByUrl(url)
}