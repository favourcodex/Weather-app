package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyItem
import com.example.data.model.WeatherCategory
import com.example.data.model.parseWeatherCode
import com.example.ui.theme.AetherCardBackground
import com.example.ui.theme.AetherCardBorder
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.FrostWhite
import com.example.ui.theme.SolarGold

@Composable
fun DailyForecastList(
    dailyItems: List<DailyItem>,
    formatTemp: (Double) -> String,
    modifier: Modifier = Modifier
) {
    if (dailyItems.isEmpty()) return

    // Find min and max temp across 7 days for range calculation
    val overallMin = dailyItems.minOfOrNull { it.minTempC } ?: 0.0
    val overallMax = dailyItems.maxOfOrNull { it.maxTempC } ?: 30.0
    val rangeSpan = (overallMax - overallMin).coerceAtLeast(1.0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(AetherCardBackground)
            .border(1.dp, AetherCardBorder, RoundedCornerShape(28.dp))
            .padding(20.dp)
            .testTag("daily_forecast_card")
    ) {
        Column {
            Text(
                text = "7-DAY FORECAST",
                color = FrostWhite.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            dailyItems.forEachIndexed { index, item ->
                DailyItemRow(
                    item = item,
                    overallMin = overallMin,
                    rangeSpan = rangeSpan,
                    formatTemp = formatTemp
                )
                if (index < dailyItems.size - 1) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(vertical = 6.dp)
                            .background(Color(0x1AFFFFFF))
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyItemRow(
    item: DailyItem,
    overallMin: Double,
    rangeSpan: Double,
    formatTemp: (Double) -> String
) {
    val category = parseWeatherCode(item.weatherCode, true)

    val iconVector = when (category) {
        WeatherCategory.CLEAR_DAY -> Icons.Default.WbSunny
        WeatherCategory.PARTLY_CLOUDY_DAY -> Icons.Default.WbCloudy
        WeatherCategory.CLOUDY, WeatherCategory.FOG -> Icons.Default.Cloud
        WeatherCategory.DRIZZLE, WeatherCategory.RAIN -> Icons.Default.Grain
        WeatherCategory.HEAVY_RAIN, WeatherCategory.THUNDERSTORM -> Icons.Default.Thunderstorm
        WeatherCategory.SNOW -> Icons.Default.AcUnit
        else -> Icons.Default.WbSunny
    }

    val iconTint = when (category) {
        WeatherCategory.CLEAR_DAY -> SolarGold
        WeatherCategory.DRIZZLE, WeatherCategory.RAIN, WeatherCategory.HEAVY_RAIN -> CyanGlow
        else -> FrostWhite
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Day Label & Rain Prob
        Row(
            modifier = Modifier.width(110.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.dateFormatted,
                color = FrostWhite,
                fontSize = 14.sp,
                fontWeight = if (item.dateFormatted == "Today") FontWeight.Bold else FontWeight.Medium
            )
            if (item.popMax > 20) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = "Rain",
                    tint = CyanGlow,
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = "${item.popMax}%",
                    color = CyanGlow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Icon
        Icon(
            imageVector = iconVector,
            contentDescription = category.name,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Min Temp
        Text(
            text = formatTemp(item.minTempC),
            color = FrostWhite.copy(alpha = 0.6f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(42.dp)
        )

        // Temperature Range Visual Bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0x33FFFFFF))
        ) {
            val startWeight = ((item.minTempC - overallMin) / rangeSpan).coerceIn(0.0, 0.9).toFloat()
            val fillWeight = ((item.maxTempC - item.minTempC) / rangeSpan).coerceIn(0.1, 1.0).toFloat()

            Row(modifier = Modifier.fillMaxWidth()) {
                if (startWeight > 0f) {
                    Spacer(modifier = Modifier.weight(startWeight))
                }
                Box(
                    modifier = Modifier
                        .weight(fillWeight)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(CyanGlow, SolarGold)
                            )
                        )
                )
                val remainingWeight = (1f - (startWeight + fillWeight)).coerceAtLeast(0f)
                if (remainingWeight > 0f) {
                    Spacer(modifier = Modifier.weight(remainingWeight))
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Max Temp
        Text(
            text = formatTemp(item.maxTempC),
            color = FrostWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(42.dp)
        )
    }
}
