package com.example.automind.ui.hub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.automind.R
import com.example.automind.databinding.FragmentHubBinding
import com.google.android.material.tabs.TabLayout

class HubFragment : Fragment() {
    private var _binding: FragmentHubBinding? = null
    private val binding get() = _binding!!
    private lateinit var horizontalAdapter: HorizontalAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHubBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Work"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Ideas"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Personal"))

        horizontalAdapter = HorizontalAdapter()
        binding.horizontalRecyclerView.adapter = horizontalAdapter


        // Provide the data to your adapter
        val items = listOf(
            HorizontalItem("01-01-2023", "Title 1", "Content 1"),
            HorizontalItem("02-01-2023", "Title 2", "Content 2"),
            HorizontalItem("03-01-2023", "Title 3", "Content 3"),
            HorizontalItem("04-01-2023", "Title 4", "Content 4"),
            HorizontalItem("05-01-2023", "Title 5", "Content 5")

        )

        horizontalAdapter.submitList(items)

        // Load WorkFragment initially
        replaceFragment(WorkFragment())

        // Setup TabLayout
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

                when (tab?.position) {
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