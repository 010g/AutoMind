package com.example.automind.ui.hub

import android.annotation.SuppressLint
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
import com.example.automind.databinding.FragmentHubBinding
import com.google.android.material.tabs.TabLayout

class HubFragment : Fragment() {
    private var _binding: FragmentHubBinding? = null
    private var items: MutableList<HorizontalItem> = mutableListOf()
    private val binding get() = _binding!!
    private lateinit var viewModel: CategoryViewModel
    private lateinit var horizontalAdapter: HorizontalAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHubBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Work"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Ideas"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Personal"))

        horizontalAdapter = HorizontalAdapter{position ->
            if (position < items.size) {
                val updatedList = items.toMutableList()
                updatedList.removeAt(position)
                items = updatedList // Update the reference to the current list.
                Log.d("!!horizonAdapter remove: position", position.toString())
                Log.d("!!horizonAdapter remove: remaining list", items.toString())
                horizontalAdapter.submitList(items) // Update adapter with the new list.
                horizontalAdapter.notifyDataSetChanged()
            } else {
                Log.w("HubFragment", "Invalid item position: $position")
            }
        }
        binding.horizontalRecyclerView.adapter = horizontalAdapter
        binding.horizontalRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)



        // Initialize the items list
        items = mutableListOf(
            HorizontalItem("01-01-2023", "Title 1", "Content 1", isSelected = false),
            HorizontalItem("02-01-2023", "Title 2", "Content 2", isSelected = false),
            HorizontalItem("03-01-2023", "Title 3", "Content 3", isSelected = false),
            HorizontalItem("04-01-2023", "Title 4", "Content 4", isSelected = false),
            HorizontalItem("05-01-2023", "Title 5", "Content 5", isSelected = false)
        )

        horizontalAdapter.submitList(items)



        // Load WorkFragment initially
        replaceFragment(WorkFragment())

        // Setup TabLayout
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val tag = when(tab?.position) {
                    0 -> "Work"
                    1 -> "Ideas"
                    2 -> "Personal"
                    else -> return
                }
                viewModel.filterDataByTag(tag)
                when(tab?.position){
                    0 -> replaceFragment(WorkFragment())
                    1 -> replaceFragment(IdeasFragment())
                    2 -> replaceFragment(PersonalFragment())
                }
            }


            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Optional: handle tab unselected
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Optional: handle tab reselected
            }
        })

        // Navigate to RecordFragment on FloatingActionButton Click
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_hubFragment_to_recordFragment)
        }
    }



    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}