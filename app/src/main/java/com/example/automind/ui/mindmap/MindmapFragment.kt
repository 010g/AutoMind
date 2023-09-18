package com.example.automind.ui.mindmap

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
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
        /*
        val markdownContent = """
            # My Mind Map
            ## Main Topic
            - Subtopic 1
              - Sub-subtopic 1.1
              - Sub-subtopic 1.2
            - Subtopic 2
              - Sub-subtopic 2.1
              - Sub-subtopic 2.2
            ## Another Main Topic
            - Subtopic A
              - Sub-subtopic A.1
              - Sub-subtopic A.2
            - Subtopic B
              - Sub-subtopic B.1
              - Sub-subtopic B.2
        """.trimIndent()
        */
        Log.d("MindMapFragment", "Markdown Content: $markdownContent")

        // Enable JavaScript in the WebView
        markmapWebView.settings.javaScriptEnabled = true
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
            </head>
            <body>
                <svg id="markmap" style="width: 800px; height: 800px"></svg>
                <script>
                    const { markmap } = window;
                    const { Markmap, loadCSS, loadJS } = markmap;
        
                    function renderMarkmap(markdownContent) {
                        // Transform Markdown to markmap data
                        const transformer = new markmap.Transformer();
                        const { root, features } = transformer.transform(markdownContent);
                        const { styles, scripts } = transformer.getUsedAssets(features);
        
                        // Load assets
                        if (styles) loadCSS(styles);
                        if (scripts) loadJS(scripts, { getMarkmap: () => markmap });
        
                        // Create markmap
                        Markmap.create('#markmap', undefined, root);
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
}
