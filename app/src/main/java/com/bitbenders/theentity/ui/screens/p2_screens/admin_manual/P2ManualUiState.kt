package com.bitbenders.theentity.ui.screens.p2_screens.admin_manual

data class RuleEntry(
    val title: String,
    val details: String
)

data class P2ManualUiState(
    val personaOverrides: List<RuleEntry> = listOf(
        RuleEntry("PEASANT", "Target: Harvest | Forbidden: Kill, Die, Escape"),
        RuleEntry("SOLDIER", "Target: Lantern | Forbidden: Fire, Light, Burn"),
        RuleEntry("DETECTIVE", "Target: Anchor | Forbidden: Ocean, Sea, Boat"),
        RuleEntry("JUDGE", "Target: Verdict | Forbidden: Judge, Court, Trial")
    ),
    val cipherRules: List<RuleEntry> = listOf(
        RuleEntry("THE OCEAN", "Extract the last 4 letters of the longest word."),
        RuleEntry("MATHEMATICS", "First letter of each prime number mentioned."),
        RuleEntry("ANIMALS", "Name the apex predator of the described biome.")
    )
)

