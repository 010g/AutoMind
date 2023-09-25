package com.example.automind.ui.hub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.automind.databinding.FragmentIdeasBinding

class IdeasFragment : Fragment() {

    private var _binding: FragmentIdeasBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIdeasBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryAdapter = CategoryAdapter()
        binding.recyclerView.adapter = categoryAdapter

        // Provide data to your adapter
        val categories = listOf(
            DataModel("01-01-2023", "Title 1", "Content 1"),
            DataModel("02-01-2023", "Title 2", "Content 2"),
            DataModel("03-01-2023", "Title 3", "Content 3"),
            DataModel("04-01-2023", "Title 4", "Content 4"),
            DataModel("05-01-2023", "Title 5", "Content 5"),
            DataModel("06-01-2023", "Title 6", "Content 6"),
            DataModel("07-01-2023", "Title 7", "Content 7"),
            DataModel("08-01-2023", "Title 8", "Content 8"),
            DataModel("09-01-2023", "Title 9", "Content 9"),
            DataModel("10-01-2023", "Title 10", "Content 10"),
            DataModel("11-01-2023", "Title 11", "Content 11"),
        )

        categoryAdapter.data = categories
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
