package org.havrylenko.vrgsoftapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.havrylenko.vrgsoftapp.R
import org.havrylenko.vrgsoftapp.databinding.ItemArticleBinding
import org.havrylenko.vrgsoftapp.models.Article
import org.havrylenko.vrgsoftapp.utils.DateUtil

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root)

    private val callback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.binding.apply {
            Glide.with(root)
                .load(article.urlToImage ?: R.drawable.placeholder_image) // Fallback for null URL
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

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}