package com.bitbenders.theentity.ui.screens.shared_screens.lobby

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.sp
import com.bitbenders.theentity.ui.theme.EntityBlack
import com.bitbenders.theentity.ui.theme.EntityBorder
import com.bitbenders.theentity.ui.theme.EntityGreen
import com.bitbenders.theentity.ui.theme.EntityRed

import com.bitbenders.theentity.ui.effects.glitchTextEffectIfSupported

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
            text = "THE ENTITY",
            color = EntityGreen,
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 58.sp,
                letterSpacing = 16.sp,
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .glitchTextEffectIfSupported()
        )

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = { viewModel.onCreateRoomClicked() },
            enabled = !uiState.isConnecting,
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
                text = "CREATE ROOM (P1)",
                style = MaterialTheme.typography.titleLarge,
                letterSpacing = 2.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.onJoinModeClicked() },
            enabled = !uiState.isConnecting,
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
                text = "JOIN ROOM (P2)",
                style = MaterialTheme.typography.titleLarge,
                letterSpacing = 2.sp
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
                Text(
                    text = "ENTER TERMINAL", 
                    style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp)
                )
            }
        }

        if (uiState.mode == LobbyMode.JOIN) {
            Spacer(modifier = Modifier.height(28.dp))
            OutlinedTextField(
                value = uiState.joinCodeInput,
                onValueChange = { viewModel.onJoinCodeChanged(it.uppercase()) },
                label = { Text("ENTER ROOM CODE", style = MaterialTheme.typography.labelLarge) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EntityGreen,
                    unfocusedBorderColor = EntityBorder,
                    focusedLabelColor = EntityGreen,
                    unfocusedLabelColor = EntityGreen.copy(alpha = 0.6f),
                    focusedTextColor = EntityGreen,
                    unfocusedTextColor = EntityGreen,
                    cursorColor = EntityGreen
                ),
                textStyle = MaterialTheme.typography.titleLarge.copy(letterSpacing = 4.sp, textAlign = TextAlign.Center)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    viewModel.resolveJoinCodeOrError { joinCode ->
                        onJoinAsOperator(joinCode)
                    }
                },
                enabled = viewModel.canJoinCurrentCode() && !uiState.isConnecting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EntityGreen,
                    contentColor = EntityBlack,
                ),
            ) {
                Text(
                    text = "CONNECT AS OPERATOR", 
                    style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp)
                )
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
