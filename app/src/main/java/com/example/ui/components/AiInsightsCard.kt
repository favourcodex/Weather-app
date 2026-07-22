package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import androidx.compose.ui.unit.sp
import com.example.data.api.AiInsightsResult
import com.example.ui.theme.AetherCardBackground
import com.example.ui.theme.AetherCardBorder
import com.example.ui.theme.AuroraGreen
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.FrostWhite
import com.example.ui.theme.SolarGold
import com.example.ui.theme.SunsetRose

@Composable
fun AiInsightsCard(
    insights: AiInsightsResult?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0x331E1B4B), Color(0x1A0F172A))
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(SolarGold.copy(alpha = 0.5f), CyanGlow.copy(alpha = 0.3f))
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .testTag("ai_insights_card")
    ) {
        Column {
            // Header Image Banner with Overlay Title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_weather_banner_1784731031886),
                    contentDescription = "Aether AI Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color(0x22000000), Color(0xDD0B0F19))
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x40000000))
                            .border(1.dp, SolarGold, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Aether AI",
                                tint = SolarGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AETHER AI ADVISOR",
                                color = SolarGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp
                            )
                        }
                    }
                }
            }

            // Body Content
            Column(modifier = Modifier.padding(20.dp)) {
                if (isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        CircularProgressIndicator(
                            color = SolarGold,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Synthesizing AI forecast & outfit advice...",
                            color = FrostWhite.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                    }
                } else if (insights != null) {
                    // Section 1: AI Weather Overview
                    Text(
                        text = "FORECAST SUMMARY",
                        color = FrostWhite.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = insights.summary,
                        color = FrostWhite,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Section 2: Outfit & Gear Recommendation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0x26000000))
                            .border(1.dp, AetherCardBorder, RoundedCornerShape(18.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Checkroom,
                                    contentDescription = "Outfit",
                                    tint = CyanGlow,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "RECOMMENDED OUTFIT & GEAR",
                                    color = CyanGlow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = insights.outfitAdvice,
                                color = FrostWhite.copy(alpha = 0.95f),
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Section 3: Activity Suitability Grid
                    Text(
                        text = "ACTIVITY SUITABILITY",
                        color = FrostWhite.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val activitiesList = parseActivitiesText(insights.activitiesText)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        activitiesList.chunked(2).forEach { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                pair.forEach { activity ->
                                    ActivityChip(
                                        title = activity.first,
                                        rating = activity.second,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (pair.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityChip(
    title: String,
    rating: String,
    modifier: Modifier = Modifier
) {
    val (ratingColor, icon) = when {
        rating.equals("Ideal", true) -> AuroraGreen to Icons.Default.CheckCircle
        rating.equals("Good", true) -> SolarGold to Icons.Default.CheckCircle
        rating.equals("Moderate", true) -> CyanGlow to Icons.Default.LightMode
        else -> SunsetRose to Icons.Default.CheckCircle
    }

    val activityIcon = when {
        title.contains("Running", true) -> Icons.Default.DirectionsRun
        title.contains("Dining", true) -> Icons.Default.Restaurant
        title.contains("Photo", true) -> Icons.Default.PhotoCamera
        else -> Icons.Default.Stars
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x20FFFFFF))
            .border(1.dp, AetherCardBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = activityIcon,
                contentDescription = title,
                tint = FrostWhite,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                color = FrostWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(ratingColor.copy(alpha = 0.2f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = rating,
                color = ratingColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

private fun parseActivitiesText(text: String): List<Pair<String, String>> {
    if (text.isBlank()) return listOf(
        "Running" to "Good",
        "Outdoor Dining" to "Moderate",
        "Photography" to "Ideal",
        "Stargazing" to "Moderate"
    )

    val items = text.split("|")
    val list = mutableListOf<Pair<String, String>>()
    for (item in items) {
        val parts = item.split(":")
        if (parts.size >= 2) {
            val title = parts[0].trim()
            val rating = parts[1].trim()
            list.add(title to rating)
        }
    }
    return if (list.isNotEmpty()) list else listOf(
        "Running" to "Good",
        "Outdoor Dining" to "Moderate",
        "Photography" to "Ideal",
        "Stargazing" to "Moderate"
    )
}
