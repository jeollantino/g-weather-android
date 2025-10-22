package com.jeollantino.gweather.ui.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector

val Moon: ImageVector
    get() {
        if (_moon != null) {
            return _moon!!
        }
        _moon = materialIcon(name = "Filled.Moon") {
            materialPath {
                // Crescent moon shape - open side facing right
                moveTo(12f, 2f)
                curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
                curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f)
                curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
                curveTo(22f, 6.48f, 17.52f, 2f, 12f, 2f)
                close()
                
                // Inner crescent cutout
                moveTo(12f, 4f)
                curveTo(16.42f, 4f, 20f, 7.58f, 20f, 12f)
                curveTo(20f, 16.42f, 16.42f, 20f, 12f, 20f)
                curveTo(7.58f, 20f, 4f, 16.42f, 4f, 12f)
                curveTo(4f, 7.58f, 7.58f, 4f, 12f, 4f)
                close()
            }
        }
        return _moon!!
    }

private var _moon: ImageVector? = null

val Sunrise: ImageVector
    get() {
        if (_sunrise != null) {
            return _sunrise!!
        }
        _sunrise = materialIcon(name = "Filled.Sunrise") {
            materialPath {
                // Sun circle (partially above horizon)
                moveTo(12f, 4f)
                curveTo(9.24f, 4f, 7f, 6.24f, 7f, 9f)
                curveTo(7f, 11.76f, 9.24f, 14f, 12f, 14f)
                curveTo(14.76f, 14f, 17f, 11.76f, 17f, 9f)
                curveTo(17f, 6.24f, 14.76f, 4f, 12f, 4f)
                close()

                // Top ray (pointing up)
                moveTo(11f, 1f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(-2f)
                close()

                // Top-left ray
                moveTo(5.64f, 3.64f)
                lineTo(7.05f, 5.05f)
                lineTo(5.64f, 6.46f)
                lineTo(4.22f, 5.05f)
                close()

                // Top-right ray
                moveTo(18.36f, 3.64f)
                lineTo(19.78f, 5.05f)
                lineTo(18.36f, 6.46f)
                lineTo(16.95f, 5.05f)
                close()

                // Left ray
                moveTo(2f, 8f)
                horizontalLineToRelative(3f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(-3f)
                close()

                // Right ray
                moveTo(19f, 8f)
                horizontalLineToRelative(3f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(-3f)
                close()

                // Horizon line (ground)
                moveTo(2f, 16f)
                horizontalLineToRelative(20f)
                verticalLineToRelative(1.5f)
                horizontalLineToRelative(-20f)
                close()

                // Arrow pointing up (sunrise indicator)
                moveTo(12f, 18f)
                lineTo(9f, 21f)
                horizontalLineToRelative(6f)
                close()
            }
        }
        return _sunrise!!
    }

private var _sunrise: ImageVector? = null

val Sunset: ImageVector
    get() {
        if (_sunset != null) {
            return _sunset!!
        }
        _sunset = materialIcon(name = "Filled.Sunset") {
            materialPath {
                // Sun circle (partially below horizon)
                moveTo(12f, 10f)
                curveTo(9.24f, 10f, 7f, 12.24f, 7f, 15f)
                curveTo(7f, 17.76f, 9.24f, 20f, 12f, 20f)
                curveTo(14.76f, 20f, 17f, 17.76f, 17f, 15f)
                curveTo(17f, 12.24f, 14.76f, 10f, 12f, 10f)
                close()

                // Top ray (pointing up)
                moveTo(11f, 7f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(-2f)
                close()

                // Top-left ray
                moveTo(5.64f, 9.64f)
                lineTo(7.05f, 11.05f)
                lineTo(5.64f, 12.46f)
                lineTo(4.22f, 11.05f)
                close()

                // Top-right ray
                moveTo(18.36f, 9.64f)
                lineTo(19.78f, 11.05f)
                lineTo(18.36f, 12.46f)
                lineTo(16.95f, 11.05f)
                close()

                // Left ray
                moveTo(2f, 14f)
                horizontalLineToRelative(3f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(-3f)
                close()

                // Right ray
                moveTo(19f, 14f)
                horizontalLineToRelative(3f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(-3f)
                close()

                // Horizon line (ground)
                moveTo(2f, 16f)
                horizontalLineToRelative(20f)
                verticalLineToRelative(1.5f)
                horizontalLineToRelative(-20f)
                close()

                // Arrow pointing down (sunset indicator)
                moveTo(12f, 21f)
                lineTo(15f, 18f)
                horizontalLineToRelative(-6f)
                close()
            }
        }
        return _sunset!!
    }

private var _sunset: ImageVector? = null

val LocationPin: ImageVector
    get() {
        if (_locationPin != null) {
            return _locationPin!!
        }
        _locationPin = materialIcon(name = "Filled.LocationPin") {
            materialPath {
                // Location pin shape
                moveTo(12f, 2f)
                curveTo(8.13f, 2f, 5f, 5.13f, 5f, 9f)
                curveTo(5f, 14.25f, 12f, 22f, 12f, 22f)
                curveTo(12f, 22f, 19f, 14.25f, 19f, 9f)
                curveTo(19f, 5.13f, 15.87f, 2f, 12f, 2f)
                close()
                
                // Inner circle
                moveTo(12f, 6f)
                curveTo(13.66f, 6f, 15f, 7.34f, 15f, 9f)
                curveTo(15f, 10.66f, 13.66f, 12f, 12f, 12f)
                curveTo(10.34f, 12f, 9f, 10.66f, 9f, 9f)
                curveTo(9f, 7.34f, 10.34f, 6f, 12f, 6f)
                close()
            }
        }
        return _locationPin!!
    }

private var _locationPin: ImageVector? = null

@Composable
fun WeatherIconWithGlow(
    icon: ImageVector,
    contentDescription: String?,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    tint: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified
) {
    val glowColor = remember { androidx.compose.ui.graphics.Color(0x8014D0E6) }
    
    androidx.compose.foundation.layout.Box(
        modifier = modifier,
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        // Glow layer (blurred shadow)
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = null,
            tint = glowColor,
            modifier = androidx.compose.ui.Modifier
                .graphicsLayer {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                        renderEffect = androidx.compose.ui.graphics.BlurEffect(
                            radiusX = 8f,
                            radiusY = 8f,
                            edgeTreatment = androidx.compose.ui.graphics.TileMode.Clamp
                        )
                    }
                }
        )
        
        // Main icon
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = androidx.compose.ui.Modifier
        )
    }
}
