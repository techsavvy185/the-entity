package com.bitbenders.theentity.ui.screens.p1_screens.anomalies

data class P1AnomalyUiState(
    val staticIntensity: Float = 0f,
    val lockedGlyphs: List<String> = emptyList(),
    val isLockdownActive: Boolean = false
)

