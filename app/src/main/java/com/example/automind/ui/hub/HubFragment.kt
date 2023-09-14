package com.example.automind.ui.hub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.automind.databinding.FragmentHubBinding

class HubFragment : Fragment() {

    private var _binding: FragmentHubBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val hubViewModel =
            ViewModelProvider(this).get(HubViewModel::class.java)

        _binding = FragmentHubBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHub
        hubViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}