package com.example.data.api

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent?
)

interface GeminiRestApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    val service: GeminiRestApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GeminiRestApi::class.java)
    }

    suspend fun getAiWeatherSummaryAndOutfit(
        cityName: String,
        tempC: Double,
        conditionDesc: String,
        humidity: Double,
        windKmH: Double,
        uvIndex: Double,
        rainProbability: Int
    ): AiInsightsResult {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return generateLocalFallbackInsights(tempC, conditionDesc, rainProbability, windKmH, uvIndex)
        }

        val prompt = """
            You are Aether AI, a luxury weather assistant. Analyze this weather for $cityName:
            - Temperature: ${tempC.toInt()}°C (${(tempC * 9/5 + 32).toInt()}°F)
            - Weather Condition: $conditionDesc
            - Rain Chance: $rainProbability%
            - Wind Speed: ${windKmH.toInt()} km/h
            - UV Index: $uvIndex
            - Humidity: ${humidity.toInt()}%

            Provide a short response in EXACTLY this format with 3 distinct paragraphs separated by "|||":
            Paragraph 1: A brief, stylish 2-sentence summary highlighting what to expect today and atmospheric vibe.
            Paragraph 2: Specific outfit and gear recommendations (e.g. layers, jacket, sunglasses, umbrella, breathable fabrics).
            Paragraph 3: Ratings for activities formatted as: Running: [Ideal/Good/Moderate/Poor] | Outdoor Dining: [Ideal/Good/Moderate/Poor] | Photography: [Ideal/Good/Moderate/Poor] | Stargazing: [Ideal/Good/Moderate/Poor].
        """.trimIndent()

        return try {
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                )
            )
            val response = service.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!text.isNullOrBlank()) {
                val parts = text.split("|||")
                val summary = parts.getOrNull(0)?.trim() ?: "Crisp weather conditions in $cityName today."
                val outfit = parts.getOrNull(1)?.trim() ?: "Wear comfortable clothing suitable for $conditionDesc."
                val activitiesText = parts.getOrNull(2)?.trim() ?: ""

                AiInsightsResult(
                    summary = summary,
                    outfitAdvice = outfit,
                    activitiesText = activitiesText,
                    isAiGenerated = true
                )
            } else {
                generateLocalFallbackInsights(tempC, conditionDesc, rainProbability, windKmH, uvIndex)
            }
        } catch (e: Exception) {
            generateLocalFallbackInsights(tempC, conditionDesc, rainProbability, windKmH, uvIndex)
        }
    }

    private fun generateLocalFallbackInsights(
        tempC: Double,
        conditionDesc: String,
        rainProb: Int,
        windKmH: Double,
        uvIndex: Double
    ): AiInsightsResult {
        val summary = when {
            rainProb > 60 -> "Rain showers expected today ($rainProb% chance). High humidity and wet pavement ahead."
            tempC > 28 -> "Warm and radiant atmosphere today. Stay hydrated and enjoy the bright sunshine."
            tempC < 10 -> "Chilly conditions. A brisk breeze brings a cool atmosphere throughout the day."
            else -> "Pleasant conditions today with mild temperature and gentle atmospheric pressure."
        }

        val outfit = when {
            rainProb > 50 -> "Bring a sleek waterproof jacket or compact umbrella. Moisture-resistant shoes recommended."
            tempC > 25 -> "Light breathable cotton or linen, sunglasses, and UV sunscreen."
            tempC < 12 -> "A warm wool coat or insulated bomber jacket, paired with a stylish scarf."
            else -> "Versatile light jacket over a casual crewneck sweater."
        }

        val running = if (tempC in 12.0..22.0 && rainProb < 30) "Ideal" else if (rainProb > 70) "Poor" else "Good"
        val dining = if (tempC >= 18 && rainProb < 20) "Ideal" else if (rainProb > 40) "Poor" else "Moderate"
        val photo = if (conditionDesc.contains("Cloud", true) || conditionDesc.contains("Clear", true)) "Ideal" else "Moderate"
        val star = if (conditionDesc.contains("Clear", true)) "Ideal" else "Poor"

        val activities = "Running: $running | Outdoor Dining: $dining | Photography: $photo | Stargazing: $star"

        return AiInsightsResult(
            summary = summary,
            outfitAdvice = outfit,
            activitiesText = activities,
            isAiGenerated = false
        )
    }
}

data class AiInsightsResult(
    val summary: String,
    val outfitAdvice: String,
    val activitiesText: String,
    val isAiGenerated: Boolean
)
