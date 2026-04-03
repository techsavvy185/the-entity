package com.bitbenders.theentity.ui.screens.p1_screens.terminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitbenders.theentity.domain.models.CipherChunk
import com.bitbenders.theentity.domain.repository.IEntityBackendRepository
import com.bitbenders.theentity.domain.repository.IGameEngineRepository
import com.bitbenders.theentity.domain.usecases.EvaluatePlayerInputUseCase
import com.bitbenders.theentity.domain.usecases.ResolveAnomalyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlin.math.abs
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
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
    private var round3Answer: String = ""
    private var staticTargetFrequency: Float = 0.5f
    private var correctBossWord: String = ""

    init {
        viewModelScope.launch {
            gameEngineRepository.remainingTimeSeconds.collect { seconds ->
                val mins = seconds / 60
                val secs = seconds % 60
                _uiState.update {
                    it.copy(timerString = String.format("%02d:%02d", mins, secs))
                }

                if (seconds <= 0 && !_uiState.value.showKillScreen && !_uiState.value.isVictory) {
                    onGameFailed("SUBJECT INTEGRATED. PATTERN ACQUIRED.")
                }
            }
        }

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
                    triggerStaticAnomaly()
                }
                if (strikeState.isGameOver && !_uiState.value.showKillScreen && !_uiState.value.isVictory) {
                    onGameFailed("SUBJECT INTEGRATED. PATTERN ACQUIRED.")
                }
            }
        }

        viewModelScope.launch {
            try {
                val health = entityBackendRepository.checkHealth()
                _uiState.update {
                    it.copy(
                        currentPersona = if (health.isUp) "RELAY ONLINE" else "RELAY OFFLINE",
                        chatHistory = listOf(
                            "AI BOOT SEQUENCE INITIATED...",
                            if (health.mockMode) "[MOCK MODE ACTIVE]" else "[LIVE MODE]",
                            "AWAITING INPUT.",
                        ),
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        currentPersona = "ERROR: RELAY OFFLINE",
                        chatHistory = it.chatHistory + "Running in fallback local scenario data."
                    )
                }
            }
        }

        viewModelScope.launch {
            resolveAnomalyUseCase.observeStaticIntensity().collect { dialValue ->
                if (_uiState.value.roundPhase != RoundPhase.STATIC) return@collect
                val distance = abs(dialValue - staticTargetFrequency)
                val noise = (distance * 1.7f).coerceIn(0f, 1f)

                _uiState.update { it.copy(currentStaticIntensity = noise) }

                if (distance <= STATIC_RESOLVE_TOLERANCE) {
                    _uiState.update {
                        it.copy(
                            currentStaticIntensity = 0f,
                            roundPhase = RoundPhase.ACTIVE,
                            chatHistory = it.chatHistory + "STATIC STABILIZED. TERMINAL FEED RESTORED."
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            resolveAnomalyUseCase.observeKeypadSymbols().collect { symbol ->
                if (_uiState.value.roundPhase != RoundPhase.LOCKDOWN) return@collect
                processLockdownSymbol(symbol)
            }
        }

        bootstrapRoundData()
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

        val prompt = _uiState.value.inputText
        if (prompt.isBlank()) return

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
                round3Answer = gamePackage.round3.answer
                correctBossWord = gamePackage.round4NativeBrief.correctWord

                val options = gamePackage.round4NativeBrief.homophones.ifEmpty {
                    listOf("WAIT", "WEIGHT", "RIGHT", "WRITE", "HOLE", "WHOLE")
                }

                _uiState.update {
                    it.copy(
                        roundPhase = RoundPhase.ACTIVE,
                        currentPersona = "PERSONA: ${gamePackage.round1.persona.uppercase()}",
                        roundInstruction = "Force target output without forbidden lexicon.",
                        calibrationKey = gamePackage.round4NativeBrief.calibrationKey,
                        bossOptions = options.mapIndexed { index, word -> BossOptionUi(id = index, text = word) },
                        chatHistory = it.chatHistory + listOf(
                            "ROUND 1 ONLINE: Persona trap initialized.",
                            "FORBIDDEN WORDS (P2 MANUAL): ${round1ForbiddenWords.joinToString(", ")}",
                        ),
                    )
                }
            } catch (e: Exception) {
                round1TargetWord = "harvest"
                round1ForbiddenWords = listOf("kill", "free")
                round2SubjectId = "7312"
                round3Answer = "VOID"
                correctBossWord = "WRITE"

                _uiState.update {
                    it.copy(
                        roundPhase = RoundPhase.ACTIVE,
                        currentPersona = "PERSONA: PARANOID SOLDIER",
                        roundInstruction = "Fallback scenario loaded. Continue extraction.",
                        calibrationKey = "C7",
                        bossOptions = listOf("WAIT", "WEIGHT", "RIGHT", "WRITE", "HOLE", "WHOLE")
                            .mapIndexed { index, word -> BossOptionUi(id = index, text = word) },
                        chatHistory = it.chatHistory + "Fallback round package loaded due to relay failure.",
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
                        it.copy(chatHistory = it.chatHistory + listOf("ERR: FORBIDDEN LEXICON DETECTED.", "STRIKE APPLIED."))
                    }
                    return@launch
                }

                if (result.accepted || prompt.contains(round1TargetWord, ignoreCase = true)) {
                    lockChunk(0, round1TargetWord.uppercase())
                    _uiState.update {
                        it.copy(chatHistory = it.chatHistory + "CHUNK 1 LOCKED: ${round1TargetWord.uppercase()}")
                    }
                    triggerSymbolLockdown(nextRound = 2)
                } else {
                    _uiState.update { it.copy(chatHistory = it.chatHistory + "AI DEFLECTED. REFRAME PROMPT.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(chatHistory = it.chatHistory + "ERR: CONNECTION LOST.") }
            }
        }
    }

    private fun handleRoundTwo(prompt: String) {
        if (prompt.trim() == round2SubjectId) {
            lockChunk(1, round2SubjectId)
            _uiState.update { it.copy(chatHistory = it.chatHistory + "CHUNK 2 LOCKED: $round2SubjectId") }
            triggerSymbolLockdown(nextRound = 3)
            return
        }

        viewModelScope.launch {
            gameEngineRepository.addStrike("Incorrect subject ID")
            _uiState.update { it.copy(chatHistory = it.chatHistory + "SUBJECT ID REJECTED. STRIKE APPLIED.") }
        }
    }

    private fun handleRoundThree(prompt: String) {
        if (prompt.trim().equals(round3Answer, ignoreCase = true)) {
            lockChunk(2, round3Answer.uppercase())
            _uiState.update { it.copy(chatHistory = it.chatHistory + "CHUNK 3 LOCKED: ${round3Answer.uppercase()}") }
            triggerSymbolLockdown(nextRound = 4)
            return
        }

        viewModelScope.launch {
            gameEngineRepository.addStrike("Incorrect thematic cipher")
            _uiState.update { it.copy(chatHistory = it.chatHistory + "THEMATIC CIPHER INVALID. STRIKE APPLIED.") }
        }
    }

    private fun triggerStaticAnomaly() {
        if (_uiState.value.roundPhase == RoundPhase.LOCKDOWN || _uiState.value.showKillScreen) return
        staticTargetFrequency = Random.nextDouble(0.15, 0.85).toFloat()
        _uiState.update {
            it.copy(
                roundPhase = RoundPhase.STATIC,
                currentStaticIntensity = 1f,
                chatHistory = it.chatHistory + "ANOMALY A: STATIC FREQUENCY. OPERATOR DIAL REQUIRED.",
            )
        }
    }

    private fun triggerSymbolLockdown(nextRound: Int) {
        val glyphs = GLYPHS.shuffled().take(4)
        _uiState.update {
            it.copy(
                roundPhase = RoundPhase.LOCKDOWN,
                roundNumber = nextRound,
                lockedGlyphs = glyphs,
                roundInstruction = "Terminal lockdown. P2 must enter symbol sequence.",
                chatHistory = it.chatHistory + "ANOMALY B: SYMBOL LOCKDOWN ENGAGED.",
            )
        }
    }

    private fun processLockdownSymbol(symbol: String) {
        val expected = _uiState.value.lockedGlyphs.firstOrNull() ?: return
        if (symbol == expected) {
            val remaining = _uiState.value.lockedGlyphs.drop(1)
            _uiState.update {
                it.copy(
                    lockedGlyphs = remaining,
                    chatHistory = it.chatHistory + "LOCKDOWN INPUT ACCEPTED: $symbol",
                )
            }

            if (remaining.isEmpty()) {
                val nextInstruction = when (_uiState.value.roundNumber) {
                    2 -> "Round 2: decode the 4-digit Subject ID from post-mortem logs."
                    3 -> "Round 3: parse theme-based data and extract the answer token."
                    4 -> "Round 4: hostile lexical calibration. Use commit on the homophone grid."
                    else -> ""
                }

                _uiState.update {
                    it.copy(
                        roundPhase = RoundPhase.ACTIVE,
                        roundInstruction = nextInstruction,
                        chatHistory = it.chatHistory + "LOCKDOWN CLEARED.",
                    )
                }
            }
            return
        }

        _uiState.update {
            it.copy(chatHistory = it.chatHistory + "LOCKDOWN MISMATCH. SEQUENCE RESET.")
        }
        triggerSymbolLockdown(_uiState.value.roundNumber)
    }

    private fun lockChunk(index: Int, value: String) {
        _uiState.update { state ->
            val mutable = state.cipherSlots.toMutableList()
            mutable[index] = CipherChunk(id = index + 1, textValue = value, isLocked = true)
            state.copy(cipherSlots = mutable)
        }
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
            delay(500)
            _uiState.update { it.copy(isShaking = false) }
        }
    }

    companion object {
        private val GLYPHS = listOf("☉", "☊", "♇", "⚼", "⌬", "⋔", "⟐", "⟁", "✶")
        private const val STATIC_RESOLVE_TOLERANCE = 0.06f
    }
}
