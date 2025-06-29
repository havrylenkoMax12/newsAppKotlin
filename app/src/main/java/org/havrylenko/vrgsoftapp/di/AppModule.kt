package org.havrylenko.vrgsoftapp.di


import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.havrylenko.vrgsoftapp.data.api.NewsService
import org.havrylenko.vrgsoftapp.data.db.ArticleDao
import org.havrylenko.vrgsoftapp.data.db.ArticleDatabase
import org.havrylenko.vrgsoftapp.utils.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun baseUrl() = BASE_URL

    @Provides
    fun logging() = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    fun okHttpClient() = OkHttpClient.Builder()
        .addInterceptor(logging())
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String): NewsService =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient())
            .build()
            .create(NewsService::class.java)

    @Provides
    @Singleton
    fun provideArticleDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            ArticleDatabase::class.java,
            "article_database"
        ).addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideArticleDao(appDatabase: ArticleDatabase): ArticleDao {
        return appDatabase.getArticleDao()
    }

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE articles RENAME TO articles_old")

            database.execSQL(
                """
            CREATE TABLE articles (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                author TEXT,
                content TEXT,
                description TEXT,
                publishedAt TEXT,
                title TEXT,
                url TEXT,
                urlToImage TEXT,
                category TEXT
            )
        """.trimIndent()
            )

            database.execSQL("CREATE UNIQUE INDEX index_articles_url ON articles(url)")

            database.execSQL(
                """
            INSERT OR IGNORE INTO articles (
                id, author, content, description, publishedAt, title, url, urlToImage, category
            )
            SELECT 
                id, author, content, description, publishedAt, title, url, urlToImage, NULL AS category
            FROM articles_old
        """.trimIndent()
            )

            database.execSQL("DROP TABLE articles_old")
        }
    }
}
