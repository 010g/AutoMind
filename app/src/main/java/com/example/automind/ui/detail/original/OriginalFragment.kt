package com.example.automind.ui.detail.original

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.automind.databinding.FragmentOriginalBinding
import com.example.automind.ui.record.RecordViewModel

class OriginalFragment : Fragment() {

    private val viewModel: RecordViewModel by activityViewModels()

    private var _binding: FragmentOriginalBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOriginalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("OriginalFragment", "Setting up observer")
        Log.d("OriginalFragment", "ViewModel instance: $viewModel")

        viewModel.originalText.observe(viewLifecycleOwner) { data ->
            Log.d("Fragment", "Received data: $data")
            binding.editText.setText(data)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
