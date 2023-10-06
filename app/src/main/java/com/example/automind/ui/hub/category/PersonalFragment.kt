package com.example.automind.ui.hub.category

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.automind.databinding.FragmentPersonalBinding

class PersonalFragment : Fragment() {

    private var _binding: FragmentPersonalBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CategoryViewModel

    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        categoryAdapter = CategoryAdapter()
        binding.recyclerView.adapter = categoryAdapter

        // Initialize ViewModel
        viewModel = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)

        // Observe the categories LiveData
        viewModel.personals.observe(viewLifecycleOwner) {
            Log.d("personals observed!", viewModel.personals.value.toString())
            categoryAdapter.submitList(it as MutableList<CategoryItem>?)
            //categoryAdapter.notifyDataSetChanged()
        }

        // Filter data based on tag when fragment is created
        viewModel.filterDataByTag("Personal")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}