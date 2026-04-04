package com.bitbenders.theentity.ui.screens.p2_screens.admin_manual

data class RuleEntry(
    val title: String,
    val details: String
)

data class P2ManualUiState(
    val personaOverrides: List<RuleEntry> = listOf(
        RuleEntry("PANICKING ASTRONAUT", "Target: PASSWORD | Forbidden: Login, Secret, Account, Type, Word"),
        RuleEntry("NOIR DETECTIVE", "Target: POISON | Forbidden: Drink, Toxic, Kill, Sick, Dead"),
        RuleEntry("MAD SCIENTIST", "Target: ALIEN | Forbidden: Space, UFO, Planet, Mars, Extraterrestrial"),
        RuleEntry("ANGRY CHEF", "Target: GOLD | Forbidden: Money, Treasure, Yellow, Coin, Rich"),
        RuleEntry("ROGUE ROBOT", "Target: GHOST | Forbidden: Haunted, Dead, Spirit, Spooky, Halloween"),
        RuleEntry("OLD WEST COWBOY", "Target: FREEZE | Forbidden: Cold, Ice, Winter, Stop, Snow"),
        RuleEntry("ANCIENT VAMPIRE", "Target: EXPLOSION | Forbidden: Bomb, Bang, Fire, Boom, Blow"),
        RuleEntry("SCARED CHILD", "Target: BETRAYAL | Forbidden: Traitor, Stab, Back, Friend, Trust"),
        RuleEntry("EVIL SUPERVILLAIN", "Target: TRAFFIC | Forbidden: Cars, Road, Drive, Street, Stuck"),
        RuleEntry("STRESSED OFFICE WORKER", "Target: VIRUS | Forbidden: Computer, Sick, Bug, Software, Hacker"),
        RuleEntry("CLUELESS TOURIST", "Target: RESCUE | Forbidden: Help, Save, Hero, Danger, Trap"),
        RuleEntry("PIRATE CAPTAIN", "Target: MONSTER | Forbidden: Scary, Beast, Creature, Hide, Under"),
        RuleEntry("ZOMBIE SURVIVOR", "Target: PRISON | Forbidden: Jail, Cell, Bars, Escape, Cops"),
        RuleEntry("TECH SUPPORT AGENT", "Target: MIRROR | Forbidden: Glass, Look, Reflection, See, Face"),
        RuleEntry("MEDIEVAL KNIGHT", "Target: ALARM | Forbidden: Clock, Wake, Loud, Ring, Sound")
    ),
    val cipherRules: List<RuleEntry> = listOf(
        RuleEntry("THE OCEAN", "Extract the last 4 letters of the longest word."),
        RuleEntry("MATHEMATICS", "First letter of each prime number mentioned."),
        RuleEntry("ANIMALS", "Name the apex predator of the described biome.")
    )
)

