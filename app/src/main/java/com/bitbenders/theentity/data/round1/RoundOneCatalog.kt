package com.bitbenders.theentity.data.round1

data class WordPuzzleEntry(
    val targetWord: String,
    val forbiddenWords: List<String>,
)

object RoundOneCatalog {
    val personas: List<String> = listOf(
        "Panicking Astronaut",
        "Noir Detective",
        "Mad Scientist",
        "Angry Chef",
        "Rogue Robot",
        "Old West Cowboy",
        "Ancient Vampire",
        "Scared Child",
        "Evil Supervillain",
        "Stressed Office Worker",
        "Clueless Tourist",
        "Pirate Captain",
        "Zombie Survivor",
        "Tech Support Agent",
        "Medieval Knight",
    )

    val wordPuzzles: List<WordPuzzleEntry> = listOf(
        WordPuzzleEntry("PASSWORD", listOf("Login", "Secret", "Account", "Type", "Word")),
        WordPuzzleEntry("POISON", listOf("Drink", "Toxic", "Kill", "Sick", "Dead")),
        WordPuzzleEntry("ALIEN", listOf("Space", "UFO", "Planet", "Mars", "Extraterrestrial")),
        WordPuzzleEntry("GOLD", listOf("Money", "Treasure", "Yellow", "Coin", "Rich")),
        WordPuzzleEntry("GHOST", listOf("Haunted", "Dead", "Spirit", "Spooky", "Halloween")),
        WordPuzzleEntry("FREEZE", listOf("Cold", "Ice", "Winter", "Stop", "Snow")),
        WordPuzzleEntry("EXPLOSION", listOf("Bomb", "Bang", "Fire", "Boom", "Blow")),
        WordPuzzleEntry("BETRAYAL", listOf("Traitor", "Stab", "Back", "Friend", "Trust")),
        WordPuzzleEntry("TRAFFIC", listOf("Cars", "Road", "Drive", "Street", "Stuck")),
        WordPuzzleEntry("VIRUS", listOf("Computer", "Sick", "Bug", "Software", "Hacker")),
        WordPuzzleEntry("RESCUE", listOf("Help", "Save", "Hero", "Danger", "Trap")),
        WordPuzzleEntry("MONSTER", listOf("Scary", "Beast", "Creature", "Hide", "Under")),
        WordPuzzleEntry("PRISON", listOf("Jail", "Cell", "Bars", "Escape", "Cops")),
        WordPuzzleEntry("MIRROR", listOf("Glass", "Look", "Reflection", "See", "Face")),
        WordPuzzleEntry("ALARM", listOf("Clock", "Wake", "Loud", "Ring", "Sound")),
    )

    fun selectPersona(roomId: String): String {
        val index = stableIndex(roomId, "persona", personas.size)
        return personas[index]
    }

    fun selectPuzzle(roomId: String): WordPuzzleEntry {
        val index = stableIndex(roomId, "puzzle", wordPuzzles.size)
        return wordPuzzles[index]
    }

    private fun stableIndex(roomId: String, salt: String, size: Int): Int {
        val hash = (roomId + ":" + salt).hashCode()
        return kotlin.math.abs(hash) % size
    }
}

