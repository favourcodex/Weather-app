package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CityLocation
import com.example.ui.theme.AetherCardBackground
import com.example.ui.theme.AetherCardBorder
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.FrostWhite
import com.example.ui.theme.SolarGold

@Composable
fun HeaderLocationBar(
    currentLocation: CityLocation,
    isCelsius: Boolean,
    onToggleUnit: () -> Unit,
    onOpenSearch: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isRefreshing) 360f else 0f,
        animationSpec = tween(durationMillis = 600),
        finishedListener = { isRefreshing = false },
        label = "refreshRotation"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Location selector pill
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(AetherCardBackground)
                .border(1.dp, AetherCardBorder, RoundedCornerShape(24.dp))
                .clickable { onOpenSearch() }
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .testTag("location_pill_button"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location Pin",
                tint = CyanGlow,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = currentLocation.name,
                    color = FrostWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (currentLocation.country.isNotBlank()) {
                    Text(
                        text = currentLocation.country,
                        color = FrostWhite.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Action Buttons: Unit Toggle, Search, Refresh
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // °C / °F Unit Toggle Button
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(AetherCardBackground)
                    .border(1.dp, AetherCardBorder, CircleShape)
                    .clickable { onToggleUnit() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("unit_toggle_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isCelsius) "°C" else "°F",
                    color = SolarGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Search Button
            IconButton(
                onClick = onOpenSearch,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AetherCardBackground)
                    .border(1.dp, AetherCardBorder, CircleShape)
                    .testTag("search_city_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Cities",
                    tint = FrostWhite,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Refresh Button
            IconButton(
                onClick = {
                    isRefreshing = true
                    onRefresh()
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AetherCardBackground)
                    .border(1.dp, AetherCardBorder, CircleShape)
                    .testTag("refresh_weather_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh Weather",
                    tint = FrostWhite,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(rotationAngle)
                )
            }
        }
    }
}
