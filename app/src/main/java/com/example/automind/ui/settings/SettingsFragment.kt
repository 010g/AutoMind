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

            val root: View = binding.root


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
                    settingsViewModel.setOutputLength(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // Handle if needed
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // Handle if needed
                }
            })

            settingsViewModel.outputLength.observe(viewLifecycleOwner, Observer { length ->
                binding.seekbarOutputLength.progress = length
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

            // Set the spinner's selected item to be the default text
            val defaultPosition = adapter.getPosition(defaultText)
            if (defaultPosition >= 0) {
                spinner.setSelection(defaultPosition)
            }

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
                // Handle changes to input language
                // ...
            }

            settingsViewModel.outputLanguage.observe(viewLifecycleOwner) { language ->
                // Handle changes to output language
                // ...
            }

            settingsViewModel.writingStyle.observe(viewLifecycleOwner) { style ->
                // Handle changes to writing style
                // ...
            }

            settingsViewModel.outputLength.observe(viewLifecycleOwner) { length ->
                // Update the SeekBar's progress with the observed value
                binding.seekbarOutputLength.progress = length
            }
        }

        private fun setupUI() {
            // Set up the UI elements like Spinners, ChipGroups, SeekBars etc.
            // ...

            binding.seekbarOutputLength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        settingsViewModel.setOutputLength(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            // ... other UI setup code ...
        }




        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
