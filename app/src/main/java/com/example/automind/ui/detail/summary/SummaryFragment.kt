package com.example.automind.ui.detail.summary

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.automind.databinding.FragmentSummaryBinding
import com.example.automind.ui.record.RecordViewModel

class SummaryFragment : Fragment() {

    private val viewModel: RecordViewModel by activityViewModels()

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.summaryText.observe(viewLifecycleOwner) { data ->
            Log.d("SummaryFragment", "Received data: $data")
            if (data != binding.etSummary.text.toString()) {
                binding.etSummary.setText(data)
            }
        }

        binding.etSummary.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                // Update the LiveData in ViewModel
                if (s?.toString() != viewModel.summaryText.value) {
                    viewModel.summaryText.postValue(s?.toString())
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
