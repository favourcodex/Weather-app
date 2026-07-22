package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.AiInsightsResult
import com.example.data.db.AppDatabase
import com.example.data.db.SavedCityEntity
import com.example.data.db.SavedCityDao
import com.example.data.model.CityLocation
import com.example.data.model.DailyItem
import com.example.data.model.HourlyItem
import com.example.data.model.OpenMeteoAirQualityResponse
import com.example.data.model.OpenMeteoWeatherResponse
import com.example.data.model.WeatherCategory
import com.example.data.model.getDescription
import com.example.data.model.parseWeatherCode
import com.example.data.repository.WeatherRepository
import androidx.room.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface WeatherUiState {
    object Loading : WeatherUiState
    data class Success(
        val weather: OpenMeteoWeatherResponse,
        val airQuality: OpenMeteoAirQualityResponse?,
        val hourlyList: List<HourlyItem>,
        val dailyList: List<DailyItem>,
        val currentWeatherCategory: WeatherCategory
    ) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "aether_weather.db"
    ).build()

    private val repository = WeatherRepository(db.savedCityDao())

    val savedCities: StateFlow<List<SavedCityEntity>> = repository.savedCities
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentLocation = MutableStateFlow(
        CityLocation(
            id = "37.7749_-122.4194",
            name = "San Francisco",
            country = "California, United States",
            lat = 37.7749,
            lon = -122.4194,
            isUserCurrent = true
        )
    )
    val currentLocation: StateFlow<CityLocation> = _currentLocation.asStateFlow()

    private val _isCelsius = MutableStateFlow(true)
    val isCelsius: StateFlow<Boolean> = _isCelsius.asStateFlow()

    private val _weatherUiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherUiState: StateFlow<WeatherUiState> = _weatherUiState.asStateFlow()

    private val _aiInsightsState = MutableStateFlow<AiInsightsResult?>(null)
    val aiInsightsState: StateFlow<AiInsightsResult?> = _aiInsightsState.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _searchResults = MutableStateFlow<List<CityLocation>>(emptyList())
    val searchResults: StateFlow<List<CityLocation>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    // Preset Cities for instant selection
    val presetCities = listOf(
        CityLocation("37.7749_-122.4194", "San Francisco", "USA", 37.7749, -122.4194),
        CityLocation("40.7128_-74.006", "New York", "USA", 40.7128, -74.0060),
        CityLocation("51.5074_-0.1278", "London", "United Kingdom", 51.5074, -0.1278),
        CityLocation("48.8566_2.3522", "Paris", "France", 48.8566, 2.3522),
        CityLocation("35.6762_139.6503", "Tokyo", "Japan", 35.6762, 139.6503),
        CityLocation("47.3769_8.5417", "Zurich", "Switzerland", 47.3769, 8.5417),
        CityLocation("-33.8688_151.2093", "Sydney", "Australia", -33.8688, 151.2093)
    )

    init {
        // Seed default saved cities if database is empty
        viewModelScope.launch {
            savedCities.collect { list ->
                if (list.isEmpty()) {
                    repository.saveCity(presetCities[0], "Home")
                    repository.saveCity(presetCities[2], "Europe Hub")
                    repository.saveCity(presetCities[4], "Vacation Spot")
                }
            }
        }
        loadWeatherForCurrentLocation()
    }

    fun selectLocation(location: CityLocation) {
        _currentLocation.value = location
        loadWeatherForCurrentLocation()
    }

    fun toggleUnit() {
        _isCelsius.value = !_isCelsius.value
    }

    fun refreshWeather() {
        loadWeatherForCurrentLocation()
    }

    private fun loadWeatherForCurrentLocation() {
        val city = _currentLocation.value
        viewModelScope.launch {
            _weatherUiState.value = WeatherUiState.Loading
            _aiInsightsState.value = null
            try {
                val weather = repository.fetchWeather(city.lat, city.lon)
                val airQuality = repository.fetchAirQuality(city.lat, city.lon)
                val hourlyList = repository.processHourlyForecast(weather)
                val dailyList = repository.processDailyForecast(weather)

                val currentCode = weather.current?.weatherCode ?: 0
                val isDay = (weather.current?.isDay ?: 1) == 1
                val category = parseWeatherCode(currentCode, isDay)

                _weatherUiState.value = WeatherUiState.Success(
                    weather = weather,
                    airQuality = airQuality,
                    hourlyList = hourlyList,
                    dailyList = dailyList,
                    currentWeatherCategory = category
                )

                // Load AI Insights in background
                loadAiInsights(city, weather, category)
            } catch (e: Exception) {
                _weatherUiState.value = WeatherUiState.Error(
                    e.localizedMessage ?: "Failed to load weather data. Please check connection."
                )
            }
        }
    }

    private fun loadAiInsights(
        city: CityLocation,
        weather: OpenMeteoWeatherResponse,
        category: WeatherCategory
    ) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val tempC = weather.current?.temperature2m ?: 20.0
            val humidity = weather.current?.relativeHumidity2m ?: 50.0
            val wind = weather.current?.windSpeed10m ?: 10.0
            val rainProb = weather.hourly?.precipitationProbability?.firstOrNull()?.toInt() ?: 0
            val uv = weather.daily?.uvIndexMax?.firstOrNull() ?: 3.0

            val insights = repository.fetchAiInsights(
                cityName = city.name,
                tempC = tempC,
                conditionDesc = category.getDescription(),
                humidity = humidity,
                windKmH = wind,
                uvIndex = uv,
                rainProbability = rainProb
            )
            _aiInsightsState.value = insights
            _isAiLoading.value = false
        }
    }

    fun searchCity(query: String) {
        if (query.length < 2) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isSearching.value = true
            _searchResults.value = repository.searchCities(query)
            _isSearching.value = false
        }
    }

    fun saveCityToFavorites(location: CityLocation, customTag: String? = null) {
        viewModelScope.launch {
            repository.saveCity(location, customTag)
        }
    }

    fun deleteCityFromFavorites(cityId: String) {
        viewModelScope.launch {
            repository.deleteSavedCity(cityId)
        }
    }

    // Temperature formatting utility
    fun formatTemp(celsius: Double): String {
        return if (_isCelsius.value) {
            "${celsius.toInt()}°C"
        } else {
            val f = (celsius * 9 / 5) + 32
            "${f.toInt()}°F"
        }
    }
}
