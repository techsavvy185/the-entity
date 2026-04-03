package com.bitbenders.theentity.ui.screens.p1_screens.anomalies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bitbenders.theentity.ui.components.staticNoise
import com.bitbenders.theentity.ui.theme.EntityBlack

@Composable
fun P1StaticScreen(
    intensity: Float,
    modifier: Modifier = Modifier
) {
    if (intensity <= 0f) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EntityBlack)
            .staticNoise(intensity)
    )
}

