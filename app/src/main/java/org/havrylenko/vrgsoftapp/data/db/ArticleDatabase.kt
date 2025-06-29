package org.havrylenko.vrgsoftapp.data.db

import androidx.room.RoomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import org.havrylenko.vrgsoftapp.models.Article

@Database(entities = [Article::class], version = 2, exportSchema = true)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun getArticleDao(): ArticleDao
}