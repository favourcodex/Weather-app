package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyItem
import com.example.data.model.OpenMeteoAirQualityResponse
import com.example.data.model.OpenMeteoWeatherResponse
import com.example.ui.theme.AetherCardBackground
import com.example.ui.theme.AetherCardBorder
import com.example.ui.theme.AuroraGreen
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.FrostWhite
import com.example.ui.theme.SolarGold
import com.example.ui.theme.SunsetRose

@Composable
fun WeatherMetricsGrid(
    weather: OpenMeteoWeatherResponse,
    airQuality: OpenMeteoAirQualityResponse?,
    todayDaily: DailyItem?,
    modifier: Modifier = Modifier
) {
    val current = weather.current
    val hourlyVisibility = weather.hourly?.visibility?.firstOrNull() ?: 10000.0
    val usAqi = airQuality?.current?.usAqi?.toInt() ?: 28
    val uvIndex = todayDaily?.uvMax ?: 4.0
    val windDirection = current?.windDirection10m ?: 180.0
    val windSpeed = current?.windSpeed10m?.toInt() ?: 12
    val gusts = current?.windGusts10m?.toInt() ?: 18
    val pressure = current?.pressureMsl?.toInt() ?: 1014

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("weather_metrics_grid")
    ) {
        Text(
            text = "ATMOSPHERIC METRICS",
            color = FrostWhite.copy(alpha = 0.7f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
        )

        // Row 1: Air Quality & UV Index
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AirQualityCard(
                aqi = usAqi,
                pm25 = airQuality?.current?.pm25 ?: 6.2,
                modifier = Modifier.weight(1f)
            )

            UvIndexCard(
                uvIndex = uvIndex,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 2: Wind Compass & Sunrise/Sunset Arc
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WindCompassCard(
                speedKmH = windSpeed,
                gustsKmH = gusts,
                directionDegree = windDirection,
                modifier = Modifier.weight(1f)
            )

            SunCycleCard(
                sunrise = todayDaily?.sunrise ?: "06:15 AM",
                sunset = todayDaily?.sunset ?: "08:30 PM",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 3: Visibility & Pressure
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            VisibilityCard(
                visibilityMeters = hourlyVisibility,
                modifier = Modifier.weight(1f)
            )

            PressureCard(
                pressureHpa = pressure,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MetricCardContainer(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(AetherCardBackground)
            .border(1.dp, AetherCardBorder, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = CyanGlow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title.uppercase(),
                    color = FrostWhite.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun AirQualityCard(
    aqi: Int,
    pm25: Double,
    modifier: Modifier = Modifier
) {
    val (status, color) = when {
        aqi <= 50 -> "Good" to AuroraGreen
        aqi <= 100 -> "Moderate" to SolarGold
        aqi <= 150 -> "Unhealthy for Sensitive Groups" to SunsetRose
        else -> "Unhealthy" to SunsetRose
    }

    MetricCardContainer(title = "Air Quality", icon = Icons.Default.FilterDrama, modifier = modifier) {
        Text(
            text = "$aqi AQI",
            color = FrostWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = status,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        // AQI level bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0x33FFFFFF))
        ) {
            val progress = (aqi / 200f).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "PM2.5: ${pm25.toInt()} µg/m³",
            color = FrostWhite.copy(alpha = 0.5f),
            fontSize = 10.sp
        )
    }
}

@Composable
private fun UvIndexCard(
    uvIndex: Double,
    modifier: Modifier = Modifier
) {
    val (level, color) = when {
        uvIndex < 3 -> "Low" to AuroraGreen
        uvIndex < 6 -> "Moderate" to SolarGold
        uvIndex < 8 -> "High" to SunsetRose
        else -> "Very High" to SunsetRose
    }

    MetricCardContainer(title = "UV Index", icon = Icons.Default.WbSunny, modifier = modifier) {
        Text(
            text = "${uvIndex.toInt()}",
            color = FrostWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = level,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0x33FFFFFF))
        ) {
            val progress = (uvIndex / 11.0).toFloat().coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(AuroraGreen, SolarGold, SunsetRose)
                        )
                    )
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (uvIndex >= 6) "Sun protection needed" else "Low hazard outdoors",
            color = FrostWhite.copy(alpha = 0.5f),
            fontSize = 10.sp
        )
    }
}

@Composable
private fun WindCompassCard(
    speedKmH: Int,
    gustsKmH: Int,
    directionDegree: Double,
    modifier: Modifier = Modifier
) {
    MetricCardContainer(title = "Wind", icon = Icons.Default.Air, modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "$speedKmH km/h",
                    color = FrostWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Gusts to $gustsKmH km/h",
                    color = FrostWhite.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }

            // Rotating compass arrow indicator
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = "Wind Direction",
                    tint = CyanGlow,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(directionDegree.toFloat())
                )
            }
        }
    }
}

@Composable
private fun SunCycleCard(
    sunrise: String,
    sunset: String,
    modifier: Modifier = Modifier
) {
    MetricCardContainer(title = "Sun Cycle", icon = Icons.Default.WbTwilight, modifier = modifier) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Sunrise: ",
                    color = FrostWhite.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
                Text(
                    text = sunrise,
                    color = SolarGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Sunset: ",
                    color = FrostWhite.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
                Text(
                    text = sunset,
                    color = SunsetRose,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Arc Sun Curve Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            ) {
                val w = size.width
                val h = size.height
                drawArc(
                    color = Color(0x33FFFFFF),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    style = Stroke(width = 3f, cap = StrokeCap.Round),
                    size = Size(w, h * 1.8f),
                    topLeft = Offset(0f, 0f)
                )
                // Sun position indicator
                drawCircle(
                    color = SolarGold,
                    radius = 5f,
                    center = Offset(w * 0.6f, h * 0.3f)
                )
            }
        }
    }
}

@Composable
private fun VisibilityCard(
    visibilityMeters: Double,
    modifier: Modifier = Modifier
) {
    val km = (visibilityMeters / 1000.0).coerceAtLeast(0.1)
    val desc = if (km >= 10) "Unlimited view" else "Reduced visibility"

    MetricCardContainer(title = "Visibility", icon = Icons.Default.Visibility, modifier = modifier) {
        Text(
            text = "${km.toInt()} km",
            color = FrostWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = desc,
            color = FrostWhite.copy(alpha = 0.6f),
            fontSize = 11.sp
        )
    }
}

@Composable
private fun PressureCard(
    pressureHpa: Int,
    modifier: Modifier = Modifier
) {
    val status = if (pressureHpa >= 1013) "High Pressure" else "Low Pressure"

    MetricCardContainer(title = "Pressure", icon = Icons.Default.Compress, modifier = modifier) {
        Text(
            text = "$pressureHpa hPa",
            color = FrostWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = status,
            color = CyanGlow,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
