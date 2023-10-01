package com.example.automind.ui.detail

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.automind.MainActivity
import com.example.automind.R
import com.example.automind.databinding.FragmentDetailBinding
import com.example.automind.ui.hub.CategoryViewModel
import com.example.automind.ui.record.RecordViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailFragment : Fragment() {


    // View binding instance
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RecordViewModel

    private lateinit var categoryViewModel: CategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using View Binding
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(RecordViewModel::class.java)
        categoryViewModel = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.adapter = DetailViewPagerAdapter(this)

        // Create and bind tabs to the TabLayout
        val viewPager2 = binding.viewPager
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val mainActivity = activity as MainActivity?
                val toolbarTitle = mainActivity?.findViewById<TextView>(R.id.toolbar_title)
                when (position) {
                    0 -> if (toolbarTitle != null) {
                        toolbarTitle.text = "Original"
                    }
                    1 -> if (toolbarTitle != null) {
                        toolbarTitle.text = "Summary"
                    }
                    2 -> if (toolbarTitle != null) {
                        toolbarTitle.text = "List"
                    }
                    3 -> if (toolbarTitle != null) {
                        toolbarTitle.text = "Mind Map"
                    }
                }
            }
        })

        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.customView = createTabView(false)
        }.attach()


        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.customView = createTabView(true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView = createTabView(false)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        binding.title.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun afterTextChanged(s: Editable?) {
                saveTitle()
            }
        })

        val items = arrayOf("Work", "Ideas", "Personal")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                Log.d("selectedItem in spinner.onItemSelectedListener", selectedItem)
                saveTag(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leak
    }

    private fun createTabView(isSelected: Boolean): View {
        val tabView = ImageView(requireContext())
        tabView.setImageResource(if (isSelected) R.drawable.selected_dot else R.drawable.default_dot)
        tabView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return tabView
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveTitle() {
        Log.d("latestSavedTextId in saveTag", viewModel.latestSavedTextId.value.toString())
        val title = binding.title.text.toString()
        viewModel.latestSavedTextId.value?.let {
            Log.d("saveTitle", "$it: $title")
            viewModel.updateTitleForId(
                id = it,
                title = title
            ).invokeOnCompletion {
                categoryViewModel.filterDataByTag("Work")
                categoryViewModel.filterDataByTag("Ideas")
                categoryViewModel.filterDataByTag("Personal")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveTag(selectedItem: String) {
        Log.d("latestSavedTextId in saveTag", viewModel.latestSavedTextId.value.toString())
        viewModel.latestSavedTextId.value?.let {
            Log.d("saveTag", "$it: $tag")
            viewModel.updateTagForId(
                id = it,
                tag = selectedItem
            ).invokeOnCompletion {
                categoryViewModel.filterDataByTag("Work")
                categoryViewModel.filterDataByTag("Ideas")
                categoryViewModel.filterDataByTag("Personal")
            }
        }
    }

}
