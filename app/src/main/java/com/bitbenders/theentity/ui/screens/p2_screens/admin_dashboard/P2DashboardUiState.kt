package com.bitbenders.theentity.ui.screens.p2_screens.admin_dashboard

data class PersonaTabEntry(
    val persona: String,
    val targetWord: String,
    val forbiddenWords: List<String>,
)

data class P2DashboardUiState(
    val missionSecondsRemaining: Int = 5 * 60,
    val strikes: Int = 0,
    val maxStrikes: Int = 3,
    val tuningValue: Int = 50,
    val currentDialValue: Float = 50f,
    val keypadSequence: List<String> = emptyList(),
    val isPersonaRevealPressed: Boolean = false,
    val isDissectionRevealPressed: Boolean = false,
    val isDataIndexRevealPressed: Boolean = false,
    val activePersonaEntry: PersonaTabEntry? = null,
    val personaEntries: List<PersonaTabEntry> = emptyList(),
    val actionLog: List<String> = listOf(
        "> ARMOROS LINK ESTABLISHED",
        "> OPERATOR CONSOLE READY"
    )
) {
    val keypadInput: String
        get() = keypadSequence.joinToString(" ")
}
