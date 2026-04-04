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
        PriorityEntry("READY", listOf("YES", "OKAY", "WHAT", "MIDDLE", "LEFT", "PRESS", "RIGHT", "BLANK", "READY", "NO", "FIRST", "UHHH", "NOTHING", "WAIT")),
        PriorityEntry("FIRST", listOf("LEFT", "OKAY", "YES", "MIDDLE", "NO", "RIGHT", "NOTHING", "UHHH", "WAIT", "READY", "BLANK", "WHAT", "PRESS", "FIRST")),
        PriorityEntry("NO", listOf("BLANK", "UHHH", "WAIT", "FIRST", "WHAT", "READY", "RIGHT", "YES", "NOTHING", "LEFT", "PRESS", "OKAY", "NO", "MIDDLE")),
        PriorityEntry("BLANK", listOf("WAIT", "RIGHT", "OKAY", "MIDDLE", "BLANK", "PRESS", "READY", "NOTHING", "NO", "WHAT", "LEFT", "UHHH", "YES", "FIRST")),
        PriorityEntry("YES", listOf("OKAY", "RIGHT", "UHHH", "MIDDLE", "FIRST", "WHAT", "PRESS", "READY", "NOTHING", "YES", "LEFT", "BLANK", "NO", "WAIT")),
        PriorityEntry("WHAT", listOf("UHHH", "WHAT", "LEFT", "NOTHING", "READY", "BLANK", "MIDDLE", "NO", "OKAY", "FIRST", "WAIT", "YES", "PRESS", "RIGHT")),
        PriorityEntry("LEFT", listOf("RIGHT", "LEFT", "FIRST", "NO", "MIDDLE", "YES", "BLANK", "WHAT", "UHHH", "WAIT", "PRESS", "READY", "OKAY", "NOTHING")),
        PriorityEntry("RIGHT", listOf("YES", "NOTHING", "READY", "PRESS", "NO", "WAIT", "WHAT", "RIGHT", "MIDDLE", "LEFT", "UHHH", "BLANK", "OKAY", "FIRST")),
        PriorityEntry("MIDDLE", listOf("BLANK", "READY", "OKAY", "WHAT", "NOTHING", "PRESS", "NO", "WAIT", "LEFT", "MIDDLE", "RIGHT", "FIRST", "UHHH", "YES")),
        PriorityEntry("OKAY", listOf("MIDDLE", "NO", "FIRST", "YES", "UHHH", "NOTHING", "WAIT", "OKAY", "LEFT", "READY", "BLANK", "PRESS", "WHAT", "RIGHT")),
        PriorityEntry("PRESS", listOf("RIGHT", "MIDDLE", "YES", "READY", "PRESS", "OKAY", "NOTHING", "UHHH", "BLANK", "LEFT", "FIRST", "WHAT", "NO", "WAIT")),
        PriorityEntry("YOU", listOf("SURE", "YOU ARE", "YOUR", "YOU'RE", "NEXT", "UH HUH", "UR", "HOLD", "WHAT?", "YOU", "UH UH", "LIKE", "DONE", "U")),
        PriorityEntry("YOU ARE", listOf("YOUR", "NEXT", "LIKE", "UH HUH", "WHAT?", "DONE", "UH UH", "HOLD", "YOU", "U", "YOU'RE", "SURE", "UR", "YOU ARE")),
        PriorityEntry("YOUR", listOf("UH UH", "YOU ARE", "UH HUH", "YOUR", "NEXT", "UR", "SURE", "U", "YOU'RE", "YOU", "WHAT?", "HOLD", "LIKE", "DONE")),
        PriorityEntry("YOU'RE", listOf("YOU", "YOU'RE", "UR", "NEXT", "UH UH", "YOU ARE", "U", "YOUR", "WHAT?", "UH HUH", "SURE", "DONE", "LIKE", "HOLD")),
        PriorityEntry("UR", listOf("DONE", "U", "UR", "UH HUH", "WHAT?", "SURE", "YOUR", "HOLD", "YOU'RE", "LIKE", "NEXT", "UH UH", "YOU ARE", "YOU")),
        PriorityEntry("UH HUH", listOf("UH HUH", "YOUR", "YOU ARE", "YOU", "DONE", "HOLD", "UH UH", "NEXT", "SURE", "LIKE", "YOU'RE", "UR", "U", "WHAT?")),
        PriorityEntry("WHAT?", listOf("YOU", "HOLD", "YOU'RE", "YOUR", "U", "DONE", "UH UH", "LIKE", "YOU ARE", "UH HUH", "UR", "NEXT", "WHAT?", "SURE")),
        PriorityEntry("DONE", listOf("SURE", "UH HUH", "NEXT", "WHAT?", "YOUR", "UR", "YOU'RE", "HOLD", "LIKE", "YOU", "U", "YOU ARE", "UH UH", "DONE")),
        PriorityEntry("NEXT", listOf("WHAT?", "UH HUH", "UH UH", "YOUR", "HOLD", "SURE", "NEXT", "LIKE", "DONE", "YOU ARE", "UR", "YOU'RE", "U", "YOU")),
        PriorityEntry("HOLD", listOf("YOU ARE", "U", "DONE", "UH UH", "YOU", "UR", "SURE", "WHAT?", "YOU'RE", "NEXT", "HOLD", "UH HUH", "YOUR", "LIKE")),
        PriorityEntry("LIKE", listOf("YOU'RE", "NEXT", "U", "UR", "HOLD", "DONE", "UH UH", "WHAT?", "UH HUH", "YOU", "LIKE", "SURE", "YOU ARE", "YOUR")),
    )
}

data class PriorityEntry(
    val label: String,
    val priority: List<String>,
)
