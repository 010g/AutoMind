package com.example.automind.ui.record

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.automind.MainActivity
import com.example.automind.R
import com.example.automind.databinding.FragmentRecordBinding
import com.example.automind.ui.hub.CategoryViewModel
import com.example.automind.ui.settings.SettingsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.speech.v1.RecognitionAudio
import com.google.cloud.speech.v1.RecognitionConfig
import com.google.cloud.speech.v1.RecognizeRequest
import com.google.cloud.speech.v1.SpeechClient
import com.google.cloud.speech.v1.SpeechSettings
import com.google.protobuf.ByteString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString



class RecordFragment : Fragment(),Timer.OnTimerTickListener {


    private val client = OkHttpClient()

    private lateinit var recordViewModel: RecordViewModel

    private lateinit var categoryViewModel: CategoryViewModel

    private lateinit var settingsViewModel: SettingsViewModel

    private val LOG_TAG = "AudioRecordTest"

    private var fileName: String = ""
    private var editText : EditText? = null
    private var btn_mic :ImageButton? = null
    private var recorder: MediaRecorder? = null
//    private var player: MediaPlayer? = null
    private var tv_timer: TextView? = null
    private var waveformView: View? = null
//    private var btn_submit:Button? = null
//    private var btn_convert_to_mindmap:Button? = null
//    private var txt_response:TextView? = null

    private lateinit var timer: Timer

    var mStartRecording = true

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

    var question = ""
    var mindmapPrompt = ""
    var summaryPrompt = ""
    var listPrompt = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recordViewModel = ViewModelProvider(requireActivity()).get(RecordViewModel::class.java)
        categoryViewModel = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)
        settingsViewModel = ViewModelProvider(requireActivity()).get(SettingsViewModel::class.java)
        recordViewModel.clearLiveData()

        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Record to the external cache directory for visibility
        fileName = "${requireContext().externalCacheDir?.absolutePath}/audiorecordtest.awb"

        editText = binding.edittext
        btn_mic = binding.btnMic
        tv_timer = binding.tvTimer
        waveformView = binding.waveformView
