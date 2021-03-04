package ml.techzgamecoding.breezer

import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity() {

    private lateinit var web_browser: WebView
    private lateinit var search_btn : ImageButton
    private lateinit var back_btn : ImageButton
    private lateinit var url_txt : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        web_browser = findViewById(R.id.web_browser)
        search_btn = findViewById(R.id.search_btn)
        back_btn = findViewById(R.id.back_btn)
        url_txt = findViewById(R.id.url_txt)
        // to be able of using webview in android we should add Internet permission first
        // go to manifest and add permission
        //we will add google as a defaut web page when our activity load

        web_browser.loadUrl("http://www.google.com")
        web_browser.settings.javaScriptEnabled = true
        // we need to enable javascript
        web_browser.canGoBack()
        web_browser.webViewClient = WebClient(this)
        // Now we need to load an url everytime we search something
        search_btn.setOnClickListener {
            val URL = url_txt.text.toString()
            web_browser.loadUrl(URL)
        }

        //now we will add the script to return back
        back_btn.setOnClickListener {
            web_browser.goBack()
        }


        //before we test our app we need to create a webclient class


    }


    class WebClient internal constructor(private val activity: Activity): WebViewClient(){
        override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
        ): Boolean {
            view?.loadUrl(request?.url.toString())
            return true
        }

    }
}
