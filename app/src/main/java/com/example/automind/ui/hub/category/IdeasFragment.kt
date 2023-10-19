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
import com.example.automind.databinding.FragmentIdeasBinding
import com.example.automind.ui.hub.HubViewModel
import com.example.automind.ui.record.RecordViewModel

class IdeasFragment : Fragment() {

    private var _binding: FragmentIdeasBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CategoryViewModel
    private lateinit var hubViewModel: HubViewModel
    private lateinit var recordViewModel: RecordViewModel

    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIdeasBinding.inflate(inflater, container, false)

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
        viewModel.ideas.observe(viewLifecycleOwner) {observedIdeas ->
            Log.d("ideas observed!", viewModel.ideas.value.toString())
            // Check if the list is empty
            if (observedIdeas.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.tvNoRecords.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.tvNoRecords.visibility = View.GONE
                val threeCategoryItemList = mutableListOf<ThreeCategoryItems>()
                for (i in 0..(observedIdeas.size - 1) / 3) {
                    val threeCategoryItems = ThreeCategoryItems(
                        observedIdeas.getOrNull(i*3),
                        observedIdeas.getOrNull(i*3+1),
                        observedIdeas.getOrNull(i*3+2)
                    )
                    threeCategoryItemList.add(threeCategoryItems)
                }
                Log.d("threeCategoryItemList", threeCategoryItemList.toString())
                categoryAdapter.submitList(threeCategoryItemList)
                categoryAdapter.notifyDataSetChanged()
            }
        }

        // Filter data based on tag when fragment is created
        viewModel.filterDataByTag("Ideas")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
