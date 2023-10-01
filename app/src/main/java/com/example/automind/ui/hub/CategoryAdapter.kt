package com.example.automind.ui.hub

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.automind.databinding.ItemCategoryBinding

data class CategoryItem(val date: String, val title: String, val content: String)

class CategoryAdapter : ListAdapter<CategoryItem, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val index = position * 3

            currentList.getOrNull(index)?.let {
                binding.tvDate.text = it.date
                binding.tvTitle.text = it.title
                binding.tvContent.text = it.content
            }

            currentList.getOrNull(index + 1)?.let {
                binding.tvDate2.text = it.date
                binding.tvTitle2.text = it.title
                binding.tvContent2.text = it.content
            }

            currentList.getOrNull(index + 2)?.let {
                binding.tvDate3.text = it.date
                binding.tvTitle3.text = it.title
                binding.tvContent3.text = it.content
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = (currentList.size + 2) / 3  // 每一個 item 有三個數據模型
}

class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryItem>() {
    override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean =
        oldItem == newItem
}
