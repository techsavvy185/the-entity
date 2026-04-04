package com.bitbenders.theentity.ui.screens.p2_screens.admin_dashboard

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitbenders.theentity.data.round4.RoundFourCatalog
import com.bitbenders.theentity.ui.effects.tacticalCrtEffectIfSupported
import com.bitbenders.theentity.ui.theme.EntityTypography
import java.util.Locale

private val TabletBg = Color(0xFF050B14)
private val PanelEdge = Color(0x8834D6FF)
private val Accent = Color(0xFF34D6FF)
private val AccentSoft = Color(0x6634D6FF)
private val Ink = Color(0xFFE8F7FF)
private val ExpandBlue = Color(0xFF00AFFF)

private enum class P2Tab(val label: String, val icon: String) {
    Index("INDEX", "[]"),
    Personas("PERSONAS", "@"),
    Dissect("DISSECT", "†"),
    Display("DISPLAY", "?"),
    Signals("SIGNALS", "~")
}

@Composable
fun P2DashboardScreen(
    viewModel: P2DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabOrder = remember {
        val rest = P2Tab.entries.filter { it != P2Tab.Index }.shuffled()
        listOf(P2Tab.Index) + rest
    }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabOrder.size })

    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TabletBg)
            .tacticalCrtEffectIfSupported()
    ) {
        // Grid/scanlines now come from the shader effect.

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TacticalPanel {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "T ${formatTimer(uiState.missionSecondsRemaining)}",
                        color = Color(0xFFFF4D4D),
                        style = EntityTypography.displayLarge.copy(fontSize = 28.sp, lineHeight = 30.sp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(uiState.maxStrikes) { index ->
                            val filled = index < uiState.strikes
                            Text(
                                text = if (filled) "●" else "○",
                                color = if (filled) Ink else AccentSoft,
                                style = EntityTypography.headlineMedium.copy(fontSize = 24.sp, lineHeight = 24.sp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TacticalPanel(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        key = { it }
                        ) { page ->
                            val tab = tabOrder[page]
                            val isLastPage = page == tabOrder.lastIndex
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(end = 2.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                when (tab) {
                                    P2Tab.Index -> {
                                        Spacer(Modifier.height(12.dp))
                                        // Title block
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(2.dp, Accent, RoundedCornerShape(6.dp))
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    "⬡ ARMOROS ⬡",
                                                    color = Accent,
                                                    style = EntityTypography.headlineMedium.copy(fontSize = 11.sp, lineHeight = 12.sp)
                                                )
                                                Spacer(Modifier.height(8.dp))
                                                Text(
                                                    "OPERATOR\nFIELD MANUAL",
                                                    color = Ink,
                                                    style = EntityTypography.displayLarge.copy(fontSize = 26.sp, lineHeight = 30.sp),
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(Modifier.height(6.dp))
                                                Text(
                                                    "REV 4.1  ·  RESTRICTED",
                                                    color = AccentSoft,
                                                    style = EntityTypography.labelLarge.copy(fontSize = 10.sp, lineHeight = 12.sp)
                                                )
                                            }
                                        }

                                        Spacer(Modifier.height(10.dp))
                                        // Classification
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0x44FF4D4D), RoundedCornerShape(4.dp))
                                                .border(1.dp, Color(0xFFFF4D4D), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "▲ CLASSIFIED — OPERATOR EYES ONLY ▲",
                                                color = Color(0xFFFF4D4D),
                                                style = EntityTypography.headlineMedium.copy(fontSize = 11.sp, lineHeight = 12.sp),
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                        Spacer(Modifier.height(10.dp))
                                        ManualSection(
                                            "OVERVIEW",
                                            "This manual contains all reference material required for remote operator support during active containment events. Memorization is not expected. Speed of lookup is critical."
                                        )

                                        val tocDescriptions = mapOf(
                                            P2Tab.Personas to "Behavioral profiles & forbidden words",
                                            P2Tab.Dissect to "Post-mortem dissection flowchart",
                                            P2Tab.Display to "Who's on First module reference",
                                            P2Tab.Signals to "Radio interference & recovered transmissions"
                                        )
                                        val tocText = tabOrder
                                            .filter { it != P2Tab.Index }
                                            .mapIndexed { i, tab ->
                                                "§${i + 1}  ${tab.label} — ${tocDescriptions[tab] ?: ""}"
                                            }
                                            .joinToString("\n")
                                        ManualSection("TABLE OF CONTENTS", tocText)

                                        ManualSection(
                                            "HANDLING INSTRUCTIONS",
                                            "• Keep this terminal in low-light conditions at all times.\n" +
                                                "• Do not read aloud near active containment zones.\n" +
                                                "• Rotate stations every 9 minutes to prevent fixation.\n" +
                                                "• If page references conflict, trust the margin code."
                                        )

                                        ManualSection(
                                            "FIELD NOTE",
                                            "This binder was reconstructed from partial machine logs recovered after Incident ΔV-7. Some sections may appear out of order. Cross-reference entries by content, not sequence."
                                        )

                                        SwipeHint(isLast = isLastPage)
                                    }

                                    P2Tab.Personas -> {
                                        ManualHeading("PERSONAS // BEHAVIORAL DOSSIER")
                                        Spacer(Modifier.height(6.dp))
                                        val active = uiState.activePersonaEntry
                                        if (active != null) {
                                            ManualSection(
                                                "ACTIVE ROOM PERSONA: ${active.persona.uppercase(Locale.US)}",
                                                "Target: ${active.targetWord} | Forbidden: ${active.forbiddenWords.joinToString(", ")}"
                                            )
                                        } else {
                                            ManualSection(
                                                "ACTIVE ROOM PERSONA",
                                                "Join or create the same room as P1 to load the active target and forbidden words."
                                            )
                                        }

                                        uiState.personaEntries.forEach { entry ->
                                            ManualSection(
                                                entry.persona.uppercase(Locale.US),
                                                "Target: ${entry.targetWord} | Forbidden: ${entry.forbiddenWords.joinToString(", ")}"
                                            )
                                        }

                                        SwipeHint(isLast = isLastPage)
                                    }

                                    P2Tab.Dissect -> {
                                        ManualHeading("DISSECT // POST-MORTEM PROTOCOL")
                                        Spacer(Modifier.height(6.dp))
                                        ManualSection(
                                            "INSTRUCTIONS",
                                            "P1 reads a post-mortem report. Ask the questions below in order, following YES/NO branches. Arrive at a 4-digit code, then apply any matching Global Modifiers."
                                        )

                                        Spacer(Modifier.height(4.dp))
                                        ManualHeading("GLOBAL MODIFIERS")
                                        Spacer(Modifier.height(4.dp))
                                        ManualSection(
                                            "① BRACKET ANOMALY",
                                            "If the log contains square brackets [ ] anywhere in the text, swap the first and last digits.\n(1234 → 4231)"
                                        )
                                        ManualSection(
                                            "② ODD TIMESTAMP",
                                            "Look at the time log at the top of the report. If the timestamp ends in an odd number (e.g., 04:17), replace all even digits in your code with 0.\n(1234 → 1030)"
                                        )
                                        ManualSection(
                                            "③ ASTERISK SHIFT",
                                            "If you see an asterisk (*) anywhere in the body of the report, shift all digits one position to the right. The last number loops to the front.\n(1234 → 4123)"
                                        )
                                        ManualSection(
                                            "④ HEX GLITCH",
                                            "If the report contains a raw hex memory address (like 0x00 or 0xA1), subtract 1 from every digit. 0 wraps to 9.\n(1059 → 0948)"
                                        )
                                        ManualSection(
                                            "⑤ SCREAMING RULE",
                                            "If there are exactly THREE consecutive words in ALL CAPS, swap the middle two digits.\n(1234 → 1324)"
                                        )
                                        ManualSection(
                                            "⑥ CORRUPTED AUTHOR",
                                            "If the author's name at the top of the report is \"UNKNOWN\", replace the highest digit in your code with a 5.\n(1294 → 1254)"
                                        )

                                        Spacer(Modifier.height(12.dp))
                                        ManualHeading("DISSECTION FLOWCHART")
                                        Spacer(Modifier.height(4.dp))

                                        FlowchartNode(
                                            tag = "Q1 — START",
                                            question = "Did the report mention the presence of any biological fluid (blood, bile, or resin) at the terminal?",
                                            yes = "→ Q2", no = "→ Q3"
                                        )
                                        FlowchartNode(
                                            tag = "Q2 — BIO-PATH",
                                            question = "Was the subject's neural link severed forcefully?",
                                            yes = "→ Q4", no = "→ Q5"
                                        )
                                        FlowchartNode(
                                            tag = "Q3 — TECH-PATH",
                                            question = "Did the terminal log a localized power surge prior to termination?",
                                            yes = "→ Q6", no = "→ Q7"
                                        )
                                        FlowchartNode(
                                            tag = "Q4",
                                            question = "Is there a reference to audible screaming or vocal distress on the audio logs?",
                                            yes = "→ Q8", no = "→ Q9"
                                        )
                                        FlowchartNode(
                                            tag = "Q5",
                                            question = "Were the subject's eyes described in the autopsy notes (e.g., \"dilated\", \"burnt\", \"missing\")?",
                                            yes = "→ Q10", no = "→ Q11"
                                        )
                                        FlowchartNode(
                                            tag = "Q6",
                                            question = "Was the sector cooling system offline for more than 5 minutes?",
                                            yes = "→ Q12", no = "→ Q13"
                                        )
                                        FlowchartNode(
                                            tag = "Q7",
                                            question = "Did the AI display any conversational text before the final blackout?",
                                            yes = "→ Q14", no = "→ Q15"
                                        )
                                        FlowchartNode(
                                            tag = "Q8",
                                            question = "Was the physical containment door locked from the inside?",
                                            yes = "CODE: 8041", no = "CODE: 9920"
                                        )
                                        FlowchartNode(
                                            tag = "Q9",
                                            question = "Are there physical scratch marks logged on the primary console casing?",
                                            yes = "CODE: 4412", no = "CODE: 1058"
                                        )
                                        FlowchartNode(
                                            tag = "Q10",
                                            question = "Did the medical scan show heavy metal toxicity in the bloodstream?",
                                            yes = "CODE: 7734", no = "CODE: 2091"
                                        )
                                        FlowchartNode(
                                            tag = "Q11",
                                            question = "Was the subject clutching a personal item when the body was recovered?",
                                            yes = "CODE: 6116", no = "CODE: 3303"
                                        )
                                        FlowchartNode(
                                            tag = "Q12",
                                            question = "Is the system core temperature listed above 90 degrees?",
                                            yes = "CODE: 5589", no = "CODE: 0224"
                                        )
                                        FlowchartNode(
                                            tag = "Q13",
                                            question = "Did the localized EMP trigger successfully during the breach?",
                                            yes = "CODE: 8100", no = "CODE: 4949"
                                        )
                                        FlowchartNode(
                                            tag = "Q14",
                                            question = "Did the AI refer to the subject by their real name in the logs?",
                                            yes = "CODE: 1173", no = "CODE: 6060"
                                        )
                                        FlowchartNode(
                                            tag = "Q15",
                                            question = "Are there unidentified background audio anomalies recorded during the event?",
                                            yes = "CODE: 2856", no = "CODE: 9401"
                                        )

                                        SwipeHint(isLast = isLastPage)
                                    }

                                    P2Tab.Display -> {
                                        ManualHeading("DISPLAY // WHO'S ON FIRST")
                                        Spacer(Modifier.height(6.dp))
                                        ManualSection(
                                            "STEP 1 — DISPLAY LOOKUP",
                                            "Read the word shown on the display. Find it below. The ◉ marks which button position to READ the label from."
                                        )
                                        Spacer(Modifier.height(4.dp))

                                        val entries = RoundFourCatalog.displayEntries
                                        entries.chunked(3).forEach { rowEntries ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                rowEntries.forEach { entry ->
                                                    DisplayLookupCell(
                                                        displayWord = entry.displayWord,
                                                        positionRow = entry.position.row,
                                                        positionCol = entry.position.col,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                                repeat(3 - rowEntries.size) {
                                                    Spacer(Modifier.weight(1f))
                                                }
                                            }
                                        }

                                        Spacer(Modifier.height(12.dp))
                                        ManualSection(
                                            "STEP 2 — PRIORITY LISTS",
                                            "Using the label from Step 1, find it below. Press the FIRST button in the list that is also visible on the module."
                                        )
                                        Spacer(Modifier.height(4.dp))

                                        RoundFourCatalog.priorityEntries.forEach { entry ->
                                            PriorityListRow(
                                                keyword = entry.label,
                                                words = entry.priority
                                            )
                                        }

                                        SwipeHint(isLast = isLastPage)
                                    }

                                    P2Tab.Signals -> {
                                        ManualHeading("SIGNALS // INTERFERENCE REFERENCE")
                                        Spacer(Modifier.height(6.dp))
                                        ManualSection(
                                            "NOTICE",
                                            "This appendix was recovered from Shelf 07-F. It may not correspond to your facility revision. Consult your sector lead before acting on any frequency listed below."
                                        )

                                        ManualSection(
                                            "FREQUENCY TABLE — SECTOR 3",
                                            "CH-01 ···· 142.7 MHz — Cargo elevator interlock\n" +
                                                "CH-02 ···· 146.3 MHz — Mess hall paging (inactive)\n" +
                                                "CH-03 ···· 151.9 MHz — Reserved (see memo 14-D)\n" +
                                                "CH-04 ···· 160.0 MHz — Emergency lighting override\n" +
                                                "CH-05 ···· 174.4 MHz — Unassigned"
                                        )

                                        ManualSection(
                                            "STATIC CLASSIFICATIONS",
                                            "Type A — Broadband hiss, equipment origin. Harmless.\n" +
                                                "Type B — Rhythmic clicking, often HVAC-related.\n" +
                                                "Type C — Tonal sweep, 2-8 second duration. Log but do not investigate.\n" +
                                                "Type D — Vocal-adjacent patterning. File Form 9-Sigma immediately."
                                        )

                                        ManualSection(
                                            "ANTENNA MAINTENANCE SCHEDULE",
                                            "• Rooftop array: Inspect every 14 shifts. Last service: Shift 97.\n" +
                                                "• Basement relay: Replace capacitor C-11 if amber LED is solid.\n" +
                                                "• Mobile unit: Do not charge past 80%. Battery lot 44-R recalled."
                                        )

                                        ManualSection(
                                            "RECOVERED TRANSMISSION — FRAGMENT 11",
                                            "\"...seven, seven, hold... I can still see the corridor from here. It hasn't moved. Please confirm the corridor has not moved. Over.\""
                                        )

                                        ManualSection(
                                            "RECOVERED TRANSMISSION — FRAGMENT 23",
                                            "\"...repeat, the vending machine on B4 is dispensing items we did not stock. Requesting maintenance. Requesting maintenance. Over.\""
                                        )

                                        ManualSection(
                                            "CALIBRATION NOTE",
                                            "If the signal meter reads exactly 0.00 for longer than 30 seconds, the meter is not broken. Leave the room."
                                        )

                                        SwipeHint(isLast = isLastPage)
                                    }
                                }
                            }
                        }
                }
            }
        }

        // ScanlinesOverlay() // Removed as it's now handled by the shader effect
    }
}

