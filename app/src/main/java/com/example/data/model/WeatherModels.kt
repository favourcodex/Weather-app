package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// --- Open-Meteo Weather API Response ---
@JsonClass(generateAdapter = true)
data class OpenMeteoWeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String?,
    @Json(name = "elevation") val elevation: Double? = 0.0,
    @Json(name = "current") val current: CurrentUnitsAndValues?,
    @Json(name = "hourly") val hourly: HourlyUnitsAndValues?,
    @Json(name = "daily") val daily: DailyUnitsAndValues?
)

@JsonClass(generateAdapter = true)
data class CurrentUnitsAndValues(
    val time: String?,
    @Json(name = "temperature_2m") val temperature2m: Double?,
    @Json(name = "relative_humidity_2m") val relativeHumidity2m: Double?,
    @Json(name = "apparent_temperature") val apparentTemperature: Double?,
    @Json(name = "is_day") val isDay: Int?,
    val precipitation: Double?,
    val rain: Double?,
    val showers: Double?,
    val snowfall: Double?,
    @Json(name = "weather_code") val weatherCode: Int?,
    @Json(name = "cloud_cover") val cloudCover: Double?,
    @Json(name = "pressure_msl") val pressureMsl: Double?,
    @Json(name = "surface_pressure") val surfacePressure: Double?,
    @Json(name = "wind_speed_10m") val windSpeed10m: Double?,
    @Json(name = "wind_direction_10m") val windDirection10m: Double?,
    @Json(name = "wind_gusts_10m") val windGusts10m: Double?
)

@JsonClass(generateAdapter = true)
data class HourlyUnitsAndValues(
    val time: List<String>?,
    @Json(name = "temperature_2m") val temperature2m: List<Double>?,
    @Json(name = "relative_humidity_2m") val relativeHumidity2m: List<Double>?,
    @Json(name = "apparent_temperature") val apparentTemperature: List<Double>?,
    @Json(name = "precipitation_probability") val precipitationProbability: List<Double>?,
    val precipitation: List<Double>?,
    @Json(name = "weather_code") val weatherCode: List<Int>?,
    @Json(name = "pressure_msl") val pressureMsl: List<Double>?,
    @Json(name = "cloud_cover") val cloudCover: List<Double>?,
    val visibility: List<Double>?,
    @Json(name = "wind_speed_10m") val windSpeed10m: List<Double>?,
    @Json(name = "uv_index") val uvIndex: List<Double>?
)

@JsonClass(generateAdapter = true)
data class DailyUnitsAndValues(
    val time: List<String>?,
    @Json(name = "weather_code") val weatherCode: List<Int>?,
    @Json(name = "temperature_2m_max") val temperature2mMax: List<Double>?,
    @Json(name = "temperature_2m_min") val temperature2mMin: List<Double>?,
    @Json(name = "apparent_temperature_max") val apparentTemperatureMax: List<Double>?,
    @Json(name = "apparent_temperature_min") val apparentTemperatureMin: List<Double>?,
    val sunrise: List<String>?,
    val sunset: List<String>?,
    @Json(name = "uv_index_max") val uvIndexMax: List<Double>?,
    @Json(name = "precipitation_sum") val precipitationSum: List<Double>?,
    @Json(name = "precipitation_probability_max") val precipitationProbabilityMax: List<Double>?,
    @Json(name = "wind_speed_10m_max") val windSpeed10mMax: List<Double>?
)

// --- Open-Meteo Air Quality Response ---
@JsonClass(generateAdapter = true)
data class OpenMeteoAirQualityResponse(
    val latitude: Double,
    val longitude: Double,
    val current: AirQualityCurrentData?
)

@JsonClass(generateAdapter = true)
data class AirQualityCurrentData(
    @Json(name = "us_aqi") val usAqi: Double?,
    @Json(name = "european_aqi") val europeanAqi: Double?,
    val pm10: Double?,
    @Json(name = "pm2_5") val pm25: Double?,
    val ozone: Double?,
    @Json(name = "nitrogen_dioxide") val nitrogenDioxide: Double?,
    @Json(name = "sulphur_dioxide") val sulphurDioxide: Double?
)

// --- Open-Meteo Geocoding API Response ---
@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    val results: List<GeocodingResult>?
)

@JsonClass(generateAdapter = true)
data class GeocodingResult(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    @Json(name = "country_code") val countryCode: String?,
    @Json(name = "admin1") val adminArea: String?,
    val timezone: String?,
    val population: Long?
)

// --- Processed Application Models ---
data class HourlyItem(
    val timeFormatted: String, // e.g. "3 PM" or "Now"
    val tempC: Double,
    val weatherCode: Int,
    val pop: Int, // precipitation probability %
    val isDay: Boolean,
    val rawIsoTime: String
)

data class DailyItem(
    val dateFormatted: String, // e.g. "Mon, Jul 22" or "Today"
    val maxTempC: Double,
    val minTempC: Double,
    val weatherCode: Int,
    val popMax: Int,
    val uvMax: Double,
    val sunrise: String, // e.g. "06:12 AM"
    val sunset: String,  // e.g. "08:45 PM"
    val rawIsoDate: String
)

data class CityLocation(
    val id: String,
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val isUserCurrent: Boolean = false,
    val customTag: String? = null
)

// Weather Condition Classification
enum class WeatherCategory {
    CLEAR_DAY,
    CLEAR_NIGHT,
    PARTLY_CLOUDY_DAY,
    PARTLY_CLOUDY_NIGHT,
    CLOUDY,
    FOG,
    DRIZZLE,
    RAIN,
    HEAVY_RAIN,
    THUNDERSTORM,
    SNOW,
    WINDY
}

fun parseWeatherCode(code: Int?, isDay: Boolean = true): WeatherCategory {
    return when (code) {
        0 -> if (isDay) WeatherCategory.CLEAR_DAY else WeatherCategory.CLEAR_NIGHT
        1, 2 -> if (isDay) WeatherCategory.PARTLY_CLOUDY_DAY else WeatherCategory.PARTLY_CLOUDY_NIGHT
        3 -> WeatherCategory.CLOUDY
        45, 48 -> WeatherCategory.FOG
        51, 53, 55, 56, 57 -> WeatherCategory.DRIZZLE
        61, 63, 80, 81 -> WeatherCategory.RAIN
        65, 82 -> WeatherCategory.HEAVY_RAIN
        71, 73, 75, 77, 85, 86 -> WeatherCategory.SNOW
        95, 96, 99 -> WeatherCategory.THUNDERSTORM
        else -> if (isDay) WeatherCategory.CLEAR_DAY else WeatherCategory.CLEAR_NIGHT
    }
}

fun WeatherCategory.getDescription(): String {
    return when (this) {
        WeatherCategory.CLEAR_DAY -> "Clear Sky"
        WeatherCategory.CLEAR_NIGHT -> "Clear Night"
        WeatherCategory.PARTLY_CLOUDY_DAY -> "Partly Cloudy"
        WeatherCategory.PARTLY_CLOUDY_NIGHT -> "Partly Cloudy"
        WeatherCategory.CLOUDY -> "Overcast"
        WeatherCategory.FOG -> "Foggy & Misty"
        WeatherCategory.DRIZZLE -> "Light Drizzle"
        WeatherCategory.RAIN -> "Rainy"
        WeatherCategory.HEAVY_RAIN -> "Heavy Rainstorm"
        WeatherCategory.THUNDERSTORM -> "Thunderstorms"
        WeatherCategory.SNOW -> "Snowing"
        WeatherCategory.WINDY -> "Windy"
    }
}
