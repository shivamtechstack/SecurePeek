SecurePeek

SecurePeek is a powerful URL analyzer that checks the safety of any URL using the Google Safe Browsing API. It provides users with detailed insights into the URL, including its metadata and a safe screenshot preview of the website.
🚀 Features

    Safety Analysis: Scans URLs to detect threats like malware, phishing attempts, and unwanted software using the Google Safe Browsing API.
    Metadata Extraction: Displays metadata about the website, such as title, description, and other useful attributes.
    Screenshot Previews: Securely opens URLs in the background and generates a screenshot to give users a quick preview of the website.
    User-Friendly: Intuitive interface and clear results for non-technical users.

🛠️ Getting Started
Prerequisites

To use SecurePeek, you need:

    Google Safe Browsing API Key
    A development environment with Kotlin and the necessary dependencies installed.

How to Obtain an API Key

    Go to the Google Cloud Console.
    Create or select a project.
    Navigate to the API & Services section.
    Enable the Google Safe Browsing API.
    Generate and copy your API key.

Replace the placeholder API key in the code with your own:

private const val apiKey = "YOUR_API_KEY" // Replace with your API key

🖥️ Installation

    Clone the repository:

    git clone https://github.com/shivamtechstack/SecurePeek.git
    cd SecurePeek

    Open the project in Android studio.

    Replace the placeholder API key with your actual API key in the SafeBrowsingService object.

    Build and run the application.

⚙️ How It Works
1. URL Safety Check

The app sends a request to the Google Safe Browsing API to analyze the URL. Based on the response, it identifies whether the URL is safe or contains potential threats.

val threatResult = SafeBrowsingService.checkUrlSafety("https://example.com")
if (threatResult.isSafe) {
    println("The URL is safe!")
} else {
    println("Threats detected: ${threatResult.threats}")
}

2. Metadata Extraction

SecurePeek fetches metadata, such as the website's title and description, using utilities for a detailed analysis.

##Screenshots
Here are some previews of the app:

<img src="preview/WhatsApp%20Image%202025-01-04%20at%2019.01.53_697e5460.jpg" alt="one" width="200">
<img src="preview/WhatsApp%20Image%202025-01-04%20at%2019.01.54_5daddb8c.jpg" alt="two" width="200">
<img src="preview/WhatsApp%20Image%202025-01-04%20at%2019.01.54_c997a12b.jpg" alt="three" width="200">

The app opens the URL in a safe, headless environment to capture a screenshot, providing a preview to the user.
🔑 Code Highlights

Here’s the core of the SafeBrowsingService implementation:

object SafeBrowsingService {

    private val client = OkHttpClient()
    private const val apiKey = "YOUR_API_KEY" // Replace with your API key

    suspend fun checkUrlSafety(url: String): ThreatResult {
        val requestBody = """
        {
            "client": {
                "clientId": "url-analyzer",
                "clientVersion": "1.0"
            },
            "threatInfo": {
                "threatTypes": ["MALWARE", "SOCIAL_ENGINEERING", "UNWANTED_SOFTWARE", "POTENTIALLY_HARMFUL_APPLICATION"],
                "platformTypes": ["ANY_PLATFORM"],
                "threatEntryTypes": ["URL"],
                "threatEntries": [{"url": "$url"}]
            }
        }
        """.trimIndent()

        val request = Request.Builder()
            .url("https://safebrowsing.googleapis.com/v4/threatMatches:find?key=$apiKey")
            .post(RequestBody.create("application/json".toMediaType(), requestBody))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Error: Received HTTP ${response.code}. Please check the request format.")
                }

                val responseJson = response.body?.string()
                if (responseJson?.contains("matches") == true) {
                    val threatResponse = Gson().fromJson(responseJson, ThreatResponse::class.java)
                    return ThreatResult(
                        isSafe = false,
                        threats = threatResponse.matches?.map { match ->
                            Threat(
                                type = match.threatType,
                                platform = match.platformType,
                                url = match.threat?.url ?: "Unknown"
                            )
                        } ?: emptyList()
                    )
                }
                return ThreatResult(isSafe = true, threats = emptyList())
            }
        } catch (e: Exception) {
            Log.e("SafeBrowsingService", "Error checking URL safety: ${e.message}", e)
            throw Exception("Failed to check URL safety. Please try again.")
        }
    }
}


🤝 Contributing

Contributions are welcome! Feel free to submit issues or pull requests to enhance SecurePeek.
📜 License

This project is licensed under the MIT License.
