package com.bitbenders.theentity.ui.screens.shared_screens.lobby

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bitbenders.theentity.ui.theme.EntityBlack
import com.bitbenders.theentity.ui.theme.EntityBorder
import com.bitbenders.theentity.ui.theme.EntityGreen

@Composable
fun LobbyScreen(
    viewModel: LobbyViewModel,
    onJoinAsTrapped: () -> Unit,
    onJoinAsOperator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EntityBlack)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "T H E  E N T I T Y",
            color = EntityGreen,
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = onJoinAsTrapped,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = EntityBlack,
                contentColor = EntityGreen
            ),
            border = BorderStroke(2.dp, EntityBorder)
        ) {
            Text(
                text = "JOIN AS TRAPPED (P1)",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onJoinAsOperator,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = EntityBlack,
                contentColor = EntityGreen
            ),
            border = BorderStroke(2.dp, EntityBorder)
        ) {
            Text(
                text = "JOIN AS OPERATOR (P2)",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

