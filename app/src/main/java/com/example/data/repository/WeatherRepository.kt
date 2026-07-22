package com.example.data.repository

import com.example.data.api.ApiClient
import com.example.data.api.AiInsightsResult
import com.example.data.api.GeminiApiClient
import com.example.data.db.SavedCityDao
import com.example.data.db.SavedCityEntity
import com.example.data.model.CityLocation
import com.example.data.model.DailyItem
import com.example.data.model.HourlyItem
import com.example.data.model.OpenMeteoAirQualityResponse
import com.example.data.model.OpenMeteoWeatherResponse
import com.example.data.model.parseWeatherCode
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherRepository(private val savedCityDao: SavedCityDao) {

    val savedCities: Flow<List<SavedCityEntity>> = savedCityDao.getAllSavedCities()

    suspend fun fetchWeather(lat: Double, lon: Double): OpenMeteoWeatherResponse {
        return ApiClient.weatherApi.getWeatherForecast(latitude = lat, longitude = lon)
    }

    suspend fun fetchAirQuality(lat: Double, lon: Double): OpenMeteoAirQualityResponse? {
        return try {
            ApiClient.airQualityApi.getAirQuality(latitude = lat, longitude = lon)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun searchCities(query: String): List<CityLocation> {
        if (query.isBlank()) return emptyList()
        return try {
            val response = ApiClient.geocodingApi.searchCities(query)
            response.results?.map {
                CityLocation(
                    id = "${it.latitude}_${it.longitude}",
                    name = it.name,
                    country = listOfNotNull(it.adminArea, it.country).joinToString(", "),
                    lat = it.latitude,
                    lon = it.longitude
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveCity(location: CityLocation, customTag: String? = null) {
        savedCityDao.insertCity(
            SavedCityEntity(
                id = location.id,
                name = location.name,
                country = location.country,
                lat = location.lat,
                lon = location.lon,
                customTag = customTag
            )
        )
    }

    suspend fun deleteSavedCity(cityId: String) {
        savedCityDao.deleteCityById(cityId)
    }

    suspend fun fetchAiInsights(
        cityName: String,
        tempC: Double,
        conditionDesc: String,
        humidity: Double,
        windKmH: Double,
        uvIndex: Double,
        rainProbability: Int
    ): AiInsightsResult {
        return GeminiApiClient.getAiWeatherSummaryAndOutfit(
            cityName = cityName,
            tempC = tempC,
            conditionDesc = conditionDesc,
            humidity = humidity,
            windKmH = windKmH,
            uvIndex = uvIndex,
            rainProbability = rainProbability
        )
    }

    // Helper utilities to parse Open-Meteo responses into UI items
    fun processHourlyForecast(response: OpenMeteoWeatherResponse): List<HourlyItem> {
        val hourly = response.hourly ?: return emptyList()
        val times = hourly.time ?: return emptyList()
        val temps = hourly.temperature2m ?: return emptyList()
        val codes = hourly.weatherCode ?: emptyList()
        val pops = hourly.precipitationProbability ?: emptyList()

        val sdfInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        val sdfOutput = SimpleDateFormat("h a", Locale.getDefault())
        val nowMs = System.currentTimeMillis()

        val items = mutableListOf<HourlyItem>()
        for (i in times.indices) {
            val rawTime = times[i]
            val date = try { sdfInput.parse(rawTime) } catch (e: Exception) { null } ?: continue
            
            // Only include upcoming hours (next 24 hours)
            if (date.time >= nowMs - 3600000 && items.size < 24) {
                val isNow = Math.abs(date.time - nowMs) < 1800000
                val formattedTime = if (isNow) "Now" else sdfOutput.format(date)
                val temp = temps.getOrNull(i) ?: 0.0
                val code = codes.getOrNull(i) ?: 0
                val pop = pops.getOrNull(i)?.toInt() ?: 0
                
                // Determine if hour is day or night based on hour 6 to 20
                val hourOfDay = date.hours
                val isDay = hourOfDay in 6..19

                items.add(
                    HourlyItem(
                        timeFormatted = formattedTime,
                        tempC = temp,
                        weatherCode = code,
                        pop = pop,
                        isDay = isDay,
                        rawIsoTime = rawTime
                    )
                )
            }
        }
        return items
    }

    fun processDailyForecast(response: OpenMeteoWeatherResponse): List<DailyItem> {
        val daily = response.daily ?: return emptyList()
        val times = daily.time ?: return emptyList()
        val maxTemps = daily.temperature2mMax ?: return emptyList()
        val minTemps = daily.temperature2mMin ?: return emptyList()
        val codes = daily.weatherCode ?: emptyList()
        val popsMax = daily.precipitationProbabilityMax ?: emptyList()
        val uvs = daily.uvIndexMax ?: emptyList()
        val sunrises = daily.sunrise ?: emptyList()
        val sunsets = daily.sunset ?: emptyList()

        val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfOutput = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        val sdfSunInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        val sdfSunOutput = SimpleDateFormat("hh:mm a", Locale.getDefault())

        val items = mutableListOf<DailyItem>()
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        for (i in times.indices) {
            val rawDate = times[i]
            val date = try { sdfInput.parse(rawDate) } catch (e: Exception) { null } ?: continue
            val isToday = rawDate == todayStr
            val dateFormatted = if (isToday) "Today" else sdfOutput.format(date)

            val maxT = maxTemps.getOrNull(i) ?: 0.0
            val minT = minTemps.getOrNull(i) ?: 0.0
            val code = codes.getOrNull(i) ?: 0
            val pop = popsMax.getOrNull(i)?.toInt() ?: 0
            val uv = uvs.getOrNull(i) ?: 0.0

            val sunriseRaw = sunrises.getOrNull(i) ?: ""
            val sunsetRaw = sunsets.getOrNull(i) ?: ""

            val sunriseFormatted = try {
                val d = sdfSunInput.parse(sunriseRaw)
                if (d != null) sdfSunOutput.format(d) else "06:00 AM"
            } catch (e: Exception) { "06:00 AM" }

            val sunsetFormatted = try {
                val d = sdfSunInput.parse(sunsetRaw)
                if (d != null) sdfSunOutput.format(d) else "08:00 PM"
            } catch (e: Exception) { "08:00 PM" }

            items.add(
                DailyItem(
                    dateFormatted = dateFormatted,
                    maxTempC = maxT,
                    minTempC = minT,
                    weatherCode = code,
                    popMax = pop,
                    uvMax = uv,
                    sunrise = sunriseFormatted,
                    sunset = sunsetFormatted,
                    rawIsoDate = rawDate
                )
            )
        }
        return items
    }
}
