package com.example.automind.ui.hub.search

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.automind.R
import com.example.automind.data.AppDatabase
import com.example.automind.data.Repository
import com.example.automind.databinding.FragmentSearchBinding
import com.example.automind.ui.hub.HubViewModel
import com.example.automind.ui.record.RecordViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var hubViewModel: HubViewModel
    private lateinit var recordViewModel: RecordViewModel
    private lateinit var searchAdapter: SearchAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireActivity().application)
        val repository = Repository(database.noteDao(), database.settingsDao())

        viewModel = ViewModelProvider(requireActivity(), SearchViewModelFactory(requireActivity().application, repository)).get(SearchViewModel::class.java)
        hubViewModel =  ViewModelProvider(requireActivity()).get(HubViewModel::class.java)
        recordViewModel = ViewModelProvider(requireActivity()).get(RecordViewModel::class.java)

        searchAdapter = SearchAdapter(
            recordViewModel,
            findNavController(),
            R.id.navigation_detail,
            itemListener = { noteId, recordVM, navC, desId ->
                hubViewModel.navigateToDetailFragmentById(noteId, recordVM, navC, desId)
            }
    )

        binding.searchResultsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.searchResultsRecyclerView.adapter = searchAdapter

        // Listening for query changes in the SearchView
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle when the user submits the query.
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle real-time query changes.
                newText?.let {
                    viewModel.searchNotesByTitle(it)
                }
                return true
            }
        })



        // Change search icon color
        val searchIcon = binding.searchBar.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(resources.getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP)

        // Change query hint and query text color
        binding.searchBar.queryHint = "Search titles..."
        val searchTextView = binding.searchBar.findViewById<AutoCompleteTextView>(androidx.appcompat.R.id.search_src_text)
        searchTextView.setTextColor(resources.getColor(android.R.color.white))
        searchTextView.setHintTextColor(resources.getColor(android.R.color.white))




        val query = arguments?.getString("query")
        viewModel.searchNotesByTitle(query ?: "")


        // Observer for the search results
        viewModel.searchResults.observe(viewLifecycleOwner) { notes ->
            val dateFormatter = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
            val searchItems = notes.map { note ->
                val dateStr = dateFormatter.format(Date(note.timestamp))
                SearchItem(note.id, dateStr, note.title, note.content)
            }
            searchAdapter.submitList(searchItems.reversed())
        }

    }
}
