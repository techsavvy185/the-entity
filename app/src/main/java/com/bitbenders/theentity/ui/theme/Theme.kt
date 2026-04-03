package com.bitbenders.theentity.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EntityColorScheme = darkColorScheme(
    primary = EntityGreen,
    onPrimary = EntityBlack,
    secondary = EntityGreenDim,
    onSecondary = EntityBlack,
    error = EntityRed,
    onError = EntityBlack,
    background = EntityBlack,
    onBackground = EntityGreen,
    surface = EntityBlack,
    onSurface = EntityGreen,
    outline = EntityBorder,
)

@Composable
fun TheEntityTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = EntityColorScheme,
        typography = EntityTypography,
        content = content,
    )
}

