package com.example.automind.ui.hub

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.automind.databinding.ItemHorizontalBinding

class HorizontalAdapter :
    ListAdapter<HorizontalItem, HorizontalAdapter.HorizontalViewHolder>(HorizontalDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        val binding = ItemHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HorizontalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class HorizontalViewHolder(private val binding: ItemHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HorizontalItem) {
            binding.tvDate.text = item.date
            binding.tvTitle.text = item.title
            binding.tvContent.text = item.content
        }
    }

    class HorizontalDiffCallback : DiffUtil.ItemCallback<HorizontalItem>() {
        override fun areItemsTheSame(oldItem: HorizontalItem, newItem: HorizontalItem): Boolean {
            // If items have unique IDs, compare them here.
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: HorizontalItem, newItem: HorizontalItem): Boolean {
            return oldItem == newItem
        }
    }
}



data class HorizontalItem(
    val date: String,
    val title: String,
    val content: String
)

