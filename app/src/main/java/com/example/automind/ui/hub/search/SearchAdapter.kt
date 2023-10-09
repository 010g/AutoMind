package com.example.automind.ui.hub.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.automind.databinding.ItemSearchBinding
import com.example.automind.ui.record.RecordViewModel


class SearchAdapter (
    val recordViewModel: RecordViewModel,
    val navController: NavController,
    val detailFragmentId: Int,
    val itemListener: (
        Long,
        RecordViewModel,
        NavController,
        Int,
    ) -> Unit,
) :
    ListAdapter<SearchItem, SearchAdapter.SearchViewHolder>(SearchDiffCallback()) {

    class SearchViewHolder(
        val binding: ItemSearchBinding,
        val recordViewModel: RecordViewModel,
        val navController: NavController,
        val detailFragmentId: Int,
        val itemListener: (
            Long,
            RecordViewModel,
            NavController,
            Int,
        ) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchItem) {
            binding.noteDate.text = item.date
            binding.noteTitle.text = item.title
            binding.noteContent.text = item.content
            itemView.setOnClickListener{
                itemListener(
                    item.id,
                    recordViewModel,
                    navController,
                    detailFragmentId
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchBinding.inflate(layoutInflater, parent, false)
        return SearchViewHolder(
            binding,
            recordViewModel,
            navController,
            detailFragmentId,
            itemListener
        )
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

