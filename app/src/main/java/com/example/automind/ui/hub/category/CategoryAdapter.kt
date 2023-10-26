    package com.example.automind.ui.hub.category

    import android.annotation.SuppressLint
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.ViewGroup
    import androidx.navigation.NavController
    import androidx.recyclerview.widget.DiffUtil
    import androidx.recyclerview.widget.ListAdapter
    import androidx.recyclerview.widget.RecyclerView
    import com.example.automind.databinding.ItemCategoryBinding
    import com.example.automind.ui.record.RecordViewModel

    data class CategoryItem(val id: Long, val date: String, val title: String, val content: String)
    data class ThreeCategoryItems(val categoryItem0: CategoryItem?, val categoryItem1: CategoryItem?, val categoryItem2: CategoryItem?)

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
    )  : ListAdapter<ThreeCategoryItems, CategoryAdapter.CategoryViewHolder>(
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
            @SuppressLint("SetTextI18n")
            fun bind(position: Int) {
                Log.d("getItem(position)", getItem(position).toString())
                if (getItem(position).categoryItem0 == null){
                    binding.tvDate.text = "YYYY.MM.DD"
                    binding.tvTitle.text = "Title"
                    binding.tvContent.text = "content"
                }
                getItem(position).categoryItem0?.let {
                    Log.d( "categoryItem0 title", it.title)
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

                if (getItem(position).categoryItem1 == null){
                    binding.tvDate2.text = "YYYY.MM.DD"
                    binding.tvTitle2.text = "Title"
                    binding.tvContent2.text = "content"
                }
                getItem(position).categoryItem1?.let {
                    Log.d( "categoryItem1 title", it.title)
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

                if (getItem(position).categoryItem2 == null){
                    binding.tvDate3.text = "YYYY.MM.DD"
                    binding.tvTitle3.text = "Title"
                    binding.tvContent3.text = "content"
                }
                getItem(position).categoryItem2?.let {
                    Log.d( "categoryItem2 title", it.title)
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
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<ThreeCategoryItems>() {
        override fun areItemsTheSame(oldItem: ThreeCategoryItems, newItem: ThreeCategoryItems): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: ThreeCategoryItems, newItem: ThreeCategoryItems): Boolean =
            oldItem == newItem
    }
