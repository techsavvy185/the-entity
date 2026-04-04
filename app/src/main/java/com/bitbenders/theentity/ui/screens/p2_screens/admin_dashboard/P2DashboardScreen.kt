package com.bitbenders.theentity.ui.screens.p2_screens.admin_dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitbenders.theentity.ui.components.HardwareDial
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
    Poems("POEMS", "~"),
    Eyes("EYES", "O")
}

@Composable
fun P2DashboardScreen(
    viewModel: P2DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(P2Tab.Index.ordinal) }
    var tabsCollapsed by remember { mutableStateOf(true) }
    var anomalyMode by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(initialPage = selectedTabIndex, pageCount = { P2Tab.entries.size })
    val sheetProgress by animateFloatAsState(
        targetValue = if (anomalyMode) 0f else 1f,
        animationSpec = if (anomalyMode) spring(stiffness = 420f) else tween(durationMillis = 260),
        label = "AnomalySheetProgress"
    )
    val density = LocalDensity.current

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
                if (!tabsCollapsed) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(64.dp)
                                .height(28.dp)
                                .border(1.dp, ExpandBlue, RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
                                .background(ExpandBlue, RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
                                .clickable { tabsCollapsed = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "<",
                                color = Color(0xFF02111A),
                                style = EntityTypography.headlineMedium.copy(fontSize = 18.sp, lineHeight = 18.sp)
                            )
                        }

                        P2Tab.entries.forEachIndexed { index, tab ->
                            val selected = index == selectedTabIndex
                            Box(
                                modifier = Modifier
                                    .size(width = 64.dp, height = 54.dp)
                                    .border(
                                        width = if (selected) 2.dp else 1.dp,
                                        color = if (selected) Accent else AccentSoft,
                                        shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
                                    )
                                    .background(
                                        color = if (selected) Color(0x330AB3D8) else Color(0x22040A12),
                                        shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
                                    )
                                    .clickable {
                                        selectedTabIndex = index
                                        tabsCollapsed = true
                                        anomalyMode = false
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(tab.icon, color = Ink, style = EntityTypography.titleLarge.copy(fontSize = 16.sp, lineHeight = 16.sp))
                                    Text(tab.label, color = Accent, style = EntityTypography.labelLarge.copy(fontSize = 10.sp, lineHeight = 10.sp))
                                }
                            }
                        }
                    }
                }

                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    val sheetTravelPx = with(density) { maxHeight.toPx() }
                    TacticalPanel(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (tabsCollapsed) {
                                Box(
                                    modifier = Modifier
                                        .border(1.dp, ExpandBlue, RoundedCornerShape(12.dp))
                                        .background(ExpandBlue, RoundedCornerShape(12.dp))
                                        .clickable { tabsCollapsed = false }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Open tabs",
                                        color = Color(0xFF02111A),
                                        style = EntityTypography.labelLarge.copy(fontSize = 14.sp, lineHeight = 14.sp)
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .border(1.dp, ExpandBlue, RoundedCornerShape(12.dp))
                                    .background(ExpandBlue, RoundedCornerShape(12.dp))
                                    .clickable {
                                        anomalyMode = true
                                        tabsCollapsed = true
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Anomaly",
                                    color = Color(0xFF02111A),
                                    style = EntityTypography.labelLarge.copy(fontSize = 14.sp, lineHeight = 14.sp)
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            userScrollEnabled = !anomalyMode,
                            key = { it }
                        ) { page ->
                            val tab = P2Tab.entries[page]
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(end = 2.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                when (tab) {
                                    P2Tab.Index -> {
                                        ManualHeading("INDEX // OPERATOR FIELD MANUAL")
                                        Spacer(Modifier.height(6.dp))
                                        ManualSection(
                                            "REVISION NOTE",
                                            "This binder was reconstructed from partial machine logs. Some references are out of order. If page numbers disagree, trust the margin code."
                                        )
                                        ManualSection(
                                            "GENERAL HANDLING",
                                            "Maintain low-light operation. Avoid direct verbal prompts near active terminals. Rotate stations every 9 minutes to reduce fixation artifacts."
                                        )
                                        ManualSection(
                                            "ARCHIVE FRAGMENT",
                                            "Shelf 03-B contains duplicate labels for entries 11 through 14. Cross-check by paper texture, not title."
                                        )
                                    }

                                    P2Tab.Personas -> {
                                        ManualHeading("PERSONAS // BEHAVIORAL DOSSIER")
                                        Spacer(Modifier.height(6.dp))
                                        ManualSection(
                                            "SUBJECT CLASSIFICATION",
                                            "Persona masks drift under stress. A calm tone can trigger hostile mirroring if repeated with perfect cadence."
                                        )
                                        ManualSection(
                                            "INTERVIEW TEMPLATE",
                                            "Ask one factual question, one memory question, then one contradiction. Log the first spontaneous correction."
                                        )
                                        ManualSection(
                                            "FIELD NOTE",
                                            "Subject 7 wrote three names in graphite, crossed out all three, and circled blank space."
                                        )
                                    }

                                    P2Tab.Poems -> {
                                        ManualHeading("POEMS // COGNITIVE LURES")
                                        Spacer(Modifier.height(6.dp))
                                        ManualSection(
                                            "USAGE WARNING",
                                            "Recitation can stabilize panic but may synchronize heart rate with hostile signals. Keep lines short."
                                        )
                                        ManualSection(
                                            "EXCERPT A",
                                            "Under the glass the numbers sleep. Counting teeth in circuits deep. If the hallway starts to hum, close your eyes and count to one."
                                        )
                                        ManualSection(
                                            "EXCERPT B",
                                            "Ink remembers what we hide. Doors forget which side is wide. Name the shadow, lose the key."
                                        )
                                    }

                                    P2Tab.Eyes -> {
                                        ManualHeading("EYES // OBSERVATION PROTOCOL")
                                        Spacer(Modifier.height(6.dp))
                                        ManualSection(
                                            "VISUAL DISCIPLINE",
                                            "Do not hold gaze on reflective surfaces for longer than 4 seconds. Scan corners clockwise."
                                        )
                                        ManualSection(
                                            "LENS CALIBRATION",
                                            "When static blooms at screen edges, lower contrast before adjusting focus to avoid false trails."
                                        )
                                        ManualSection(
                                            "WATCH REPORT",
                                            "Camera 2 showed six frames of an empty corridor, then one frame with a chair facing the wall."
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Full-height anomaly sheet that rises from the bottom and covers this content area.
                    if (sheetProgress < 0.999f) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(TabletBg)
                                .offset {
                                    val offsetPx = (sheetProgress * sheetTravelPx).toInt()
                                    androidx.compose.ui.unit.IntOffset(0, offsetPx)
                                }
                        ) {
                            TacticalPanel(modifier = Modifier.fillMaxSize()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ManualHeading("ANOMALIES // CONTAINMENT PROCEDURE")
                                    Box(
                                        modifier = Modifier
                                            .border(1.dp, Color(0xFFFF4D4D), RoundedCornerShape(8.dp))
                                            .background(Color(0xFFFF4D4D), RoundedCornerShape(8.dp))
                                            .clickable { anomalyMode = false }
                                            .padding(horizontal = 10.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "X",
                                            color = Color(0xFF1A0303),
                                            style = EntityTypography.headlineMedium.copy(fontSize = 18.sp, lineHeight = 18.sp)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(8.dp))
                                ManualSection(
                                    "OPERATOR BRIEF",
                                    "Use tuning controls in short bursts. Sustained correction invites rebound noise."
                                )

                                TacticalPanel(modifier = Modifier.fillMaxWidth()) {
                                    Text("COGNITIVE TUNING MATRIX", color = Accent, style = EntityTypography.titleLarge.copy(fontSize = 14.sp, lineHeight = 16.sp))
                                    Spacer(Modifier.height(8.dp))
                                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        HardwareDial(
                                            rotationValue = uiState.currentDialValue,
                                            onRotationChanged = { viewModel.onDialTurned(it) },
                                            modifier = Modifier.size(180.dp)
                                        )
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Slider(
                                        value = uiState.tuningValue.toFloat(),
                                        onValueChange = { viewModel.onTuningValueChanged(it.toInt()) },
                                        valueRange = 0f..100f
                                    )
                                    Text(
                                        text = "VALUE: ${uiState.tuningValue}",
                                        color = Ink,
                                        style = EntityTypography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 16.sp)
                                    )
                                    ManualParagraph("Recommended sweep: 42 -> 57 -> 49. Pause three breaths between adjustments.")
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
