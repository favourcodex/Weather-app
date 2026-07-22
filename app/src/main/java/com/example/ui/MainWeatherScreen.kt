package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.WeatherCategory
import com.example.ui.components.AiInsightsCard
import com.example.ui.components.AtmosphericBackground
import com.example.ui.components.DailyForecastList
import com.example.ui.components.HeaderLocationBar
import com.example.ui.components.HourlyForecastRow
import com.example.ui.components.InteractiveRadarView
import com.example.ui.components.MainWeatherCard
import com.example.ui.components.SavedLocationsSheet
import com.example.ui.components.WeatherMetricsGrid
import com.example.ui.theme.AetherCardBackground
import com.example.ui.theme.AetherCardBorder
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.FrostWhite
import com.example.ui.theme.SolarGold
import com.example.ui.viewmodel.WeatherUiState
import com.example.ui.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainWeatherScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()
    val isCelsius by viewModel.isCelsius.collectAsStateWithLifecycle()
    val weatherState by viewModel.weatherUiState.collectAsStateWithLifecycle()
    val aiInsights by viewModel.aiInsightsState.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val savedCities by viewModel.savedCities.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Overview, 1: Radar, 2: AI Insights
    var showLocationsSheet by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentCategory = (weatherState as? WeatherUiState.Success)?.currentWeatherCategory ?: WeatherCategory.CLEAR_DAY

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Live Atmospheric Animated Canvas Background
            AtmosphericBackground(category = currentCategory)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                    .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
            ) {
                // Top Header Bar
                HeaderLocationBar(
                    currentLocation = currentLocation,
                    isCelsius = isCelsius,
                    onToggleUnit = { viewModel.toggleUnit() },
                    onOpenSearch = { showLocationsSheet = true },
                    onRefresh = { viewModel.refreshWeather() }
                )

                // Main Navigation Tabs (Overview, Radar, AI Insights)
                TabSelectionBar(
                    activeTab = activeTab,
                    onTabSelected = { activeTab = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Content
                when (val state = weatherState) {
                    is WeatherUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = SolarGold,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Fetching live satellite data for ${currentLocation.name}...",
                                    color = FrostWhite.copy(alpha = 0.8f),
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    is WeatherUiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(AetherCardBackground)
                                    .border(1.dp, AetherCardBorder, RoundedCornerShape(24.dp))
                                    .padding(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Error",
                                    tint = SolarGold,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Weather Connection Error",
                                    color = FrostWhite,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = state.message,
                                    color = FrostWhite.copy(alpha = 0.7f),
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.refreshWeather() },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanGlow),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Retry",
                                        tint = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Try Again", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    is WeatherUiState.Success -> {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(bottom = 24.dp)
                        ) {
                            when (activeTab) {
                                0 -> {
                                    // Tab 0: Overview
                                    MainWeatherCard(
                                        current = state.weather.current,
                                        todayDaily = state.dailyList.firstOrNull(),
                                        category = state.currentWeatherCategory,
                                        formatTemp = { viewModel.formatTemp(it) }
                                    )

                                    HourlyForecastRow(
                                        hourlyItems = state.hourlyList,
                                        formatTemp = { viewModel.formatTemp(it) }
                                    )

                                    DailyForecastList(
                                        dailyItems = state.dailyList,
                                        formatTemp = { viewModel.formatTemp(it) }
                                    )

                                    WeatherMetricsGrid(
                                        weather = state.weather,
                                        airQuality = state.airQuality,
                                        todayDaily = state.dailyList.firstOrNull()
                                    )
                                }

                                1 -> {
                                    // Tab 1: Interactive Radar
                                    InteractiveRadarView(location = currentLocation)

                                    WeatherMetricsGrid(
                                        weather = state.weather,
                                        airQuality = state.airQuality,
                                        todayDaily = state.dailyList.firstOrNull()
                                    )
                                }

                                2 -> {
                                    // Tab 2: AI Insights & Outfit Recommendations
                                    AiInsightsCard(
                                        insights = aiInsights,
                                        isLoading = isAiLoading
                                    )

                                    HourlyForecastRow(
                                        hourlyItems = state.hourlyList,
                                        formatTemp = { viewModel.formatTemp(it) }
                                    )

                                    DailyForecastList(
                                        dailyItems = state.dailyList,
                                        formatTemp = { viewModel.formatTemp(it) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Locations Bottom Sheet
            if (showLocationsSheet) {
                SavedLocationsSheet(
                    sheetState = sheetState,
                    currentLocation = currentLocation,
                    savedCities = savedCities,
                    presetCities = viewModel.presetCities,
                    searchResults = searchResults,
                    isSearching = isSearching,
                    onSearchQueryChange = { viewModel.searchCity(it) },
                    onSelectLocation = { loc -> viewModel.selectLocation(loc) },
                    onSaveLocation = { loc -> viewModel.saveCityToFavorites(loc) },
                    onDeleteSavedLocation = { id -> viewModel.deleteCityFromFavorites(id) },
                    onDismiss = { showLocationsSheet = false }
                )
            }
        }
    }
}

@Composable
private fun TabSelectionBar(
    activeTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(AetherCardBackground)
            .border(1.dp, AetherCardBorder, RoundedCornerShape(24.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(
            Triple(0, "Overview", Icons.Default.Cloud),
            Triple(1, "Radar", Icons.Default.Radar),
            Triple(2, "AI Insights", Icons.Default.AutoAwesome)
        ).forEach { (index, title, icon) ->
            val isSelected = activeTab == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) Color(0x4038BDF8) else Color.Transparent)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) CyanGlow else Color.Transparent,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 10.dp)
                    .testTag("weather_tab_$index"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isSelected) SolarGold else FrostWhite.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        color = if (isSelected) FrostWhite else FrostWhite.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