//        btn_submit = binding.btnSubmit
//        txt_response = binding.txtResponse
//        btn_convert_to_mindmap = binding.btnConvertToMindmap

        timer = Timer(this)

        btn_mic!!.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(requireActivity(), permissions, 0)
            } else {
                onRecord(mStartRecording)
                mStartRecording = !mStartRecording

                // Change the icon based on the recording state
                if (mStartRecording) {
                    // Start recording
                    btn_mic!!.setImageResource(R.drawable.ic_is_recording)
                } else {
                    // Stop recording, revert to the original icon
                    btn_mic!!.setImageResource(R.drawable.ic_not_recording)
                }
            }
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recordViewModel.originalText.observe(viewLifecycleOwner) {
            Log.d("originalText observed!", it)
            checkAndPerformActions()
        }

        recordViewModel.summaryText.observe(viewLifecycleOwner) {
            Log.d("summaryText observed!", it)
            checkAndPerformActions()
        }

        recordViewModel.listText.observe(viewLifecycleOwner) {
            Log.d("listText observed!", it)
            checkAndPerformActions()
        }

        recordViewModel.markdownContent.observe(viewLifecycleOwner) {
            Log.d("markdownContent observed!", it)
            checkAndPerformActions()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAndPerformActions() {
            if (recordViewModel.hasOriginal && recordViewModel.hasSummary && recordViewModel.hasList && recordViewModel.hasMarkdown) {
                actionsAfterAllDataObtained()
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun actionsAfterAllDataObtained(){
        recordViewModel.hasOriginal = false
        recordViewModel.hasSummary = false
        recordViewModel.hasList = false
        recordViewModel.hasMarkdown = false

        // save data
        recordViewModel.originalText.value?.let { it1 ->
            recordViewModel.summaryText.value?.let { it2 ->
                recordViewModel.listText.value?.let { it3 ->
                    recordViewModel.markdownContent.value?.let { it4 ->
                        recordViewModel.saveNoteData(
                            "Work", // tag
                            binding.edittext.text.toString(), // title
                            false,
                            it1,
                            it2,
                            it3,
                            it4
                        ).invokeOnCompletion {
                            categoryViewModel.filterDataByTag("Work")
                        }
                    }
                }
            }
        }

        // Create a bundle to pass the content to DetailFragment
        val bundle = Bundle()
        bundle.putLong("id", recordViewModel.latestSavedTextId.value ?: -1)


        // Navigate to MindMapFragment with the bundle
        findNavController().navigate(R.id.action_recordFragment_to_detailFragment, bundle)
    }

    suspend fun getResponse(): String {
        return withContext(Dispatchers.IO) {

            val apiKey = "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"
            val url = "https://api.openai.com/v1/completions"

            mindmapPrompt =  recordViewModel.generateMindmapPrompt(
                question,
                settingsViewModel.inputLanguage.value.toString(),
                settingsViewModel.outputLanguage.value.toString(),
                settingsViewModel.writingStyle.value.toString()
            )
            Log.d("mindmapPrompt","$mindmapPrompt")

            val requestBody = RequestBody(
                model = "text-davinci-003",
                prompt = mindmapPrompt,
                max_tokens = 1000,
                temperature = 0.0
            )

            val jsonString = Json { prettyPrint = true }.encodeToString(requestBody)
            val mediaType = "application/json".toMediaTypeOrNull()
            val jsonRequestBody = jsonString.toRequestBody(mediaType)

            Log.d("getResponse", "1")

            val request = Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .post(jsonRequestBody)
                .build()

            return@withContext runCatching {
                val response = client.newCall(request).execute()
                val body = response.body?.string()
                val jsonObject = JSONObject(body!!)
                val jsonArray = jsonObject.getJSONArray("choices")
                val firstChoice = jsonArray.getJSONObject(0)
                firstChoice.getString("text")
            }.getOrElse {
                ""
            }
        }
    }

    suspend fun getSummary(): String {
        return withContext(Dispatchers.IO) {


            val apiKey = "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"
            val url = "https://api.openai.com/v1/completions"

            val requestBody = RequestBody(
                model = "text-davinci-003",
                prompt = summaryPrompt,
                max_tokens = 1000,
                temperature = 0.0
            )

            val jsonString = Json { prettyPrint = true }.encodeToString(requestBody)
            val mediaType = "application/json".toMediaTypeOrNull()
            val jsonRequestBody = jsonString.toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .addHeader("Content-Type","application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .post(jsonRequestBody)
                .build()

            return@withContext runCatching {
                val response = client.newCall(request).execute()
                val body = response.body?.string()
                val jsonObject = JSONObject(body!!)
                val jsonArray = jsonObject.getJSONArray("choices")
                val firstChoice = jsonArray.getJSONObject(0)
                firstChoice.getString("text")
            }.getOrElse {
                ""
            }
        }
    }

    suspend fun getList(): String  {
        return withContext(Dispatchers.IO) {

            val apiKey = "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"
            val url = "https://api.openai.com/v1/completions"

            val requestBody = RequestBody(
                model = "text-davinci-003",
                prompt = listPrompt,
                max_tokens = 1000,
                temperature = 0.0
            )

            val jsonString = Json { prettyPrint = true }.encodeToString(requestBody)
            val mediaType = "application/json".toMediaTypeOrNull()
            val jsonRequestBody = jsonString.toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .post(jsonRequestBody)
                .build()

            return@withContext runCatching {
                val response = client.newCall(request).execute()
                val body = response.body?.string()
                val jsonObject = JSONObject(body!!)
                val jsonArray = jsonObject.getJSONArray("choices")
                val firstChoice = jsonArray.getJSONObject(0)
                firstChoice.getString("text")
            }.getOrElse {
                ""
            }
        }
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
        var languageCode = ""
        if (settingsViewModel.inputLanguage.value.toString() == "Traditional Chinese"){
            languageCode = "zh-TW"
        }
        else if (settingsViewModel.inputLanguage.value.toString() == "English"){
            languageCode = "en-US"
        }
        else {
            val inputLanguage = settingsViewModel.inputLanguage.value.toString()
            throw Exception("Wrong language code: $inputLanguage !")
        }
        val req = RecognizeRequest.newBuilder()
            .setConfig(
                RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.AMR_WB)
                    .setLanguageCode(languageCode)
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

        // Hide btnMic, tvTimer, and waveformView
        binding.btnMic.visibility = View.GONE
        binding.tvTimer.visibility = View.GONE
        binding.waveformView.visibility = View.GONE

        // Show and play Lottie animation
        binding.lottieLoadingAnimation.visibility = View.VISIBLE
        binding.lottieLoadingAnimation.playAnimation()

        // speech to text
        recordViewModel.viewModelScope.launch(Dispatchers.IO) {
            question = analyze(ByteString.copyFrom(File(fileName).readBytes()))
            Log.d("DatabaseTest", "Recorded Text: $question")

            if (question.isNotBlank() || question.isBlank()) {
                // Insert the transcribed text into the database
                withContext(Dispatchers.Main) {
                    editText?.setText(question)
                    Log.d("RecordFragment", "Posted value to originalText: $question")
                }

                    // Posting value to LiveData
                    recordViewModel.originalText.postValue(question)
                    recordViewModel.hasOriginal = true

                    val responseDeferred = async { getResponse() }

                    // Get the summary-related prompt
                    summaryPrompt =  recordViewModel.generateSummaryPrompt(
                        question,
                        settingsViewModel.inputLanguage.value.toString(),
                        settingsViewModel.outputLanguage.value.toString(),
                        settingsViewModel.outputLength.value.toString(),
                        settingsViewModel.writingStyle.value.toString()
                    )
                    Log.d("summaryPrompt","$summaryPrompt")
                    val summaryDeferred = async { getSummary() }

                    // Get the list-related prompt
                    listPrompt = recordViewModel.generateListPrompt(
                        question,
                        settingsViewModel.inputLanguage.value.toString(),
                        settingsViewModel.outputLanguage.value.toString(),
                        settingsViewModel.writingStyle.value.toString()
                    )
                    Log.d("listPrompt","$listPrompt")
                    val listDeferred = async { getList() }

                    // Await results
                    val response = responseDeferred.await()
                    recordViewModel.updateMarkdownContent(response)
                    recordViewModel.hasMarkdown = true

                    val responseSummary = summaryDeferred.await()
                    recordViewModel.summaryText.postValue(responseSummary)
                    recordViewModel.hasSummary = true

                    val responseList = listDeferred.await()
                    recordViewModel.listText.postValue(responseList)
                    recordViewModel.hasList = true


                // Stop Lottie animation when all async tasks are complete
                withContext(Dispatchers.Main) {
                    // Stop and hide Lottie animation
                    binding.lottieLoadingAnimation.pauseAnimation()
                    binding.lottieLoadingAnimation.visibility = View.GONE

                    // Show btnMic, tvTimer, and waveformView
                    binding.btnMic.visibility = View.VISIBLE
                    binding.tvTimer.visibility = View.VISIBLE
                    binding.waveformView.visibility = View.VISIBLE
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

    private fun resetWaveformView() {
        (waveformView as WaveformView).reset()
    }



}

@Serializable
data class RequestBody(
    val model: String,
    val prompt: String,
    val max_tokens: Int,
    val temperature: Double
)