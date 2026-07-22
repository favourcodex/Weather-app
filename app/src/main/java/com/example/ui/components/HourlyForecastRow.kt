package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HourlyItem
import com.example.data.model.WeatherCategory
import com.example.data.model.parseWeatherCode
import com.example.ui.theme.AetherCardBackground
import com.example.ui.theme.AetherCardBorder
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.FrostWhite
import com.example.ui.theme.SolarGold

@Composable
fun HourlyForecastRow(
    hourlyItems: List<HourlyItem>,
    formatTemp: (Double) -> String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "HOURLY FORECAST",
            color = FrostWhite.copy(alpha = 0.7f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.testTag("hourly_forecast_row")
        ) {
            items(hourlyItems) { item ->
                HourlyItemCard(item = item, formatTemp = formatTemp)
            }
        }
    }
}

@Composable
private fun HourlyItemCard(
    item: HourlyItem,
    formatTemp: (Double) -> String
) {
    val category = parseWeatherCode(item.weatherCode, item.isDay)

    val iconVector = when (category) {
        WeatherCategory.CLEAR_DAY -> Icons.Default.WbSunny
        WeatherCategory.CLEAR_NIGHT -> Icons.Default.Nightlight
        WeatherCategory.PARTLY_CLOUDY_DAY, WeatherCategory.PARTLY_CLOUDY_NIGHT -> Icons.Default.WbCloudy
        WeatherCategory.CLOUDY, WeatherCategory.FOG -> Icons.Default.Cloud
        WeatherCategory.DRIZZLE, WeatherCategory.RAIN -> Icons.Default.Grain
        WeatherCategory.HEAVY_RAIN, WeatherCategory.THUNDERSTORM -> Icons.Default.Thunderstorm
        WeatherCategory.SNOW -> Icons.Default.AcUnit
        else -> Icons.Default.WbSunny
    }

    val iconTint = when (category) {
        WeatherCategory.CLEAR_DAY -> SolarGold
        WeatherCategory.CLEAR_NIGHT -> Color(0xFFC7D2FE)
        WeatherCategory.DRIZZLE, WeatherCategory.RAIN, WeatherCategory.HEAVY_RAIN -> CyanGlow
        else -> FrostWhite
    }

    val isNow = item.timeFormatted.equals("Now", ignoreCase = true)

    Box(
        modifier = Modifier
            .width(72.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(if (isNow) Color(0x3338BDF8) else AetherCardBackground)
            .border(
                width = 1.dp,
                color = if (isNow) CyanGlow else AetherCardBorder,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(vertical = 14.dp, horizontal = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = item.timeFormatted,
                color = if (isNow) CyanGlow else FrostWhite.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = if (isNow) FontWeight.Bold else FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Icon(
                imageVector = iconVector,
                contentDescription = category.name,
                tint = iconTint,
                modifier = Modifier.size(26.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Precipitation probability badge
            if (item.pop > 10) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Rain",
                        tint = CyanGlow,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = "${item.pop}%",
                        color = CyanGlow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(14.dp))
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = formatTemp(item.tempC),
                color = FrostWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
