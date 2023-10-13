package com.example.automind.ui.hub

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.automind.R
import com.example.automind.databinding.FragmentHubBinding
import com.example.automind.ui.hub.category.CategoryViewModel
import com.example.automind.ui.hub.category.IdeasFragment
import com.example.automind.ui.hub.category.PersonalFragment
import com.example.automind.ui.hub.category.WorkFragment
import com.example.automind.ui.record.RecordViewModel
import com.google.android.material.tabs.TabLayout

class HubFragment : Fragment() {
    private var _binding: FragmentHubBinding? = null
    private val binding get() = _binding!!
    private lateinit var recordViewModel: RecordViewModel
    private lateinit var viewModel: CategoryViewModel
    private lateinit var hubViewModel: HubViewModel
    private lateinit var horizontalAdapter: HorizontalAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHubBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordViewModel = ViewModelProvider(requireActivity()).get(RecordViewModel::class.java)
        viewModel = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Work"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Ideas"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Personal"))

        horizontalAdapter = HorizontalAdapter(
            recordViewModel,
            findNavController(),
            R.id.navigation_detail,
            btnHeartListener = { item ->
                hubViewModel.updateIsLikeForId(item.id, item.isSelected).invokeOnCompletion {
                    hubViewModel.filterDataByIsLike()
                }
            },
            itemListener = { noteId, recordVM, navC, desId ->
                hubViewModel.navigateToDetailFragmentById(noteId, recordVM, navC, desId)
            }
        )
        binding.horizontalRecyclerView.adapter = horizontalAdapter
        binding.horizontalRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Initialize ViewModel
        hubViewModel = ViewModelProvider(requireActivity()).get(HubViewModel::class.java)

        // Observe LiveData to update note counts
        hubViewModel.workCount.observe(viewLifecycleOwner) { count ->
            binding.numWork.text = count.toString()
        }
        hubViewModel.ideasCount.observe(viewLifecycleOwner) { count ->
            binding.numIdeas.text = count.toString()
        }
        hubViewModel.personalCount.observe(viewLifecycleOwner) { count ->
            binding.numPersonal.text = count.toString()
        }
        // Call this to initially load the counts
        hubViewModel.refreshNoteCounts()


        //search bar
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    val bundle = Bundle()
                    bundle.putString("query", query)

                    // Navigate to the SearchFragment using bundle
                    findNavController().navigate(R.id.searchFragment, bundle)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle real-time changes if necessary. For now, we do nothing here.
                return true
            }
        })



        // Observe the categories LiveData
        hubViewModel.isLikes.observe(viewLifecycleOwner) {
            Log.d("isLikes observed!", hubViewModel.isLikes.value.toString())
            horizontalAdapter.submitList(it as MutableList<HorizontalItem>?)
            horizontalAdapter.notifyDataSetChanged()

            // Determine visibility based on the list size
            if (it.isNullOrEmpty()) {
                binding.horizontalRecyclerView.visibility = View.GONE
                binding.tvNoFav.visibility = View.VISIBLE
            } else {
                binding.horizontalRecyclerView.visibility = View.VISIBLE
                binding.tvNoFav.visibility = View.GONE
            }
        }

        // Filter data based on tag when fragment is created
        hubViewModel.filterDataByIsLike()




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