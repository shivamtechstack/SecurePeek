package shivam.sycodes.securepeek

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import shivam.sycodes.securepeek.databinding.ActivityMainBinding
import shivam.sycodes.securepeek.metadata.URLMetadataFetcher
import shivam.sycodes.securepeek.utils.WebViewScreenshotService
import java.util.zip.Inflater

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.analyseButton.setOnClickListener {
            val url = binding.receivedURL.text.toString()
            if (url.isNotEmpty()){
                binding.progressAndErrorsText.text = "Analysing URL..."
                analyseURL(url)
                binding.ContentResult.visibility = View.VISIBLE
             binding.progressAndErrorsText.text = "Loading Preview..."
                loadPreview(url)
            }else{
               binding.progressAndErrorsText.text = "Please enter a URL"
            }
        }
    }

    private fun analyseURL(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val metadata = URLMetadataFetcher.fetchURLMetadata(url)
                CoroutineScope(Dispatchers.Main).launch {
                    binding.ContentResult.text = "Title: ${metadata.title} \n Description:${metadata.description}"
                    binding.urlPreview.visibility = View.VISIBLE
                    binding.urlPreview.setImageBitmap(metadata.image)
                }
            }catch (e : Exception){
             CoroutineScope(Dispatchers.Main).launch {
                 binding.threatAnalyseResult.text = "Error: ${e.message}"
             }
            }
        }
    }
    private fun loadPreview(url: String) {
        WebViewScreenshotService.loadUrlInBackground(this, url) { screenshot ->
            runOnUiThread {
                if (screenshot != null) {
                    binding.urlScreenshot.visibility = View.VISIBLE
                   binding.urlScreenshot.setImageBitmap(screenshot)
                    binding.progressAndErrorsText.text = " "
                } else {
                    binding.progressAndErrorsText.text = "Website Preview error!"
                }
            }
        }
    }
}