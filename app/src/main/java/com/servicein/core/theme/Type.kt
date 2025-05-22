package com.servicein.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.servicein.R

val PlusJakartaSans = FontFamily(
    Font(R.font.plusjakartasans_extralight, FontWeight.ExtraLight),
    Font(R.font.plusjakartasans_light, FontWeight.Light),
    Font(R.font.plusjakartasans_regular, FontWeight.Normal),
    Font(R.font.plusjakartasans_medium, FontWeight.Medium),
    Font(R.font.plusjakartasans_semibold, FontWeight.SemiBold),
    Font(R.font.plusjakartasans_bold, FontWeight.Bold),
    Font(R.font.plusjakartasans_extrabold, FontWeight.ExtraBold)
)

private val defaultTypography = Typography()

val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal
    ),
    displayMedium = defaultTypography.displayMedium.copy(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal
    ),
    displaySmall = defaultTypography.displaySmall.copy(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal
    ),
    headlineLarge = defaultTypography.headlineLarge.copy(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Bold
    ),
    headlineMedium = defaultTypography.headlineMedium.copy(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.SemiBold
    ),
    headlineSmall = defaultTypography.headlineSmall.copy(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Bold
    ),
    titleLarge = defaultTypography.titleLarge.copy(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Bold
    ),
    titleMedium = defaultTypography.titleMedium.copy(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.SemiBold
    ),
    titleSmall = defaultTypography.titleSmall.copy(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Medium
    ),
    bodyLarge = defaultTypography.bodyLarge.copy(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = defaultTypography.bodyMedium.copy(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal
    ),
    bodySmall = defaultTypography.bodySmall.copy(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Light
    ),
    labelLarge = defaultTypography.labelLarge.copy(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.SemiBold
    ),
    labelMedium = defaultTypography.labelMedium.copy(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Medium
    ),
    labelSmall = defaultTypography.labelSmall.copy(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Light
    )
)