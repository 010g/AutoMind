package com.example.automind.ui.record

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.automind.MainActivity
import com.example.automind.data.TranscribedTextRepository
import com.example.automind.databinding.FragmentRecordBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import java.util.Objects


class RecordFragment : Fragment(), RecognitionListener {
    private lateinit var transcribedTextRepository: TranscribedTextRepository

    private lateinit var speech: SpeechRecognizer
    private lateinit var recognizerIntent: Intent

    private var editText : EditText? = null
    private var btn_mic :ImageButton? = null
    private var isRecording: Boolean = false
    private var btn_play :ImageButton? = null

    private var _binding: FragmentRecordBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val recordViewModel =
            ViewModelProvider(this).get(RecordViewModel::class.java)

        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        val root: View = binding.root

        editText = binding.edittext
        btn_mic = binding.btnMic
        btn_play = binding.btnPlay

        // Initialize the repository
        transcribedTextRepository = (activity as MainActivity).transcribedTextRepository

        speech = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speech.setRecognitionListener(this)

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "US-en")
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)

        btn_mic!!.setOnClickListener{
            if (ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(requireActivity(), permissions,0)
            } else {
                if (!isRecording) {
                    speech.startListening(recognizerIntent)
                    Toast.makeText(
                        requireContext(),
                        "Start recording!",
                        Toast.LENGTH_SHORT
                    ).show()
                    isRecording = true
                } else{
                    speech.stopListening()
                    Toast.makeText(
                        requireContext(),
                        "Stop recording!",
                        Toast.LENGTH_SHORT
                    ).show()
                    isRecording = false
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        speech.destroy()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        speech.destroy()
    }

    override fun onReadyForSpeech(params: Bundle?) {
    }

    override fun onBeginningOfSpeech() {
    }

    override fun onRmsChanged(rmsdB: Float) {
    }

    override fun onBufferReceived(buffer: ByteArray?) {
    }

    override fun onEndOfSpeech() {
    }

    override fun onError(error: Int) {
        val errorMessage: String = getErrorText(error)
        editText?.setText(errorMessage)
    }

    private fun getErrorText(error: Int): String {
        var message = ""
        message = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Didn't understand, please try again."
        }
        return message
    }

    override fun onResults(results: Bundle?) {
        val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        var text = ""
        if (matches != null) {
            for (result in matches) text = """
              $result
              """.trimIndent()

            // Insert the transcribed text into the database
            GlobalScope.launch(Dispatchers.IO) {
                transcribedTextRepository.insertTranscribedText(text)
                val transcribedTexts = transcribedTextRepository.getAllTranscribedTexts()
                for (text in transcribedTexts) {
                    Log.d("DatabaseTest", "Transcribed Text: ${text.text}")
                }
            }
        }
        editText?.setText(text)
    }

    override fun onPartialResults(partialResults: Bundle?) {
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
    }


}