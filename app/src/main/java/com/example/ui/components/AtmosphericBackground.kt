package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.data.model.WeatherCategory
import com.example.ui.theme.AetherBackgroundDark
import com.example.ui.theme.ClearDayEnd
import com.example.ui.theme.ClearDayStart
import com.example.ui.theme.ClearNightEnd
import com.example.ui.theme.ClearNightStart
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.DarkNightCanvas
import com.example.ui.theme.RainEnd
import com.example.ui.theme.RainStart
import com.example.ui.theme.SnowEnd
import com.example.ui.theme.SnowStart
import com.example.ui.theme.SolarGold
import kotlin.random.Random

@Composable
fun AtmosphericBackground(
    category: WeatherCategory,
    modifier: Modifier = Modifier
) {
    val gradientBrush = remember(category) {
        when (category) {
            WeatherCategory.CLEAR_DAY -> Brush.verticalGradient(listOf(ClearDayStart, ClearDayEnd, AetherBackgroundDark))
            WeatherCategory.CLEAR_NIGHT -> Brush.verticalGradient(listOf(ClearNightStart, ClearNightEnd, DarkNightCanvas))
            WeatherCategory.PARTLY_CLOUDY_DAY -> Brush.verticalGradient(listOf(Color(0xFF1E293B), Color(0xFF334155), AetherBackgroundDark))
            WeatherCategory.PARTLY_CLOUDY_NIGHT -> Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), DarkNightCanvas))
            WeatherCategory.CLOUDY, WeatherCategory.FOG -> Brush.verticalGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
            WeatherCategory.DRIZZLE, WeatherCategory.RAIN, WeatherCategory.HEAVY_RAIN, WeatherCategory.THUNDERSTORM -> Brush.verticalGradient(listOf(RainStart, RainEnd, DarkNightCanvas))
            WeatherCategory.SNOW -> Brush.verticalGradient(listOf(SnowStart, SnowEnd, AetherBackgroundDark))
            WeatherCategory.WINDY -> Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B), DarkNightCanvas))
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "AtmosphericAnimation")

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val movementProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "movementProgress"
    )

    // Pre-generate random particle coordinates
    val particleOffsets = remember {
        List(40) {
            Pair(Random.nextFloat(), Random.nextFloat())
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            when (category) {
                WeatherCategory.CLEAR_DAY -> {
                    // Pulsing Solar Glow at top right
                    drawCircle(
                        color = SolarGold.copy(alpha = pulseAlpha * 0.25f),
                        radius = canvasWidth * 0.6f,
                        center = Offset(canvasWidth * 0.85f, canvasHeight * 0.15f)
                    )
                    drawCircle(
                        color = Color(0xFFFFE066).copy(alpha = pulseAlpha * 0.4f),
                        radius = canvasWidth * 0.35f,
                        center = Offset(canvasWidth * 0.85f, canvasHeight * 0.15f)
                    )
                }

                WeatherCategory.CLEAR_NIGHT -> {
                    // Twinkling stars
                    particleOffsets.forEachIndexed { index, (rx, ry) ->
                        val starX = rx * canvasWidth
                        val starY = ry * canvasHeight
                        val starAlpha = (pulseAlpha + (index % 5) * 0.1f) % 0.8f
                        drawCircle(
                            color = Color.White.copy(alpha = starAlpha),
                            radius = if (index % 3 == 0) 2.5f else 1.5f,
                            center = Offset(starX, starY)
                        )
                    }
                }

                WeatherCategory.RAIN, WeatherCategory.DRIZZLE, WeatherCategory.HEAVY_RAIN, WeatherCategory.THUNDERSTORM -> {
                    // Falling rain streaks
                    val speed = if (category == WeatherCategory.HEAVY_RAIN) 1.5f else 1.0f
                    particleOffsets.forEach { (rx, ry) ->
                        val startX = rx * canvasWidth
                        val yOffset = ((ry + movementProgress * speed) % 1f) * canvasHeight
                        val streakLength = if (category == WeatherCategory.HEAVY_RAIN) 40f else 22f

                        drawLine(
                            color = CyanGlow.copy(alpha = 0.5f),
                            start = Offset(startX, yOffset),
                            end = Offset(startX - 5f, yOffset + streakLength),
                            strokeWidth = 2.5f
                        )
                    }
                }

                WeatherCategory.SNOW -> {
                    // Floating snow particles
                    particleOffsets.forEach { (rx, ry) ->
                        val snowX = (rx * canvasWidth + kotlin.math.sin((ry + movementProgress) * Math.PI * 2) * 20).toFloat()
                        val snowY = ((ry + movementProgress * 0.5f) % 1f) * canvasHeight
                        drawCircle(
                            color = Color.White.copy(alpha = 0.7f),
                            radius = (Random.nextInt(2, 5)).toFloat(),
                            center = Offset(snowX, snowY)
                        )
                    }
                }

                else -> {
                    // Subtle ambient aura
                    drawCircle(
                        color = CyanGlow.copy(alpha = pulseAlpha * 0.12f),
                        radius = canvasWidth * 0.5f,
                        center = Offset(canvasWidth * 0.5f, canvasHeight * 0.3f)
                    )
                }
            }
        }
    }
}
