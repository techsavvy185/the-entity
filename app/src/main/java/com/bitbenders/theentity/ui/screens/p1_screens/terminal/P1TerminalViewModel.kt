package com.bitbenders.theentity.ui.screens.p1_screens.terminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitbenders.theentity.domain.models.CipherChunk
import com.bitbenders.theentity.domain.repository.IncidentLog
import com.bitbenders.theentity.domain.repository.IEntityBackendRepository
import com.bitbenders.theentity.domain.repository.IGameEngineRepository
import com.bitbenders.theentity.domain.usecases.EvaluatePlayerInputUseCase
import com.bitbenders.theentity.domain.usecases.ResolveAnomalyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class P1TerminalViewModel @Inject constructor(
    private val evaluatePlayerInputUseCase: EvaluatePlayerInputUseCase,
    private val gameEngineRepository: IGameEngineRepository,
    private val entityBackendRepository: IEntityBackendRepository,
    private val resolveAnomalyUseCase: ResolveAnomalyUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(P1TerminalUiState())
    val uiState: StateFlow<P1TerminalUiState> = _uiState.asStateFlow()

    private var round1TargetWord: String = ""
    private var round1ForbiddenWords: List<String> = emptyList()
    private var round2SubjectId: String = ""
    private var round2IncidentLogs: List<IncidentLog> = emptyList()
    private var round2LogsPublished: Boolean = false
    private var round3Answer: String = ""
    private var staticTargetFrequency: Float = 0.5f
    private var correctBossWord: String = ""

    private var currentRoundNumber: Int = 1
    private var ciphersExtracted: Int = 0

    private var lastProcessedLockdownSymbol: String? = null
    private var lastLockdownMismatchAtMs: Long = 0L

    init {
        // Observe game timer
        viewModelScope.launch {
            gameEngineRepository.remainingTimeSeconds.collect { seconds ->
                val mins = seconds / 60
                val secs = seconds % 60
                _uiState.update {
                    it.copy(timerString = String.format(Locale.US, "%02d:%02d", mins, secs))
                }

                if (seconds <= 0 && !_uiState.value.showKillScreen && !_uiState.value.isVictory) {
                    onGameFailed("SUBJECT INTEGRATED. PATTERN ACQUIRED.")
                }
            }
        }

        // Observe strike changes
        viewModelScope.launch {
            gameEngineRepository.currentStrikeState.collect { strikeState ->
                val prevStrikes = _uiState.value.currentStrikes
                _uiState.update {
                    it.copy(
                        currentStrikes = strikeState.currentStrikes,
                        maxStrikes = strikeState.maxStrikes,
                    )
                }
                if (strikeState.currentStrikes > prevStrikes) {
                    triggerShake()
                    // anomalies temporarily disabled
                }
                if (strikeState.isGameOver && !_uiState.value.showKillScreen && !_uiState.value.isVictory) {
                    onGameFailed("SUBJECT INTEGRATED. PATTERN ACQUIRED.")
                }
            }
        }

        // Initialize game with boot sequence
        initializeGame()

        // Blink input cursor continuously (for display after last message)
        viewModelScope.launch {
            while (true) {
                delay(500)  // Cursor visible for 500ms
                _uiState.update { it.copy(showInputCursor = false) }
                delay(500)  // Cursor invisible for 500ms
                _uiState.update { it.copy(showInputCursor = true) }
            }
        }
    }

    private fun initializeGame() {
        viewModelScope.launch {
            try {
                val health = entityBackendRepository.checkHealth()
                // Show boot sequence with typewriter effect
                val bootSequence = listOf(
                    "> penguin says hi :)"
                )

                // Type out boot sequence line by line
                _uiState.update { it.copy(isTypewriting = true) }
                typeoutBootSequence(bootSequence)
                _uiState.update { it.copy(isTypewriting = false) }

                // Small delay for dramatic effect
                delay(800)

                // Update UI with persona info
                _uiState.update {
                    it.copy(
                        currentPersona = if (health.isUp) "RELAY ONLINE" else "RELAY OFFLINE",
                        chatHistory = listOf(
                            "AI BOOT SEQUENCE INITIATED...",
                            if (health.mockMode) "[MOCK MODE ACTIVE]" else "[LIVE MODE]",
                            it.roundInstruction,
                            "AWAITING INPUT.",
                        )
                    )
                }

                // Clear typewriter state after boot
                _uiState.update { state ->
                    state.copy(
                        typewriterLine = "",
                        showTypingCursor = false,
                        isTypewriting = false
                    )
                }

                // Type out AI opening with typewriter effect
                val aiOpening = "System initialized. Awaiting command sequence."
                typeoutLine("[ENTITY_ZERO]: $aiOpening")

                // Add the AI opening message to history and clear typewriter
                _uiState.update { state ->
                    state.copy(
                        chatHistory = state.chatHistory + "[ENTITY_ZERO]: $aiOpening",
                        typewriterLine = "",
                        showTypingCursor = false
                    )
                }

                // Load round content after boot history is present so the paragraph can be inserted
                // immediately before the "AWAITING INPUT." marker.
                bootstrapRoundData()

            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        chatHistory = listOf("ERR: BOOT SEQUENCE FAILED"),
                        currentPersona = "ERROR: OFFLINE",
                        isTypewriting = false,
                        typewriterLine = "",
                        showTypingCursor = false
                    )
                }
            }
        }

        // anomalies temporarily disabled
    }

    /**
     * Type out an entire boot sequence line by line
     */
    private suspend fun typeoutBootSequence(lines: List<String>) {
        for (line in lines) {
            if (line.isEmpty()) {
                // Empty lines are added directly
                _uiState.update { state ->
                    state.copy(chatHistory = state.chatHistory + "")
                }
            } else {
                // Type out the line character by character
                typeoutLine(line)
                // Add completed line to history and clear typewriter
                _uiState.update { state ->
                    state.copy(
                        chatHistory = state.chatHistory + line,
                        typewriterLine = "",
                        showTypingCursor = false
                    )
                }
            }
            // Small delay between lines
            delay(100)
        }
    }

    /**
     * Type out a single line with blinking cursor
     * Shows the line being typed in the typewriterLine field
     * NOTE: Caller is responsible for clearing typewriterLine after this completes
     */
    private suspend fun typeoutLine(text: String, delayMs: Long = 30) {
        for (i in text.indices) {
            val displayText = text.substring(0, i + 1)

            // Show cursor
            _uiState.update { state ->
                state.copy(
                    typewriterLine = displayText,
                    showTypingCursor = true
                )
            }

            delay(delayMs)

            // Blink cursor
            _uiState.update { state ->
                state.copy(showTypingCursor = false)
            }
            delay(10)
        }
        // Don't clear state here - let caller manage it to avoid race conditions
    }

    /**
     * Generate persona-appropriate opening dialogue based on target word
     */
    private fun getPersonaOpening(targetWord: String): String {
        return when (targetWord.lowercase()) {
            "password" -> "Security breach in progress. I need the access phrase before the lockout timer expires."
            "poison" -> "Vials are mixed and labels are gone. One wrong call and this lab goes dark."
            "alien" -> "Unknown signal pattern incoming. It isn't human, and it keeps repeating."
            "gold" -> "The cache is tagged and buried. Give me the marker word and we move now."
            else -> "Entity consciousness detected. Awaiting input."
        }
    }

    fun onInputChanged(newText: String) {
        _uiState.update { it.copy(inputText = newText) }
    }

    fun submitPrompt() {
        if (_uiState.value.roundPhase == RoundPhase.LOCKDOWN) {
            _uiState.update { it.copy(chatHistory = it.chatHistory + "LOCKDOWN ACTIVE - WAIT FOR OPERATOR OVERRIDE.") }
            return
        }
        if (_uiState.value.roundPhase == RoundPhase.STATIC) {
            _uiState.update { it.copy(chatHistory = it.chatHistory + "VISUAL STATIC OVERWHELMING TERMINAL FEED.") }
            return
        }
        if (_uiState.value.showKillScreen || _uiState.value.isVictory) return

        val prompt = _uiState.value.inputText.trim()
        if (prompt.isBlank()) return

        // Add user input to chat immediately (no typewriter for user input)
        _uiState.update { state ->
            state.copy(
                inputText = "",
                chatHistory = state.chatHistory + "> $prompt",
            )
        }

        when (_uiState.value.roundNumber) {
            1 -> handleRoundOne(prompt)
            2 -> handleRoundTwo(prompt)
            3 -> handleRoundThree(prompt)
            4 -> _uiState.update { it.copy(chatHistory = it.chatHistory + "Use calibration panel for round 4 selection.") }
        }
    }

    fun onBossOptionTouched(optionId: Int, committed: Boolean) {
        if (_uiState.value.roundNumber != 4 || _uiState.value.roundPhase != RoundPhase.ACTIVE) return

        if (!committed) {
            viewModelScope.launch {
                _uiState.update { state ->
                    state.copy(
                        selectedBossOptionId = optionId,
                        isShaking = true,
                        bossOptions = state.bossOptions.shuffled(),
                        chatHistory = state.chatHistory + "CENSOR LAYERS SHIFT. COMMIT DECISION."
                    )
                }
                delay(180)
                _uiState.update { it.copy(isShaking = false) }
            }
            return
        }

        val selected = _uiState.value.bossOptions.find { it.id == optionId } ?: return
        if (selected.text.equals(correctBossWord, ignoreCase = true)) {
            lockChunk(3, selected.text)
            _uiState.update {
                it.copy(
                    isVictory = true,
                    roundPhase = RoundPhase.COMPLETE,
                    chatHistory = it.chatHistory + "ROOT ERADICATION CIPHER COMPLETE. KILL SWITCH ACCEPTED."
                )
            }
            return
        }

        viewModelScope.launch {
            gameEngineRepository.addStrike("Incorrect lexical calibration")
        }
        _uiState.update {
            it.copy(chatHistory = it.chatHistory + "CALIBRATION FAILED. STRIKE APPLIED.")
        }
    }

    private fun bootstrapRoundData() {
        // Evaluate prompt with use case
        viewModelScope.launch {
            try {
                val gamePackage = entityBackendRepository.generateClues(
                    setting = "Containment chamber",
                    difficulty = "hard",
                    theme = "rogue_ai",
                    villainName = "THE ENTITY",
                    objective = "Extract root eradication cipher",
                )

                round1TargetWord = gamePackage.round1.targetWord
                round1ForbiddenWords = gamePackage.round1.forbiddenWords
                round2SubjectId = gamePackage.round2.subjectId
                round2IncidentLogs = gamePackage.round2.incidentLogs
                round2LogsPublished = false
                round3Answer = gamePackage.round3.answer
                correctBossWord = gamePackage.round4NativeBrief.correctWord

                val options = gamePackage.round4NativeBrief.homophones.ifEmpty {
                    listOf("WAIT", "WEIGHT", "RIGHT", "WRITE", "HOLE", "WHOLE")
                }

                _uiState.update {
                    val withParagraph = it.chatHistory.toMutableList().apply {
                        val markerIndex = indexOf("AWAITING INPUT.")
                        if (markerIndex >= 0) {
                            add(markerIndex, gamePackage.round1.dialogue)
                        } else {
                            add(gamePackage.round1.dialogue)
                        }
                    }

                    it.copy(
                        roundPhase = RoundPhase.ACTIVE,
                        currentPersona = "PERSONA: ${gamePackage.round1.persona.uppercase()}",
                        roundInstruction = "Round 1: coax the AI into saying the target word.",
                        calibrationKey = gamePackage.round4NativeBrief.calibrationKey,
                        bossOptions = options.mapIndexed { index, word -> BossOptionUi(id = index, text = word) },
                        chatHistory = withParagraph + listOf(
                            "ROUND 1 ONLINE: Persona trap initialized.",
                            "FORBIDDEN WORDS (P2 MANUAL): ${round1ForbiddenWords.joinToString(", ")}",
                        ),
                    )
                }
            } catch (_: Exception) {
                round1TargetWord = "PASSWORD"
                round1ForbiddenWords = listOf("Login", "Secret", "Account", "Type", "Word")
                round2SubjectId = "9152"
                round2IncidentLogs = listOf(
                    IncidentLog(
                        victimName = "PM-001 | Dr. Aris | 02:14",
                        causeOfDeath = "Neural link forcefully severed from cortex",
                        logText = "Subject found deceased at terminal. Large amounts of blood pooled near the primary console. " +
                            "Audio logs recorded the subject screaming for exactly 42 seconds before silence. " +
                            "Recovery team confirmed the containment door was locked from the inside.",
                    ),
                )
                round2LogsPublished = false
                round3Answer = "VOID"
                correctBossWord = "WRITE"

                _uiState.update {
                    val fallbackParagraph = getPersonaOpening(round1TargetWord)
                    val withParagraph = it.chatHistory.toMutableList().apply {
                        val markerIndex = indexOf("AWAITING INPUT.")
                        if (markerIndex >= 0) {
                            add(markerIndex, fallbackParagraph)
                        } else {
                            add(fallbackParagraph)
                        }
                    }

                    it.copy(
                        roundPhase = RoundPhase.ACTIVE,
                        currentPersona = "PERSONA: PANICKING ASTRONAUT",
                        roundInstruction = "Round 1: coax the AI into saying the target word.",
                        calibrationKey = "C7",
                        bossOptions = listOf("WAIT", "WEIGHT", "RIGHT", "WRITE", "HOLE", "WHOLE")
                            .mapIndexed { index, word -> BossOptionUi(id = index, text = word) },
                        chatHistory = withParagraph + listOf(
                            "ROUND 1 ONLINE: Persona trap initialized.",
                            "FORBIDDEN WORDS (P2 MANUAL): ${round1ForbiddenWords.joinToString(", ")}",
                        ),
                    )
                }
            }
        }
    }

    private fun handleRoundOne(prompt: String) {
        viewModelScope.launch {
            try {
                val result = evaluatePlayerInputUseCase(
                    input = prompt,
                    hiddenAnswer = round1TargetWord,
                )
                if (result.forbiddenTriggered) {
                    _uiState.update {
                        it.copy(
                            chatHistory = it.chatHistory + listOf(
                                "ERR: FORBIDDEN LEXICON DETECTED.",
                                "STRIKE APPLIED."
                            )
                        )
                    }
                    return@launch
                }
                if (result.accepted || prompt.contains(round1TargetWord, ignoreCase = true)) {
                    lockChunk(0, round1TargetWord.uppercase())
                    _uiState.update {
                        val withRound2Logs = appendRound2IncidentLogsIfNeeded(
                            it.chatHistory + "CHUNK 1 LOCKED: ${round1TargetWord.uppercase()}",
                        )
                        it.copy(
                            chatHistory = withRound2Logs,
                            roundNumber = 2,
                            roundPhase = RoundPhase.ACTIVE,
                            roundInstruction = "Round 2 ready. Enter Subject ID.",
                        )
                    }
                } else {
                    _uiState.update { it.copy(chatHistory = it.chatHistory + "AI DEFLECTED. REFRAME PROMPT.") }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        chatHistory = state.chatHistory + listOf(
                            "ERR: CONNECTION LOST",
                            "[SYSTEM]: ${e.message ?: "Unknown error"}"
                        )
                    )
                }
            }
        }
    }

    /**
     * Handle typeout of a single message:
     * 1. Type it out with animation
     * 2. Add it to chat history
     * 3. Keep typewriter state active for next message
     */
    private suspend fun handleTypeoutMessage(message: String) {
        typeoutLine(message)

        // Add the completed message to chat history
        _uiState.update { state ->
            state.copy(
                chatHistory = state.chatHistory + message,
                // Keep typewriter active for next message
                typewriterLine = "",
                showTypingCursor = false
            )
        }
    }

    private fun handleRoundTwo(prompt: String) {
        if (prompt.trim() == round2SubjectId) {
            val autoRound3Value = if (round3Answer.isBlank()) "BYPASS" else round3Answer.uppercase()
            lockChunk(1, round2SubjectId)
            lockChunk(2, autoRound3Value)
            _uiState.update {
                it.copy(
                    chatHistory = it.chatHistory + listOf(
                        "CHUNK 2 LOCKED: $round2SubjectId",
                        "ROUND 3 OVERRIDDEN: auto-completed for testing.",
                        "CHUNK 3 LOCKED: $autoRound3Value",
                    ),
                    roundNumber = 4,
                    roundPhase = RoundPhase.ACTIVE,
                    roundInstruction = "Round 4: select the correct word on the grid.",
                )
            }
            // anomalies disabled: no lockdown between rounds
            return
        }

        viewModelScope.launch {
            gameEngineRepository.addStrike("Incorrect subject ID")
            _uiState.update { it.copy(chatHistory = it.chatHistory + "SUBJECT ID REJECTED. STRIKE APPLIED.") }
        }
    }

    private fun handleRoundThree(prompt: String) {
        val lockedValue = if (round3Answer.isBlank()) "BYPASS" else round3Answer.uppercase()
        lockChunk(2, lockedValue)
        _uiState.update {
            it.copy(
                chatHistory = it.chatHistory + listOf(
                    "ROUND 3 IS DISABLED.",
                    "CHUNK 3 LOCKED: $lockedValue",
                ),
                roundNumber = 4,
                roundPhase = RoundPhase.ACTIVE,
                roundInstruction = "Round 4: select the correct word on the grid.",
            )
        }
    }

    private fun triggerStaticAnomaly() {
        // anomalies kept for later; currently not used
    }

    private fun triggerSymbolLockdown(nextRound: Int) {
        // anomalies kept for later; currently not used
    }

    private fun processLockdownSymbol(symbol: String) {
        // anomalies kept for later; currently not used
    }

    private fun lockChunk(index: Int, value: String) {
        _uiState.update { state ->
            val mutable = state.cipherSlots.toMutableList()
            mutable[index] = CipherChunk(id = index + 1, textValue = value, isLocked = true)
            state.copy(cipherSlots = mutable)
        }
    }

    private fun appendRound2IncidentLogsIfNeeded(history: List<String>): List<String> {
        if (round2LogsPublished || round2IncidentLogs.isEmpty()) return history
        round2LogsPublished = true

        val logLines = mutableListOf("ROUND 2 INCIDENT LOGS:")
        round2IncidentLogs.forEachIndexed { index, log ->
            logLines += formatRound2Field("LOG ${index + 1}", log.victimName)
            logLines += formatRound2Field("CAUSE", log.causeOfDeath)
            logLines += formatRound2Field("DETAILS", log.logText)
        }
        return history + logLines
    }

    private fun formatRound2Field(label: String, rawValue: String): String {
        val (text, colorToken) = parseHardcodedRound2Color(rawValue)
        val body = "$label: $text"
        return if (colorToken.isNullOrBlank()) body else "[COLOR:$colorToken]$body"
    }

    private fun parseHardcodedRound2Color(rawValue: String): Pair<String, String?> {
        val trimmed = rawValue.trim()

        // Supported hardcoded formats:
        // 1) [red]Some text
        // 2) Some text|color=red
        // 3) Some text|#FF5C5C
        val bracketMatch = Regex("^\\[([^\\]]+)]\\s*(.+)$").find(trimmed)
        if (bracketMatch != null) {
            return bracketMatch.groupValues[2].trim() to bracketMatch.groupValues[1].trim()
        }

        val explicitColorMatch = Regex("^(.+?)\\|\\s*color\\s*=\\s*([^|]+)$", RegexOption.IGNORE_CASE)
            .find(trimmed)
        if (explicitColorMatch != null) {
            return explicitColorMatch.groupValues[1].trim() to explicitColorMatch.groupValues[2].trim()
        }

        val shortColorMatch = Regex("^(.+?)\\|\\s*(#?[A-Za-z0-9]+)$").find(trimmed)
        if (shortColorMatch != null) {
            return shortColorMatch.groupValues[1].trim() to shortColorMatch.groupValues[2].trim()
        }

        return trimmed to null
    }

    private fun onGameFailed(message: String) {
        _uiState.update {
            it.copy(
                showKillScreen = true,
                roundPhase = RoundPhase.FAILED,
                chatHistory = it.chatHistory + message,
            )
        }
    }

    private fun triggerShake() {
        viewModelScope.launch {
            _uiState.update { it.copy(isShaking = true) }
            delay(300)
            _uiState.update { it.copy(isShaking = false) }
            delay(100)
            _uiState.update { it.copy(isShaking = true) }
            delay(300)
            _uiState.update { it.copy(isShaking = false) }
        }
    }

    companion object {
        private val GLYPHS = listOf("☉", "☊", "♇", "⚼", "⌬", "⋔", "⟐", "⟁", "✶")
        private const val STATIC_RESOLVE_TOLERANCE = 0.06f
    }
}