@Composable
private fun TacticalPanel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .border(1.dp, PanelEdge, RoundedCornerShape(6.dp))
            .background(Color(0x33040A12), RoundedCornerShape(6.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = content
    )
}

private fun formatTimer(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val minutes = safe / 60
    val seconds = safe % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}

@Composable
private fun ManualHeading(text: String) {
    Text(
        text = text,
        color = Ink,
        style = EntityTypography.headlineMedium.copy(fontSize = 20.sp, lineHeight = 20.sp)
    )
}

@Composable
private fun ManualParagraph(text: String) {
    Text(
        text = text,
        color = Color(0xFFB9D8E6),
        style = EntityTypography.bodyMedium.copy(fontSize = 12.sp, lineHeight = 16.sp)
    )
}

@Composable
private fun ManualSection(title: String, body: String) {
    TacticalPanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = Accent,
            style = EntityTypography.titleLarge.copy(fontSize = 14.sp, lineHeight = 16.sp)
        )
        ManualParagraph(body)
    }
}

@Composable
private fun DisplayLookupCell(
    displayWord: String,
    positionRow: Int,
    positionCol: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(1.dp, PanelEdge, RoundedCornerShape(4.dp))
            .background(Color(0x22040A12), RoundedCornerShape(4.dp))
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Accent, RoundedCornerShape(2.dp))
                .padding(horizontal = 2.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayWord.ifEmpty { "\"\"" },
                color = TabletBg,
                style = EntityTypography.headlineMedium.copy(fontSize = 12.sp, lineHeight = 14.sp),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            for (r in 0..2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (c in 0..1) {
                        val active = r == positionRow && c == positionCol
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .border(0.5.dp, PanelEdge.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (active) "◉" else "·",
                                color = if (active) Accent else AccentSoft,
                                style = EntityTypography.bodyMedium.copy(
                                    fontSize = if (active) 16.sp else 10.sp,
                                    lineHeight = 16.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PriorityListRow(
    keyword: String,
    words: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(0.5.dp, PanelEdge.copy(alpha = 0.4f), RoundedCornerShape(3.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "\"$keyword\"",
            color = Accent,
            style = EntityTypography.headlineMedium.copy(fontSize = 14.sp, lineHeight = 16.sp)
        )
        Text(
            text = words.joinToString(", "),
            color = Color(0xFFB9D8E6),
            style = EntityTypography.bodyMedium.copy(fontSize = 12.sp, lineHeight = 15.sp)
        )
    }
}

@Composable
private fun SwipeHint(isLast: Boolean = false) {
    Spacer(Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLast) {
            Text(
                "— END OF MANUAL —",
                color = AccentSoft,
                style = EntityTypography.labelLarge.copy(fontSize = 11.sp, lineHeight = 12.sp)
            )
        } else {
            Text(
                "SWIPE TO TURN  ▸▸",
                color = AccentSoft,
                style = EntityTypography.labelLarge.copy(fontSize = 11.sp, lineHeight = 12.sp)
            )
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun FlowchartNode(
    tag: String,
    question: String,
    yes: String,
    no: String,
    modifier: Modifier = Modifier
) {
    val isCode = yes.startsWith("CODE")
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, PanelEdge, RoundedCornerShape(4.dp))
            .background(Color(0x22040A12), RoundedCornerShape(4.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = tag,
            color = Accent,
            style = EntityTypography.headlineMedium.copy(fontSize = 13.sp, lineHeight = 14.sp)
        )
        Text(
            text = question,
            color = Color(0xFFB9D8E6),
            style = EntityTypography.bodyMedium.copy(fontSize = 12.sp, lineHeight = 15.sp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "YES: $yes",
                color = if (isCode) Color(0xFF4DFF4D) else Color(0xFF8CEAAA),
                style = EntityTypography.headlineMedium.copy(
                    fontSize = if (isCode) 14.sp else 12.sp,
                    lineHeight = 14.sp
                ),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "NO: $no",
                color = if (isCode) Color(0xFFFF6B6B) else Color(0xFFE8A090),
                style = EntityTypography.headlineMedium.copy(
                    fontSize = if (isCode) 14.sp else 12.sp,
                    lineHeight = 14.sp
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
