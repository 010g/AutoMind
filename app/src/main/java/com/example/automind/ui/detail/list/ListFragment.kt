package com.example.automind.ui.detail.list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.automind.databinding.FragmentListBinding
import com.example.automind.ui.record.RecordViewModel

class ListFragment : Fragment() {

    private val viewModel: RecordViewModel by activityViewModels()

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.listText.observe(viewLifecycleOwner) { data ->
            Log.d("ListFragment", "Received data: $data")
            if (data != binding.etList.text.toString()) {
                binding.etList.setText(data)
            }
        }

        binding.etList.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                // Update the LiveData in ViewModel
                if (s?.toString() != viewModel.listText.value) {
                    viewModel.listText.postValue(s?.toString())
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

