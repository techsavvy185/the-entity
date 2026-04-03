package com.bitbenders.theentity.ui.screens.p1_screens.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.bitbenders.theentity.ui.components.BrutalistTerminalView
import com.bitbenders.theentity.ui.components.screenShake
import com.bitbenders.theentity.ui.components.staticNoise
import com.bitbenders.theentity.ui.theme.EntityBlack
import com.bitbenders.theentity.ui.theme.EntityBorder
import com.bitbenders.theentity.ui.theme.EntityGreen
import com.bitbenders.theentity.ui.theme.EntityRed

@Composable
fun P1TerminalScreen(
    viewModel: P1TerminalViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.isShaking) {
        if (uiState.isShaking) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    LaunchedEffect(uiState.chatHistory.size) {
        if (uiState.chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(uiState.chatHistory.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EntityBlack)
            .screenShake(uiState.isShaking)
            .staticNoise(uiState.currentStaticIntensity)
            .padding(16.dp)
    ) {
        // Top Header: Timer, Strikes, Cipher Slots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "T-${uiState.timerString}", color = EntityGreen, style = MaterialTheme.typography.titleLarge)

            Text(
                text = "STRIKES: ${uiState.currentStrikes}/${uiState.maxStrikes}",
                color = if (uiState.currentStrikes > 0) EntityRed else EntityGreen,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            uiState.cipherSlots.forEachIndexed { index, chunk ->
                Text(
                    text = "[ ${chunk?.textValue ?: "_"} ]",
                    color = EntityGreen,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = uiState.currentPersona, color = EntityGreen, style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Chat History (Brutalist View inside LazyColumn for scrolling)
        Box(modifier = Modifier
            .weight(1f)
            .border(1.dp, EntityBorder)
            .padding(8.dp)
        ) {
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                items(uiState.chatHistory) { msg ->
                    Text(text = msg, color = EntityGreen, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Field
        BasicTextField(
            value = uiState.inputText,
            onValueChange = {
                viewModel.onInputChanged(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, EntityGreen)
                .padding(12.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = EntityGreen),
            cursorBrush = SolidColor(EntityGreen),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
                onSend = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.submitPrompt()
                }
            )
        )
    }
}

