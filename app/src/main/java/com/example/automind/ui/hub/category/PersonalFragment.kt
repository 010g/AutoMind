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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.automind.R
import com.example.automind.databinding.FragmentPersonalBinding
import com.example.automind.ui.hub.HubViewModel
import com.example.automind.ui.record.RecordViewModel

class PersonalFragment : Fragment() {

    private var _binding: FragmentPersonalBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CategoryViewModel
    private lateinit var hubViewModel: HubViewModel
    private lateinit var recordViewModel: RecordViewModel

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

        // Initialize ViewModel
        viewModel = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)
        hubViewModel =  ViewModelProvider(requireActivity()).get(HubViewModel::class.java)
        recordViewModel = ViewModelProvider(requireActivity()).get(RecordViewModel::class.java)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        categoryAdapter = CategoryAdapter(
            recordViewModel,
            findNavController(),
            R.id.navigation_detail,
            itemListener = { noteId, recordVM, navC, desId ->
                hubViewModel.navigateToDetailFragmentById(noteId, recordVM, navC, desId)
            }
        )
        binding.recyclerView.adapter = categoryAdapter

        // Observe the categories LiveData
        viewModel.personals.observe(viewLifecycleOwner) {observedPersonals ->
            Log.d("personals observed!", viewModel.personals.value.toString())
            // Check if the list is empty
            if (observedPersonals.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.tvNoRecords.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.tvNoRecords.visibility = View.GONE
                categoryAdapter.submitList(observedPersonals as MutableList<CategoryItem>?)
            }
        }

        // Filter data based on tag when fragment is created
        viewModel.filterDataByTag("Personal")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
