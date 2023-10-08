package com.example.automind.ui.hub

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.automind.R
import com.example.automind.databinding.ItemHorizontalBinding
import com.example.automind.ui.record.RecordViewModel

class HorizontalAdapter(
    val recordViewModel: RecordViewModel,
    val navController: NavController,
    val detailFragmentId: Int,
    val btnHeartListener: (HorizontalItem) -> Unit,
    val itemListener: (
        Long,
        RecordViewModel,
        NavController,
        Int,
    ) -> Unit,
) :
    ListAdapter<HorizontalItem, HorizontalAdapter.HorizontalViewHolder>(HorizontalDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        val binding = ItemHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HorizontalViewHolder(
            binding,
            recordViewModel,
            navController,
            detailFragmentId,
            btnHeartListener,
            itemListener,
        )
    }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.btnHeart.setImageResource(
            if (!item.isSelected) R.drawable.ic_heart
            else R.drawable.ic_heart_full
        )
        holder.bind(item)
    }

    override fun submitList(list: MutableList<HorizontalItem>?) {
        super.submitList(list?.sortedByDescending { it.id })
    }


    class HorizontalViewHolder(
        val binding: ItemHorizontalBinding,
        val recordViewModel: RecordViewModel,
        val navController: NavController,
        val detailFragmentId: Int,
        val btnHeartListener: (HorizontalItem) -> Unit,
        val itemListener: (
            Long,
            RecordViewModel,
            NavController,
            Int,
        ) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HorizontalItem) {
            binding.tvDate.text = item.date
            binding.tvTitle.text = item.title
            binding.tvContent.text = item.content
            binding.btnHeart.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    btnHeartListener(item)
                }
            }
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

    class HorizontalDiffCallback : DiffUtil.ItemCallback<HorizontalItem>() {
        override fun areItemsTheSame(oldItem: HorizontalItem, newItem: HorizontalItem): Boolean {
            // Compare the items based on their content if they don't have unique IDs
            return oldItem.date == newItem.date &&
                    oldItem.title == newItem.title &&
                    oldItem.content == newItem.content
        }

        override fun areContentsTheSame(oldItem: HorizontalItem, newItem: HorizontalItem): Boolean {
            return oldItem.date == newItem.date &&
                    oldItem.title == newItem.title &&
                    oldItem.content == newItem.content &&
                    oldItem.isSelected == newItem.isSelected // Include isSelected in comparison
        }

    }
}



data class HorizontalItem(
    val id: Long,
    val date: String,
    val title: String,
    val content: String,
    var isSelected: Boolean = false
)

