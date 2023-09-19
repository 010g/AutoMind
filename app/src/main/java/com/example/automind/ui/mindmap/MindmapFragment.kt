package com.example.automind.ui.mindmap

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.automind.R

class MindMapFragment : Fragment() {

    private lateinit var markmapWebView: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_mindmap, container, false)
        markmapWebView = root.findViewById(R.id.markmapWebView)

        val markdownContent = arguments?.getString("markdownContent")
        Log.d("MindMapFragment", "Markdown Content: $markdownContent")

        // Enable JavaScript and Zoom in the WebView
        markmapWebView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }

        val zoomInButton: ImageButton = root.findViewById(R.id.zoomInButton)
        zoomInButton.setOnClickListener {
            markmapWebView.zoomIn()
        }

        val zoomOutButton: ImageButton = root.findViewById(R.id.zoomOutButton)
        zoomOutButton.setOnClickListener {
            markmapWebView.zoomOut()
        }

        val editButton: ImageButton = root.findViewById(R.id.editButton)
        editButton.setOnClickListener {
            // Open an edit dialog or another activity to edit markdownContent
            openEditDialog()
        }

        // Set up a WebChromeClient to handle console messages from WebView
        markmapWebView.webChromeClient = object : WebChromeClient() {
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

        markmapWebView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        Log.d("MindMapFragment", "Loaded HTML content into WebView")

        // After WebView is fully loaded, render the Markmap
        markmapWebView.webViewClient = object : WebViewClient() {
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
        markmapWebView.evaluateJavascript(jsCode, null)
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

}
