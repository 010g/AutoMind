package com.example.automind.ui.detail.original

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
            Log.d("OriginalFragment", "Received data: $data")
            if (data != binding.etContent.text.toString()) {
                binding.etContent.setText(data)
            }
        }

        // Set a TextWatcher on the EditText to listen for changes
        binding.etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                // After the text has changed, post the new value to the LiveData object in the ViewModel
                if (s?.toString() != viewModel.originalText.value) {
                    viewModel.originalText.postValue(s?.toString())
                }
            }
        })
    }

    fun getTextContent(): String? {
        return binding.etContent.text.toString()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
