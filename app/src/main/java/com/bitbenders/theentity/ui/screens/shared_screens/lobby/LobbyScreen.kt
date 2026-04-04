package com.bitbenders.theentity.ui.screens.shared_screens.lobby

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bitbenders.theentity.ui.theme.EntityBlack
import com.bitbenders.theentity.ui.theme.EntityBorder
import com.bitbenders.theentity.ui.theme.EntityGreen
import com.bitbenders.theentity.ui.theme.EntityRed

@Composable
fun LobbyScreen(
    viewModel: LobbyViewModel,
    onJoinAsTrapped: (String) -> Unit,
    onJoinAsOperator: (String) -> Unit,
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
            text = "T H E\nE N T I T Y",
            color = EntityGreen,
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = { viewModel.onCreateRoomClicked() },
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
            onClick = { viewModel.onJoinModeClicked() },
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

        if (uiState.mode == LobbyMode.CREATE) {
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "ROOM CODE: ${uiState.roomCode}",
                color = EntityGreen,
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { onJoinAsTrapped(uiState.roomCode) },
                enabled = uiState.roomCode.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EntityGreen,
                    contentColor = EntityBlack,
                ),
            ) {
                Text(text = "CREATE ROOM + ENTER AS P1", style = MaterialTheme.typography.labelLarge)
            }
        }

        if (uiState.mode == LobbyMode.JOIN) {
            Spacer(modifier = Modifier.height(28.dp))
            OutlinedTextField(
                value = uiState.joinCodeInput,
                onValueChange = viewModel::onJoinCodeChanged,
                label = { Text("Enter Room Code", style = MaterialTheme.typography.labelLarge) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    val joinCode = viewModel.resolveJoinCodeOrError() ?: return@Button
                    onJoinAsOperator(joinCode)
                },
                enabled = viewModel.canJoinCurrentCode(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EntityGreen,
                    contentColor = EntityBlack,
                ),
            ) {
                Text(text = "JOIN ROOM AS P2", style = MaterialTheme.typography.labelLarge)
            }
        }

        uiState.connectionError?.let { error ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = error,
                color = EntityRed,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}
