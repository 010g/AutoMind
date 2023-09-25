package com.example.automind.ui.hub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.automind.databinding.FragmentWorkBinding

class WorkFragment : Fragment() {

    private var _binding: FragmentWorkBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set LayoutManager to RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        categoryAdapter = CategoryAdapter()
        binding.recyclerView.adapter = categoryAdapter

        // Provide data to your adapter
        val categories = listOf(
            DataModel("01-01-2023", "Title 1", "Content 1"),
            DataModel("02-01-2023", "Title 2", "Content 2"),
            DataModel("03-01-2023", "Title 3", "Content 3"),
        )

        categoryAdapter.data = categories
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class DataModel(
    val date: String,
    val title: String,
    val content: String
)
