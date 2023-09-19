package com.example.automind.ui.record

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.example.automind.MainActivity
import com.example.automind.R
import com.example.automind.data.TranscribedTextRepository
import com.example.automind.databinding.FragmentRecordBinding
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.speech.v1.RecognitionAudio
import com.google.cloud.speech.v1.RecognitionConfig
import com.google.cloud.speech.v1.RecognizeRequest
import com.google.cloud.speech.v1.SpeechClient
import com.google.cloud.speech.v1.SpeechSettings
import com.google.gson.JsonArray
import com.google.protobuf.ByteString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException


class RecordFragment : Fragment(),Timer.OnTimerTickListener {

    private val client = OkHttpClient()

    private lateinit var transcribedTextRepository: TranscribedTextRepository

    private val LOG_TAG = "AudioRecordTest"

    private var fileName: String = ""
    private var editText : EditText? = null
    private var btn_mic :ImageButton? = null
    private var recorder: MediaRecorder? = null
    private var btn_play :ImageButton? = null
    private var btn_remove: Button? = null
    private var player: MediaPlayer? = null
    private var tv_timer: TextView? = null
    private var waveformView: View? = null
    private var btn_submit:Button? = null
    private var btn_convert_to_mindmap:Button? = null
    private var txt_response:TextView? = null

    private lateinit var timer: Timer

    var mStartRecording = true
    var mStartPlaying = true

    private var _binding: FragmentRecordBinding? = null

    private val binding get() = _binding!!

    private val speechClient: SpeechClient by lazy{
        activity?.applicationContext?.resources?.openRawResource(R.raw.credential).use{
            SpeechClient.create(
                SpeechSettings.newBuilder()
                    .setCredentialsProvider{GoogleCredentials.fromStream(it)}
                    .build()
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val recordViewModel =
            ViewModelProvider(this).get(RecordViewModel::class.java)

        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Record to the external cache directory for visibility
        fileName = "${requireContext().externalCacheDir?.absolutePath}/audiorecordtest.awb"

        editText = binding.edittext
        btn_mic = binding.btnMic
        btn_play = binding.btnPlay
        btn_remove = binding.btnRemove
        tv_timer = binding.tvTimer
        waveformView = binding.waveformView
        btn_submit = binding.btnSubmit
        txt_response = binding.txtResponse
        btn_convert_to_mindmap = binding.btnConvertToMindmap

        timer = Timer(this)

        // Initialize the repository
        transcribedTextRepository = (activity as MainActivity).transcribedTextRepository

        btn_mic!!.setOnClickListener{
            if (ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(requireActivity(), permissions,0)
            } else {
                onRecord(mStartRecording)
                var text = when (mStartRecording) {
                    true -> "Start recording"
                    false -> "Stop recording"
                }
                Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
                mStartRecording = !mStartRecording
            }

            btn_remove!!.setOnClickListener {
                deleteTranscribedText()
            }
        }

        btn_play!!.setOnClickListener{
            onPlay(mStartPlaying)
            var text = when (mStartPlaying) {
                true -> "Start playing"
                false -> "Stop playing"
            }
            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
            mStartPlaying = !mStartPlaying
        }

        btn_submit!!.setOnClickListener{
            val question= editText!!.text.toString()
            Toast.makeText(requireContext(),question,Toast.LENGTH_SHORT).show()
            getResponse(question){response ->
                requireActivity().runOnUiThread {
                    txt_response!!.text = response
                }

            }
        }

        btn_convert_to_mindmap!!.setOnClickListener {
            // Get the Markdown content from my EditText
            val markdownContent = txt_response!!.text.toString()

            // Create a bundle to pass the content to MindMapFragment
            val bundle = Bundle()
            bundle.putString("markdownContent", markdownContent)

            // Navigate to MindMapFragment with the bundle
            findNavController().navigate(R.id.action_recordFragment_to_mindmapFragment, bundle)
        }




        return binding.root
    }

    fun getResponse(editText: String, callback: (String) -> Unit){
        val apiKey = "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"
        val url = "https://api.openai.com/v1/completions"

        val requestBody="""
            {
            "model": "text-davinci-003",
            "prompt": "I want to make a mindmap through markmap with transforming the markdown format text.Please summarize $editText and give back the markdown format of the keywords",
            "max_tokens": 1000,
            "temperature": 0
            }
         """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type","application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread{
                    Log.e("error","API failed",e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body != null) {
                    Log.v("data",body)
                }
                else{
                        Log.v("data","empty")
                    }
                try {
                    val jsonObject = JSONObject(body)
                    val jsonArray = jsonObject.getJSONArray("choices")
                    val firstChoice = jsonArray.getJSONObject(0)
                    val textResult = firstChoice.getString("text")

                    callback(textResult)

                    requireActivity().runOnUiThread {
                        txt_response!!.text = textResult
                    }
                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                        Log.e("error", "JSON parsing failed", e)
                    }
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }

    private fun onRecord(start: Boolean) = if (start) {
        clearTranscribedText()
        startRecording()
        resetWaveformView()
        cleanAndResetTimer()
        timer.start()
    } else {
        stopRecording()
        timer.pause()
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AMR_WB)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }

            start()
        }
    }

    private fun analyze(fileByteString: ByteString): String {
        val req = RecognizeRequest.newBuilder()
            .setConfig(
                RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.AMR_WB)
                    .setLanguageCode("zh-TW")
                    .setSampleRateHertz(16000)
                    .setEnableAutomaticPunctuation(true)
                    .build()
            )
            .setAudio(
                RecognitionAudio.newBuilder()
                    .setContent(fileByteString)
                    .build()
            )
            .build()

        val response = speechClient.recognize(req)
        Log.d("DatabaseTest", "ASR Response: $response")

        val results = response.resultsList
        var text = ""
        for (result in results) {
            val alternative = result.alternativesList[0]
            Log.d("recognition result", alternative.transcript)
            text += " " + alternative.transcript
        }
        return text
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null

        // speech to text
        var text = ""
        GlobalScope.launch(Dispatchers.IO){
            text = analyze(ByteString.copyFrom(File(fileName).readBytes()))
            Log.d("DatabaseTest", "Recorded Text: $text")
            if (text.isNotBlank()) {
                // Insert the transcribed text into the database
                GlobalScope.launch(Dispatchers.IO) {
                    transcribedTextRepository.insertTranscribedText(text)
                    editText?.setText(text)
                    val transcribedTexts = transcribedTextRepository.getAllTranscribedTexts()
                    for (text in transcribedTexts) {
                        Log.d("DatabaseTest", "Transcribed Text in database: ${text.text}")
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun OnTimerTick(duration: String) {
      tv_timer?.text = duration
      recorder?.maxAmplitude?.let { (waveformView as WaveformView).addAmplitude(it.toFloat()) }
    }

    private fun cleanAndResetTimer() {
        timer.pause()
        timer = Timer(this)
        tv_timer?.text = "00:00"
    }


    private fun clearTranscribedText() {
        editText?.text?.clear()
    }

    private fun deleteTranscribedText() {
        GlobalScope.launch(Dispatchers.IO) {
            transcribedTextRepository.deleteAllTranscribedTexts()
        }
    }

    private fun resetWaveformView() {
        (waveformView as WaveformView).reset()
    }



}