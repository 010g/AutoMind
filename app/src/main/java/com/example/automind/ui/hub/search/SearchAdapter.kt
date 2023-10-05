package com.example.automind.ui.hub.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.automind.databinding.ItemSearchBinding



class SearchAdapter (val listener: (SearchItem) -> Unit) :
    ListAdapter<SearchItem, SearchAdapter.SearchViewHolder>(SearchDiffCallback()) {

    class SearchViewHolder(private val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchItem) {
            binding.noteDate.text = item.date
            binding.noteTitle.text = item.title
            binding.noteContent.text = item.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchBinding.inflate(layoutInflater, parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class SearchDiffCallback : DiffUtil.ItemCallback<SearchItem>() {
    override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
        return oldItem == newItem
    }
}


data class SearchItem(
    val id: Long,
    val date: String,
    val title: String,
    val content: String
)

