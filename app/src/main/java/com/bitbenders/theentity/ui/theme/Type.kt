package com.bitbenders.theentity.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bitbenders.theentity.R

private val Vt323Family = FontFamily(
    Font(resId = R.font.vt323, weight = FontWeight.Normal),
)

private val ShareTechMonoFamily = FontFamily(
    Font(resId = R.font.share_tech_mono, weight = FontWeight.Normal),
)

val EntityTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Vt323Family,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp,
        lineHeight = 52.sp,
        letterSpacing = 1.5.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = Vt323Family,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 36.sp,
        letterSpacing = 1.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = ShareTechMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = ShareTechMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.3.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = ShareTechMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.2.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = ShareTechMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.4.sp,
    ),
)
