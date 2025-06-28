package org.havrylenko.vrgsoftapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.havrylenko.vrgsoftapp.models.Article

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles ORDER BY id DESC")
    suspend fun getAllArticles(): List<Article>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article)

    @Delete
    suspend fun delete(article: Article)

    @Query("SELECT * FROM articles WHERE url = :url LIMIT 1")
    suspend fun getArticleByUrl(url: String): Article?

    @Query("DELETE FROM articles WHERE url = :url")
    suspend fun deleteByUrl(url: String)
}