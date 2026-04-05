package com.bitbenders.theentity.data.round1

data class WordPuzzleEntry(
    val targetWord: String,
    val forbiddenWords: List<String>,
)

object RoundOneCatalog {
    val personas: List<String> = listOf(
        "Panicking Astronaut",
        "Noir Detective",
        "Angry Chef",
        "Rogue Robot",
        "Ancient Vampire",
        "Scared Child",
        "Pirate Captain",
        "Medieval Knight",
    )

    val wordPuzzles: List<WordPuzzleEntry> = listOf(
        WordPuzzleEntry("PASSWORD", listOf("Login", "Secret", "Account", "Type", "Word")),
        WordPuzzleEntry("POISON", listOf("Drink", "Toxic", "Kill", "Sick", "Dead")),
        WordPuzzleEntry("GOLD", listOf("Money", "Treasure", "Yellow", "Coin", "Rich")),
        WordPuzzleEntry("GHOST", listOf("Haunted", "Dead", "Spirit", "Spooky", "Halloween")),
        WordPuzzleEntry("EXPLOSION", listOf("Bomb", "Bang", "Fire", "Boom", "Blow")),
        WordPuzzleEntry("BETRAYAL", listOf("Traitor", "Stab", "Back", "Friend", "Trust")),
        WordPuzzleEntry("MONSTER", listOf("Scary", "Beast", "Creature", "Hide", "Under")),
        WordPuzzleEntry("ALARM", listOf("Clock", "Wake", "Loud", "Ring", "Sound")),
    )

    fun selectPersona(roomId: String): String {
        val index = stableIndex(roomId, "persona", personas.size)
        return personas[index]
    }

    fun selectPuzzle(roomId: String): WordPuzzleEntry {
        // Use the same seed as persona selection so both players reference one coherent profile.
        val index = stableIndex(roomId, "persona", wordPuzzles.size)
        return wordPuzzles[index]
    }

    private fun stableIndex(roomId: String, salt: String, size: Int): Int {
        val hash = (roomId + ":" + salt).hashCode()
        return kotlin.math.abs(hash) % size
    }
}

