package com.example.automind.ui.detail.original

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.automind.databinding.FragmentOriginalBinding

class OriginalFragment : Fragment() {

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
        // Handle clicks and navigation here if required
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
