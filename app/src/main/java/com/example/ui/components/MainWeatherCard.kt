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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
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
import com.example.data.model.CurrentUnitsAndValues
import com.example.data.model.DailyItem
import com.example.data.model.WeatherCategory
import com.example.data.model.getDescription
import com.example.ui.theme.AetherCardBackground
import com.example.ui.theme.AetherCardBorder
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.FrostWhite
import com.example.ui.theme.SolarGold

@Composable
fun MainWeatherCard(
    current: CurrentUnitsAndValues?,
    todayDaily: DailyItem?,
    category: WeatherCategory,
    formatTemp: (Double) -> String,
    modifier: Modifier = Modifier
) {
    val tempC = current?.temperature2m ?: 0.0
    val feelsLikeC = current?.apparentTemperature ?: tempC
    val humidity = current?.relativeHumidity2m?.toInt() ?: 0
    val windKmH = current?.windSpeed10m?.toInt() ?: 0

    val maxTempC = todayDaily?.maxTempC ?: (tempC + 3)
    val minTempC = todayDaily?.minTempC ?: (tempC - 3)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x26FFFFFF),
                        Color(0x0FFFFFFF)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x40FFFFFF),
                        Color(0x10FFFFFF)
                    )
                ),
                shape = RoundedCornerShape(32.dp)
            )
            .padding(24.dp)
            .testTag("main_weather_card")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Weather Condition Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AetherCardBackground)
                    .border(1.dp, AetherCardBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = category.getDescription().uppercase(),
                    color = SolarGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Temp Display
            Text(
                text = formatTemp(tempC),
                color = FrostWhite,
                fontSize = 72.sp,
                fontWeight = FontWeight.Thin,
                letterSpacing = (-2).sp
            )

            // High / Low & Feels like
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "H: ${formatTemp(maxTempC)}",
                    color = FrostWhite.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "L: ${formatTemp(minTempC)}",
                    color = FrostWhite.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "•  Feels like ${formatTemp(feelsLikeC)}",
                    color = CyanGlow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Metrics Row (Humidity, Wind, Pressure)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x1A000000))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Humidity
                MetricSummaryPill(
                    icon = Icons.Default.WaterDrop,
                    label = "Humidity",
                    value = "$humidity%"
                )

                // Wind
                MetricSummaryPill(
                    icon = Icons.Default.Air,
                    label = "Wind",
                    value = "$windKmH km/h"
                )

                // UV / UV Max
                MetricSummaryPill(
                    icon = Icons.Default.Thermostat,
                    label = "UV Index",
                    value = "${todayDaily?.uvMax?.toInt() ?: 3}"
                )
            }
        }
    }
}

@Composable
private fun MetricSummaryPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = CyanGlow,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = value,
                color = FrostWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = FrostWhite.copy(alpha = 0.6f),
                fontSize = 10.sp
            )
        }
    }
}
