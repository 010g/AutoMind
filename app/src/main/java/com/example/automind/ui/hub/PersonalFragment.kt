package com.example.automind.ui.hub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.automind.databinding.FragmentPersonalBinding

class PersonalFragment : Fragment() {

    private var _binding: FragmentPersonalBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalBinding.inflate(inflater, container, false)

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
        )

        categoryAdapter.data = categories
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
