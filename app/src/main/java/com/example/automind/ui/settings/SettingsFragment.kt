package com.example.automind.ui.settings

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.automind.MainActivity
import com.example.automind.R
import com.example.automind.databinding.FragmentSettingsBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsViewModel: SettingsViewModel

    private var isSeekBarBeingTouched = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        settingsViewModel =
            ViewModelProvider(requireActivity()).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Setup Observers
        setupObservers()

        // Setup UI Elements
        setupUI()



        // Set up the input language Spinner
        setupSpinner(
            binding.spinnerInputLanguage,
            R.array.input_language_options,
            binding.tvInputLanguage, // Pass the associated TextView here
            "Input Language" // Initial text
        ) { selectedItem ->
            settingsViewModel.updateInputLanguage(selectedItem)
        }

        // Set up the output language Spinner
        setupSpinner(
            binding.spinnerOutputLanguage,
            R.array.output_language_options,
            binding.tvOutputLanguage, // Pass the associated TextView here
            "Output Language" // Initial text
        ) { selectedItem ->
            settingsViewModel.updateOutputLanguage(selectedItem)
        }


        //set up chipGroup
        setupWritingStyleChipGroup()

        // set up seekbar
        binding.seekbarOutputLength.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isSeekBarBeingTouched = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isSeekBarBeingTouched = false
                settingsViewModel.setOutputLength(seekBar?.progress ?: 0)
            }
        })


        return binding.root
    }

    private fun setupSpinner(spinner: Spinner, arrayResId: Int, associatedTextView: TextView,  defaultText: String, onItemSelected: (String) -> Unit) {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            arrayResId,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position) as String
                associatedTextView.text= selectedItem
                onItemSelected(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                associatedTextView.text = defaultText
            }
        }
    }


    @SuppressLint("ResourceType")
    private fun setupWritingStyleChipGroup() {
        val chipGroup: ChipGroup = binding.writingStyleChipGroup
        chipGroup.isSingleSelection = true // Ensures only one chip can be selected at a time

        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            chip?.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        settingsViewModel.setWritingStyle(text.toString())
                    }
                }

                chipBackgroundColor = ContextCompat.getColorStateList(context, R.drawable.chip_background_selector)
                setTextColor(ContextCompat.getColorStateList(context, R.drawable.chip_icon_tint))
            }
        }
    }

    private fun setupObservers() {
        settingsViewModel.inputLanguage.observe(viewLifecycleOwner) { language ->
            val position = (binding.spinnerInputLanguage.adapter as ArrayAdapter<String>).getPosition(language)
            if (position != binding.spinnerInputLanguage.selectedItem) {
                binding.spinnerInputLanguage.setSelection(position)
            }
        }

        settingsViewModel.outputLanguage.observe(viewLifecycleOwner) { language ->
            val position = (binding.spinnerOutputLanguage.adapter as ArrayAdapter<String>).getPosition(language)
            binding.spinnerOutputLanguage.setSelection(position)
            if (position != binding.spinnerOutputLanguage.selectedItem) {
                binding.spinnerOutputLanguage.setSelection(position)
            }
        }

        settingsViewModel.writingStyle.observe(viewLifecycleOwner) { style ->
            binding.writingStyleChipGroup.children.forEach { view ->
                if (view is Chip && view.text.toString() == style) {
                    view.isChecked = true
                }
            }
        }
        settingsViewModel.outputLength.observe(viewLifecycleOwner) { length ->
            if (!isSeekBarBeingTouched && length != binding.seekbarOutputLength.progress) {
                binding.seekbarOutputLength.progress = length
            }
        }
    }

    private fun setupUI() {
        // Spinner initialization with the current ViewModel values
        val positionInputLanguage = (binding.spinnerInputLanguage.adapter as ArrayAdapter<String>).getPosition(settingsViewModel.inputLanguage.value)
        binding.spinnerInputLanguage.setSelection(positionInputLanguage)

        val positionOutputLanguage = (binding.spinnerOutputLanguage.adapter as ArrayAdapter<String>).getPosition(settingsViewModel.outputLanguage.value)
        binding.spinnerOutputLanguage.setSelection(positionOutputLanguage)

        // ChipGroup initialization with the current ViewModel value
        val selectedStyle = settingsViewModel.writingStyle.value
        binding.writingStyleChipGroup.children.forEach { view ->
            if (view is Chip && view.text.toString() == selectedStyle) {
                view.isChecked = true
            }
        }

        binding.seekbarOutputLength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    settingsViewModel.setOutputLength(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
