package com.example.automind.ui.detail.mindmap

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.automind.databinding.FragmentMindmapBinding
import androidx.lifecycle.ViewModelProvider

class MindMapFragment : Fragment() {

    private var _binding: FragmentMindmapBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MindmapViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMindmapBinding.inflate(inflater, container, false)
        val root = binding.root

        viewModel = ViewModelProvider(this).get(MindmapViewModel::class.java)

        binding.markmapWebView.let {
            it.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
            }
        }

        val id = arguments?.getLong("id")
        viewModel.latestSavedTextId.value = id
        viewModel.getNoteByLatestSavedTextId()


        // Observe selectedNote LiveData
        viewModel.selectedNote.observe(viewLifecycleOwner) { note ->
            Log.d("MindMapFragment", "Received a note: $note")
            note?.let {
                // When a new note is received, get the markdownContent and render it
                val markdownContent = it.mindmapMarkdown ?: ""
                renderMarkdownInWebView(markdownContent)

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
            }
        }


        binding.btnEdit.setOnClickListener {
            openEditDialog()
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

        val darkThemeCSS = """
    <style>
        body {
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100vh; /* 100% of the viewport height */
            margin: 0; /* Removing the default margin */
            background-color: #353535; /* Dark background color */
            color: #ffffff; /* Light font color */
        }
        svg {
            background-color: #353535; /* Dark background color for SVG */
        }
    </style>
"""

        // Load the HTML content with embedded Markmap JavaScript
        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                $darkThemeCSS
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

        return root
    }

    fun renderMarkdownInWebView(markdownContent: String) {
        Log.d("MindMapFragment", "Rendering markdown: $markdownContent")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
