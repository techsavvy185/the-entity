package com.bitbenders.theentity.ui.screens.p2_screens.admin_manual

data class RuleEntry(
    val title: String,
    val details: String
)

data class P2ManualUiState(
    val personaOverrides: List<RuleEntry> = listOf(
        RuleEntry("PANICKING ASTRONAUT", "Target: PASSWORD | Forbidden: Login, Secret, Account, Type, Word"),
        RuleEntry("NOIR DETECTIVE", "Target: POISON | Forbidden: Drink, Toxic, Kill, Sick, Dead"),
        RuleEntry("ANGRY CHEF", "Target: GOLD | Forbidden: Money, Treasure, Yellow, Coin, Rich"),
        RuleEntry("ROGUE ROBOT", "Target: GHOST | Forbidden: Haunted, Dead, Spirit, Spooky, Halloween"),
        RuleEntry("ANCIENT VAMPIRE", "Target: EXPLOSION | Forbidden: Bomb, Bang, Fire, Boom, Blow"),
        RuleEntry("SCARED CHILD", "Target: BETRAYAL | Forbidden: Traitor, Stab, Back, Friend, Trust"),
        RuleEntry("PIRATE CAPTAIN", "Target: MONSTER | Forbidden: Scary, Beast, Creature, Hide, Under"),
        RuleEntry("MEDIEVAL KNIGHT", "Target: ALARM | Forbidden: Clock, Wake, Loud, Ring, Sound")
    )
)

