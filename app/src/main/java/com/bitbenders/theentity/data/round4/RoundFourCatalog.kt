package com.bitbenders.theentity.data.round4

enum class ButtonPosition(val row: Int, val col: Int) {
    TOP_LEFT(0, 0),
    TOP_RIGHT(0, 1),
    MIDDLE_LEFT(1, 0),
    MIDDLE_RIGHT(1, 1),
    BOTTOM_LEFT(2, 0),
    BOTTOM_RIGHT(2, 1),
}

data class DisplayEntry(
    val displayWord: String,
    val position: ButtonPosition,
)

object RoundFourCatalog {

    val displayEntries: List<DisplayEntry> = listOf(
        DisplayEntry("YES", ButtonPosition.MIDDLE_LEFT),
        DisplayEntry("FIRST", ButtonPosition.TOP_RIGHT),
        DisplayEntry("DISPLAY", ButtonPosition.BOTTOM_RIGHT),
        DisplayEntry("OKAY", ButtonPosition.TOP_RIGHT),
        DisplayEntry("SAYS", ButtonPosition.BOTTOM_RIGHT),
        DisplayEntry("NOTHING", ButtonPosition.MIDDLE_LEFT),
        DisplayEntry("", ButtonPosition.BOTTOM_LEFT),
        DisplayEntry("BLANK", ButtonPosition.MIDDLE_RIGHT),
        DisplayEntry("NO", ButtonPosition.BOTTOM_RIGHT),
        DisplayEntry("LED", ButtonPosition.MIDDLE_LEFT),
        DisplayEntry("LEAD", ButtonPosition.BOTTOM_RIGHT),
        DisplayEntry("READ", ButtonPosition.MIDDLE_RIGHT),
        DisplayEntry("RED", ButtonPosition.MIDDLE_RIGHT),
        DisplayEntry("REED", ButtonPosition.BOTTOM_LEFT),
        DisplayEntry("LEED", ButtonPosition.BOTTOM_LEFT),
        DisplayEntry("HOLD ON", ButtonPosition.BOTTOM_RIGHT),
        DisplayEntry("YOU", ButtonPosition.MIDDLE_RIGHT),
        DisplayEntry("YOU ARE", ButtonPosition.BOTTOM_RIGHT),
        DisplayEntry("YOUR", ButtonPosition.MIDDLE_RIGHT),
        DisplayEntry("YOU'RE", ButtonPosition.MIDDLE_RIGHT),
        DisplayEntry("UR", ButtonPosition.TOP_LEFT),
        DisplayEntry("THERE", ButtonPosition.BOTTOM_RIGHT),
        DisplayEntry("THEY'RE", ButtonPosition.BOTTOM_LEFT),
        DisplayEntry("THEIR", ButtonPosition.MIDDLE_RIGHT),
        DisplayEntry("THEY ARE", ButtonPosition.MIDDLE_LEFT),
        DisplayEntry("SEE", ButtonPosition.BOTTOM_RIGHT),
        DisplayEntry("C", ButtonPosition.TOP_RIGHT),
        DisplayEntry("CEE", ButtonPosition.BOTTOM_RIGHT),
    )

    val priorityEntries: List<PriorityEntry> = listOf(
        PriorityEntry("READY", listOf("YES", "OKAY", "WHAT", "MIDDLE", "LEFT", "PRESS", "RIGHT", "BLANK")),
        PriorityEntry("FIRST", listOf("LEFT", "OKAY", "YES", "MIDDLE", "NO", "RIGHT", "NOTHING", "UHHH")),
        PriorityEntry("NO", listOf("BLANK", "UHHH", "WAIT", "FIRST", "WHAT", "READY", "RIGHT", "YES")),
        PriorityEntry("BLANK", listOf("WAIT", "RIGHT", "OKAY", "MIDDLE", "BLANK", "PRESS", "READY", "NOTHING")),
        PriorityEntry("YES", listOf("OKAY", "RIGHT", "UHHH", "MIDDLE", "FIRST", "WHAT", "PRESS", "READY")),
        PriorityEntry("LEFT", listOf("RIGHT", "LEFT", "FIRST", "NO", "MIDDLE", "YES", "BLANK", "WHAT")),
        PriorityEntry("RIGHT", listOf("YES", "NOTHING", "READY", "PRESS", "NO", "WAIT", "WHAT", "RIGHT")),
        PriorityEntry("MIDDLE", listOf("BLANK", "READY", "OKAY", "WHAT", "NOTHING", "PRESS", "NO", "WAIT")),
        PriorityEntry("OKAY", listOf("MIDDLE", "NO", "FIRST", "YES", "UHHH", "NOTHING", "WAIT", "OKAY")),
        PriorityEntry("YOU", listOf("SURE", "YOU ARE", "YOUR", "YOU'RE", "NEXT", "UH HUH", "UR", "HOLD")),
        PriorityEntry("YOU ARE", listOf("YOUR", "NEXT", "LIKE", "UH HUH", "WHAT?", "DONE", "UH UH", "HOLD")),
        PriorityEntry("YOUR", listOf("UH UH", "YOU ARE", "UH HUH", "YOUR", "NEXT", "UR", "SURE", "U")),
        PriorityEntry("YOU'RE", listOf("YOU", "YOU'RE", "UR", "NEXT", "UH UH", "YOU ARE", "U", "YOUR")),
        PriorityEntry("UR", listOf("DONE", "U", "UR", "UH HUH", "WHAT?", "SURE", "YOUR", "HOLD")),
        PriorityEntry("WHAT?", listOf("YOU", "HOLD", "YOU'RE", "YOUR", "U", "DONE", "UH UH", "LIKE")),
        PriorityEntry("DONE", listOf("SURE", "UH HUH", "NEXT", "WHAT?", "YOUR", "UR", "YOU'RE", "HOLD")),
    )
}

data class PriorityEntry(
    val label: String,
    val priority: List<String>,
)
