package com.bitbenders.theentity.ui.screens.p2_screens.admin_dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bitbenders.theentity.ui.components.AlienKeypad
import com.bitbenders.theentity.ui.components.HardwareDial
import com.bitbenders.theentity.ui.theme.EntityBlack
import com.bitbenders.theentity.ui.theme.EntityBorder
import com.bitbenders.theentity.ui.theme.EntityGreen

@Composable
fun P2DashboardScreen(
    viewModel: P2DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EntityBlack)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ADMIN OVERRIDE DASHBOARD",
            color = EntityGreen,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Dial Region
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text("FREQUENCY SYNTHESIZER", color = EntityGreen, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                HardwareDial(
                    rotationValue = uiState.currentDialValue,
                    onRotationChanged = { viewModel.onDialTurned(it) }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "SIGNAL: ${String.format("%.2f", uiState.currentDialValue)}",
                color = EntityGreen,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Keypad Region
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1.5f)
        ) {
            Text("GLYPH LOCKDOWN PAD", color = EntityGreen, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, EntityBorder)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SEQ: ${uiState.keypadInput}",
                    color = EntityGreen,
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(
                    onClick = { viewModel.clearKeypadInput() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EntityGreen,
                        contentColor = EntityBlack
                    )
                ) {
                    Text("CLR")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AlienKeypad(
                symbols = listOf("☉", "☊", "♇", "⚼", "⌬", "⋔", "⟐", "⟁", "✶"),
                onSymbolClicked = { viewModel.onKeypadSymbolClicked(it) }
            )
        }
    }
}

