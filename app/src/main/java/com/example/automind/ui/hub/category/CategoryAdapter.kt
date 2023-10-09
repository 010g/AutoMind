package com.example.automind.ui.hub.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.automind.databinding.ItemCategoryBinding
import com.example.automind.ui.record.RecordViewModel

data class CategoryItem(val id: Long, val date: String, val title: String, val content: String)

class CategoryAdapter(
    val recordViewModel: RecordViewModel,
    val navController: NavController,
    val detailFragmentId: Int,
    val itemListener: (
        Long,
        RecordViewModel,
        NavController,
        Int,
    ) -> Unit,
)  : ListAdapter<CategoryItem, CategoryAdapter.CategoryViewHolder>(
    CategoryDiffCallback()
) {

    inner class CategoryViewHolder(
        val binding: ItemCategoryBinding,
        val recordViewModel: RecordViewModel,
        val navController: NavController,
        val detailFragmentId: Int,
        val itemListener: (
            Long,
            RecordViewModel,
            NavController,
            Int,
        ) -> Unit,
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val index = position * 3

            currentList.getOrNull(index)?.let {
                binding.tvDate.text = it.date
                binding.tvTitle.text = it.title
                binding.tvContent.text = it.content
                val id = it.id
                binding.leftLayout.setOnClickListener {
                    itemListener(
                        id,
                        recordViewModel,
                        navController,
                        detailFragmentId
                    )
                }
            }

            currentList.getOrNull(index + 1)?.let {
                binding.tvDate2.text = it.date
                binding.tvTitle2.text = it.title
                binding.tvContent2.text = it.content
                val id = it.id
                binding.rightLayout.setOnClickListener {
                    itemListener(
                        id,
                        recordViewModel,
                        navController,
                        detailFragmentId
                    )
                }
            }

            currentList.getOrNull(index + 2)?.let {
                binding.tvDate3.text = it.date
                binding.tvTitle3.text = it.title
                binding.tvContent3.text = it.content
                val id = it.id
                binding.bottomLayout.setOnClickListener {
                    itemListener(
                        id,
                        recordViewModel,
                        navController,
                        detailFragmentId
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(
            binding,
            recordViewModel,
            navController,
            detailFragmentId,
            itemListener)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun submitList(list: MutableList<CategoryItem>?) {
        super.submitList(list?.sortedByDescending { it.id })
    }


    override fun getItemCount() = (currentList.size + 2) / 3  // 每一個 item 有三個數據模型
}

class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryItem>() {
    override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean =
        oldItem == newItem
}
