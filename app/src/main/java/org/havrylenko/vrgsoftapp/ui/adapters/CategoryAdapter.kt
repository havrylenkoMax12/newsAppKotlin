package org.havrylenko.vrgsoftapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.havrylenko.vrgsoftapp.R
import org.havrylenko.vrgsoftapp.databinding.ItemCategoryBinding
import org.havrylenko.vrgsoftapp.models.Category

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    private val callback = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = differ.currentList[position]
        holder.binding.apply {
            categoryName.text = category.name

            if (category.isSelected) {
                categoryCard.setCardBackgroundColor(
                    ContextCompat.getColor(root.context, R.color.secondaryBlue)
                )
                categoryName.setTextColor(
                    ContextCompat.getColor(root.context, android.R.color.white)
                )
            } else {
                categoryCard.setCardBackgroundColor(
                    ContextCompat.getColor(root.context, R.color.primaryBlue)
                )
                categoryName.setTextColor(
                    ContextCompat.getColor(root.context, R.color.black)
                )
            }
        }

        holder.itemView.setOnClickListener {

            val currentList = differ.currentList.toMutableList()
            currentList.forEach { it.isSelected = false }
            currentList[position].isSelected = true
            differ.submitList(currentList)

            onItemClickListener?.let { it(category) }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Category) -> Unit)? = null

    fun setOnItemClickListener(listener: (Category) -> Unit) {
        onItemClickListener = listener
    }
}