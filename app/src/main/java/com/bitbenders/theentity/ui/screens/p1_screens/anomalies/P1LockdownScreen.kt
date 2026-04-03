package com.bitbenders.theentity.ui.screens.p1_screens.anomalies

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bitbenders.theentity.ui.theme.EntityBlack
import com.bitbenders.theentity.ui.theme.EntityBorder
import com.bitbenders.theentity.ui.theme.EntityGreen
import com.bitbenders.theentity.ui.theme.EntityRed

@Composable
fun P1LockdownScreen(
    lockedGlyphs: List<String>,
    modifier: Modifier = Modifier
) {
    if (lockedGlyphs.isEmpty()) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EntityBlack.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SYSTEM LOCKDOWN",
                color = EntityRed,
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                lockedGlyphs.forEach { glyph ->
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .border(2.dp, EntityBorder)
                            .background(EntityBlack),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = glyph,
                            color = EntityGreen,
                            style = MaterialTheme.typography.displayLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "AWAITING OPERATOR OVERRIDE",
                color = EntityGreen,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

