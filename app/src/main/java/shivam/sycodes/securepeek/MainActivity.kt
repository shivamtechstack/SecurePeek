package shivam.sycodes.securepeek

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import shivam.sycodes.securepeek.databinding.ActivityMainBinding
import shivam.sycodes.securepeek.metadata.URLMetadataFetcher
import shivam.sycodes.securepeek.utils.WebViewScreenshotService

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
            val url = binding.receivedURL.text.toString().trim()
            if (url.isNotEmpty()) {
                updateProgressAndErrors("Analysing URL...")
                binding.ContentResult.visibility = View.GONE
                binding.urlScreenshot.visibility = View.GONE
                binding.urlPreview.visibility = View.GONE
                analyseURL(url)
                updateProgressAndErrors("Loading Preview...")
                loadPreview(url)
            } else {
                updateProgressAndErrors("Please enter a valid URL")
            }
        }
    }

    private fun analyseURL(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val metadata = URLMetadataFetcher.fetchURLMetadata(url)
                withContext(Dispatchers.Main) {
                    binding.ContentResult.text = "Title: ${metadata.title}\n\nDescription: ${metadata.description}"
                    binding.ContentResult.visibility = View.VISIBLE
                    metadata.image?.let {
                        binding.urlPreview.setImageBitmap(it)
                        binding.urlPreview.visibility = View.VISIBLE
                    }
                    updateProgressAndErrors("")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    updateProgressAndErrors("Error analysing URL: ${e.message}")
                }
            }
        }
    }

    private fun loadPreview(url: String) {
        WebViewScreenshotService.loadUrlInBackground(this, url) { screenshot ->
            runOnUiThread {
                if (screenshot != null) {
                    binding.urlScreenshot.setImageBitmap(screenshot)
                    binding.urlScreenshot.visibility = View.VISIBLE
                    updateProgressAndErrors("") // Clear progress text
                } else {
                    updateProgressAndErrors("Website preview failed.")
                }
            }
        }
    }

    private fun updateProgressAndErrors(message: String) {
        binding.progressAndErrorsText.text = message
    }
}
