package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.SavedCityEntity
import com.example.data.model.CityLocation
import com.example.ui.theme.AetherBackgroundDark
import com.example.ui.theme.AetherCardBackground
import com.example.ui.theme.AetherCardBorder
import com.example.ui.theme.AuroraGreen
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.FrostWhite
import com.example.ui.theme.SolarGold
import com.example.ui.theme.SunsetRose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedLocationsSheet(
    sheetState: SheetState,
    currentLocation: CityLocation,
    savedCities: List<SavedCityEntity>,
    presetCities: List<CityLocation>,
    searchResults: List<CityLocation>,
    isSearching: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSelectLocation: (CityLocation) -> Unit,
    onSaveLocation: (CityLocation) -> Unit,
    onDeleteSavedLocation: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AetherBackgroundDark,
        contentColor = FrostWhite,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .fillMaxHeight(0.85f)
        ) {
            // Sheet Header Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationCity,
                        contentDescription = "Locations",
                        tint = SolarGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CHOOSE LOCATION",
                        color = FrostWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_locations_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = FrostWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    onSearchQueryChange(it)
                },
                placeholder = {
                    Text(
                        text = "Search city worldwide (e.g. Tokyo, Munich, Sydney)",
                        color = FrostWhite.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = CyanGlow
                    )
                },
                trailingIcon = {
                    if (isSearching) {
                        CircularProgressIndicator(
                            color = CyanGlow,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            onSearchQueryChange("")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = FrostWhite
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = AetherCardBackground,
                    unfocusedContainerColor = AetherCardBackground,
                    focusedBorderColor = CyanGlow,
                    unfocusedBorderColor = AetherCardBorder,
                    focusedTextColor = FrostWhite,
                    unfocusedTextColor = FrostWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("city_search_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Section A: Search Results
                if (searchQuery.isNotBlank() && searchResults.isNotEmpty()) {
                    item {
                        SectionHeaderTitle(title = "SEARCH RESULTS (${searchResults.size})")
                    }
                    items(searchResults) { result ->
                        CityResultCard(
                            cityName = result.name,
                            countryName = result.country,
                            isSelected = result.id == currentLocation.id,
                            onSelect = {
                                onSelectLocation(result)
                                onDismiss()
                            },
                            onBookmark = {
                                onSaveLocation(result)
                            }
                        )
                    }
                }

                // Section B: Saved Favorites List
                if (savedCities.isNotEmpty() && searchQuery.isBlank()) {
                    item {
                        SectionHeaderTitle(title = "SAVED FAVORITE LOCATIONS")
                    }
                    items(savedCities) { entity ->
                        val cityLoc = CityLocation(
                            id = entity.id,
                            name = entity.name,
                            country = entity.country,
                            lat = entity.lat,
                            lon = entity.lon,
                            customTag = entity.customTag
                        )
                        SavedCityCard(
                            entity = entity,
                            isSelected = entity.id == currentLocation.id,
                            onSelect = {
                                onSelectLocation(cityLoc)
                                onDismiss()
                            },
                            onDelete = {
                                onDeleteSavedLocation(entity.id)
                            }
                        )
                    }
                }

                // Section C: Global Featured Presets
                if (searchQuery.isBlank()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeaderTitle(title = "POPULAR METROPOLITAN HUBS")
                    }
                    items(presetCities) { preset ->
                        CityResultCard(
                            cityName = preset.name,
                            countryName = preset.country,
                            isSelected = preset.id == currentLocation.id,
                            onSelect = {
                                onSelectLocation(preset)
                                onDismiss()
                            },
                            onBookmark = {
                                onSaveLocation(preset)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeaderTitle(title: String) {
    Text(
        text = title,
        color = FrostWhite.copy(alpha = 0.6f),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
    )
}

@Composable
private fun CityResultCard(
    cityName: String,
    countryName: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onBookmark: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) Color(0x3338BDF8) else AetherCardBackground)
            .border(
                width = 1.dp,
                color = if (isSelected) CyanGlow else AetherCardBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onSelect() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location Pin",
                    tint = if (isSelected) SolarGold else CyanGlow,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = cityName,
                        color = FrostWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = countryName,
                        color = FrostWhite.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SolarGold.copy(alpha = 0.2f))
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = SolarGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    IconButton(onClick = onBookmark) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Bookmark",
                            tint = FrostWhite.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedCityCard(
    entity: SavedCityEntity,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) Color(0x3338BDF8) else AetherCardBackground)
            .border(
                width = 1.dp,
                color = if (isSelected) CyanGlow else AetherCardBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onSelect() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "Saved City",
                    tint = SolarGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = entity.name,
                            color = FrostWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (!entity.customTag.isNullOrBlank()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyanGlow.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = entity.customTag,
                                    color = CyanGlow,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                    Text(
                        text = entity.country,
                        color = FrostWhite.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete City",
                    tint = SunsetRose.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
