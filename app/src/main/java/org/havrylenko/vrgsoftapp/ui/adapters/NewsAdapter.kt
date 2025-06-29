package org.havrylenko.vrgsoftapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.havrylenko.vrgsoftapp.R
import org.havrylenko.vrgsoftapp.databinding.ItemArticleBinding
import org.havrylenko.vrgsoftapp.databinding.ItemArticleFavouriteBinding
import org.havrylenko.vrgsoftapp.models.Article
import org.havrylenko.vrgsoftapp.utils.DateUtil

class NewsAdapter(private val showDeleteButton: Boolean = false) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_NORMAL = 0
        private const val TYPE_FAVORITE = 1
    }

    inner class NewsViewHolder(val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class FavoriteNewsViewHolder(val binding: ItemArticleFavouriteBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val callback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun getItemViewType(position: Int): Int {
        return if (showDeleteButton) TYPE_FAVORITE else TYPE_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_FAVORITE -> {
                val binding = ItemArticleFavouriteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                FavoriteNewsViewHolder(binding)
            }

            else -> {
                val binding =
                    ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                NewsViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val article = differ.currentList[position]

        when (holder) {
            is NewsViewHolder -> {
                holder.binding.apply {
                    Glide.with(root)
                        .load(article.urlToImage ?: R.drawable.placeholder_image)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(articleImage)
                    articleImage.clipToOutline = true
                    articleTitle.text = article.title ?: "No Title"
                    articleDate.text = DateUtil.formatDisplayDate(article.publishedAt)
                }

                holder.itemView.setOnClickListener {
                    onItemClickListener?.let { it(article) }
                }
            }

            is FavoriteNewsViewHolder -> {
                holder.binding.apply {
                    Glide.with(root)
                        .load(article.urlToImage ?: R.drawable.placeholder_image)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(articleImage)
                    articleImage.clipToOutline = true
                    articleTitle.text = article.title ?: "No Title"
                    articleDate.text = DateUtil.formatDisplayDate(article.publishedAt)

                    deleteButton.setOnClickListener {
                        onDeleteClickListener?.let { it(article) }
                    }
                }

                holder.itemView.setOnClickListener {
                    onItemClickListener?.let { it(article) }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Article) -> Unit)? = null
    private var onDeleteClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnDeleteClickListener(listener: (Article) -> Unit) {
        onDeleteClickListener = listener
    }
}