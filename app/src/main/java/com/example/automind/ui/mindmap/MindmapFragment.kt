package com.example.automind.ui.mindmap

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import android.Manifest
import com.example.automind.R
import com.example.automind.data.AppDatabase
import com.example.automind.data.TranscribedTextRepository
import com.example.automind.databinding.FragmentMindmapBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class MindMapFragment : Fragment() {

    private val database by lazy { AppDatabase.getDatabase(requireContext()) }
    private val repository by lazy { TranscribedTextRepository(database.transcribedTextDao()) }

    private var _binding: FragmentMindmapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMindmapBinding.inflate(inflater, container, false)
        val root = binding.root
        binding.markmapWebView.let {
            it.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
            }
        }

        val markdownContent = arguments?.getString("markdownContent")
        Log.d("MindMapFragment", "Markdown Content: $markdownContent")


        binding.btnZoomIn.setOnClickListener {
            binding.markmapWebView.zoomIn()
        }

        binding.btnZoomOut.setOnClickListener {
            binding.markmapWebView.zoomOut()
        }

        binding.btnEdit.setOnClickListener {
            openEditDialog()
        }

        binding.btnSave.setOnClickListener {
            val currentMarkdown = arguments?.getString("markdownContent") ?: ""
            saveMarkdownToDatabase(currentMarkdown)
        }

        // Set up a WebChromeClient to handle console messages from WebView
        binding.markmapWebView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.d(
                    "WebView Console",
                    "${consoleMessage.message()} -- From line ${consoleMessage.lineNumber()} of ${consoleMessage.sourceId()}"
                )
                return true
            }
        }

        // Load the HTML content with embedded Markmap JavaScript
        val escapedMarkdownContent = markdownContent?.replace("`", "\\`") ?: ""
        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <script src="https://cdn.jsdelivr.net/npm/d3@7"></script>
                <script src="https://cdn.jsdelivr.net/npm/markmap-lib"></script>
                <script src="https://cdn.jsdelivr.net/npm/markmap-view"></script>
                <script src="https://cdn.jsdelivr.net/npm/markmap-toolbar"></script>

            </head>
            <body>
                <svg id="markmap" style="width: 800px; height: 800px"></svg>
                <script>
                    const { markmap } = window;
                    const { Markmap, loadCSS, loadJS } = markmap;
                    
                    let currentMarkmapInstance; // Hold the current markmap instance
        
                    function renderMarkmap(markdownContent) {
                    
                        // Clear existing markmap, if any
                        if (currentMarkmapInstance) {
                            currentMarkmapInstance.destroy();
                        }
                        
                       
                        // Transform Markdown to markmap data
                        const transformer = new markmap.Transformer();
                        const { root, features } = transformer.transform(markdownContent);
                        const { styles, scripts } = transformer.getUsedAssets(features);
                                          
                 
                        // Load assets
                        if (styles) loadCSS(styles);
                        if (scripts) loadJS(scripts, { getMarkmap: () => markmap });
                        
                        currentMarkmapInstance = Markmap.create('#markmap', undefined, root);
                        
                                              
                        // Add the toolbar
                        const { el } = Toolbar.create(mm);
                        el.style.position = 'absolute';
                        el.style.bottom = '0.5rem';
                        el.style.right = '0.5rem';
                        document.body.appendChild(el);
                    }
                </script>
            </body>
            </html>
        """

        binding.markmapWebView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        Log.d("MindMapFragment", "Loaded HTML content into WebView")

        // After WebView is fully loaded, render the Markmap
        binding.markmapWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                markdownContent?.let {
                    renderMarkdownInWebView(it)
                }
            }
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                // Handle WebView errors
                val msg = error?.description ?: "Unknown error"
                val url = request?.url?.toString() ?: "Unknown URL"
                Log.e("WebView Error", "Error loading $url: $msg")
            }
        }

        return root
    }

    fun renderMarkdownInWebView(markdownContent: String) {
        val jsCode = "javascript:renderMarkmap(`$markdownContent`);"
        binding.markmapWebView.evaluateJavascript(jsCode, null)
    }


    // Function to open an edit dialog
    private fun openEditDialog() {
        val editText = EditText(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setText(arguments?.getString("markdownContent"))
        }

        context?.let {
            AlertDialog.Builder(it)
                .setTitle("Edit Mindmap Content")
                .setView(editText)
                .setPositiveButton("Update") { _, _ ->
                    val updatedContent = editText.text.toString()
                    arguments?.putString("markdownContent", updatedContent)
                    renderMarkdownInWebView(updatedContent)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun saveMarkdownToDatabase(markdownContent: String) {
        lifecycleScope.launch {
            repository.insertTranscribedText(markdownContent)

            // Step 2: Add log to see all the data in the current database.
            val allTexts = repository.getAllTranscribedTexts()
            allTexts.forEach {
                Log.d("DatabaseContent", "ID: ${it.id}, Text: ${it.text}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
