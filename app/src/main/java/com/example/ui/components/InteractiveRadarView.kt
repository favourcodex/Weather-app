package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CityLocation
import com.example.ui.theme.AetherCardBackground
import com.example.ui.theme.AetherCardBorder
import com.example.ui.theme.AuroraGreen
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.FrostWhite
import com.example.ui.theme.SolarGold
import com.example.ui.theme.SunsetRose
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun InteractiveRadarView(
    location: CityLocation,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(true) }
    var selectedLayer by remember { mutableStateOf("Precipitation") }

    val infiniteTransition = rememberInfiniteTransition(label = "RadarScan")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepAngle"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(AetherCardBackground)
            .border(1.dp, AetherCardBorder, RoundedCornerShape(28.dp))
            .testTag("interactive_radar_view")
    ) {
        Column {
            // Radar Header Control Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = "Radar",
                        tint = CyanGlow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "DOPPLER SATELLITE RADAR",
                            color = FrostWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Live HD Precipitation & Wind Sweep",
                            color = FrostWhite.copy(alpha = 0.6f),
                            fontSize = 10.sp
                        )
                    }
                }

                // Play / Pause Toggle
                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                        .testTag("radar_play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Toggle Radar Animation",
                        tint = SolarGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Layer Selector Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Precipitation", "Wind Flow", "Cloud Cover", "Temperature").forEach { layer ->
                    val isSelected = layer == selectedLayer
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) CyanGlow.copy(alpha = 0.25f) else Color(0x1AFFFFFF))
                            .border(1.dp, if (isSelected) CyanGlow else AetherCardBorder, RoundedCornerShape(16.dp))
                            .clickable { selectedLayer = layer }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = layer,
                            color = if (isSelected) CyanGlow else FrostWhite.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Radar Display Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            ) {
                // Background Satellite Image
                Image(
                    painter = painterResource(id = R.drawable.img_radar_bg_1784731042909),
                    contentDescription = "Satellite Radar Grid",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                // Dark vignette gradient overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.Transparent, Color(0xAA0B0F19))
                            )
                        )
                )

                // Canvas Doppler Scan Sweeper & Location Pin
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height
                    val center = Offset(w / 2f, h / 2f)
                    val maxRadius = w.coerceAtMost(h) * 0.45f

                    // Concentric radar grid rings
                    for (i in 1..3) {
                        drawCircle(
                            color = CyanGlow.copy(alpha = 0.15f),
                            radius = maxRadius * (i / 3f),
                            center = center,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
                        )
                    }

                    // Crosshair grid lines
                    drawLine(
                        color = CyanGlow.copy(alpha = 0.15f),
                        start = Offset(center.x, center.y - maxRadius),
                        end = Offset(center.x, center.y + maxRadius),
                        strokeWidth = 1f
                    )
                    drawLine(
                        color = CyanGlow.copy(alpha = 0.15f),
                        start = Offset(center.x - maxRadius, center.y),
                        end = Offset(center.x + maxRadius, center.y),
                        strokeWidth = 1f
                    )

                    // Rotating Radar Sweep Line
                    if (isPlaying) {
                        val rad = Math.toRadians(sweepAngle.toDouble())
                        val sweepX = (center.x + maxRadius * cos(rad)).toFloat()
                        val sweepY = (center.y + maxRadius * sin(rad)).toFloat()

                        drawLine(
                            color = CyanGlow,
                            start = center,
                            end = Offset(sweepX, sweepY),
                            strokeWidth = 2.5f
                        )

                        // Radar sweep cone glow
                        drawArc(
                            color = CyanGlow.copy(alpha = 0.12f),
                            startAngle = sweepAngle - 30f,
                            sweepAngle = 30f,
                            useCenter = true,
                            topLeft = Offset(center.x - maxRadius, center.y - maxRadius),
                            size = androidx.compose.ui.geometry.Size(maxRadius * 2, maxRadius * 2)
                        )
                    }

                    // Pulsing Location Marker Pin
                    drawCircle(
                        color = SolarGold.copy(alpha = 0.3f),
                        radius = 12f * pulseScale,
                        center = center
                    )
                    drawCircle(
                        color = SolarGold,
                        radius = 6f,
                        center = center
                    )
                }

                // City Pin Badge Overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xAA000000))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Current City",
                        tint = SolarGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${location.name} Radar",
                        color = FrostWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Radar Map Legend Bar
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xAA000000))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Light",
                        color = FrostWhite.copy(alpha = 0.6f),
                        fontSize = 9.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(AuroraGreen, SolarGold, SunsetRose)
                                )
                            )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Heavy",
                        color = FrostWhite.copy(alpha = 0.6f),
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}
