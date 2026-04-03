package com.bitbenders.theentity.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bitbenders.theentity.ui.theme.EntityBlack
import com.bitbenders.theentity.ui.theme.EntityBorder
import com.bitbenders.theentity.ui.theme.EntityGreen
import com.bitbenders.theentity.ui.theme.EntityRed

/**
 * Reusable loading indicator for the game UI.
 * Displays when waiting for backend responses.
 */
@Composable
fun GameLoadingIndicator(
    message: String = "INITIALIZING...",
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EntityBlack)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(
            color = EntityGreen,
            modifier = Modifier.size(64.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = message,
            color = EntityGreen,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Reusable error display dialog.
 * Shows error messages with retry button.
 */
@Composable
fun ErrorDialog(
    title: String = "ERROR",
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = EntityBlack.copy(alpha = 0.8f))
            .clickable(enabled = false) { },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(EntityBlack)
                .border(2.dp, EntityRed)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                color = EntityRed,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = EntityGreen,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                GameButton(
                    text = "RETRY",
                    onClick = onRetry,
                    modifier = Modifier.weight(1f),
                )
                GameButton(
                    text = "DISMISS",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/**
 * Reusable game button with consistent styling.
 * Use throughout the game for all interactive buttons.
 */
@Composable
fun GameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textColor: androidx.compose.ui.graphics.Color = EntityGreen,
    borderColor: androidx.compose.ui.graphics.Color = EntityGreen,
) {
    Box(
        modifier = modifier
            .border(2.dp, borderColor)
            .background(EntityBlack)
            .clickable(enabled = enabled) { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (enabled) textColor else EntityGreen.copy(alpha = 0.5f),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

/**
 * Reusable info box for displaying game information.
 */
@Composable
fun GameInfoBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    labelColor: androidx.compose.ui.graphics.Color = EntityGreen,
    valueColor: androidx.compose.ui.graphics.Color = EntityGreen,
) {
    Column(
        modifier = modifier
            .border(1.dp, EntityBorder)
            .background(EntityBlack)
            .padding(12.dp),
    ) {
        Text(
            text = label,
            color = labelColor,
            style = MaterialTheme.typography.labelSmall,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = valueColor,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

/**
 * Reusable status bar showing game progress.
 */
@Composable
fun GameStatusBar(
    timer: String,
    strikes: Int,
    maxStrikes: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(EntityBlack)
            .border(1.dp, EntityBorder)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "T-$timer",
            color = EntityGreen,
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = "STRIKES: $strikes/$maxStrikes",
            color = if (strikes > 0) EntityRed else EntityGreen,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

/**
 * Reusable cipher slot display.
 */
@Composable
fun CipherSlot(
    value: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(64.dp)
            .border(2.dp, EntityGreen)
            .background(EntityBlack)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = value ?: "[ _ ]",
            color = EntityGreen,
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

/**
 * Reusable message display (chat bubble style).
 */
@Composable
fun GameMessage(
    text: String,
    isFromSystem: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = if (isFromSystem) Arrangement.Start else Arrangement.End,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .border(1.dp, if (isFromSystem) EntityGreen else EntityBorder)
                .background(EntityBlack)
                .padding(12.dp),
        ) {
            Text(
                text = text,
                color = if (isFromSystem) EntityGreen else EntityGreen.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

/**
 * Reusable progress bar for cipher collection.
 */
@Composable
fun CipherProgress(
    collected: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "CIPHERS COLLECTED: $collected/$total",
            color = EntityGreen,
            style = MaterialTheme.typography.labelSmall,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .border(1.dp, EntityGreen)
                .background(EntityBlack),
        ) {
            repeat(collected) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(EntityGreen),
                )
            }
            repeat(total - collected) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(EntityBlack),
                )
            }
        }
    }
}

/**
 * Reusable divider with text.
 */
@Composable
fun GameDivider(
    text: String? = null,
    modifier: Modifier = Modifier,
) {
    if (text == null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(EntityBorder),
        )
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(EntityBorder))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = EntityGreen,
                style = MaterialTheme.typography.labelSmall,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(EntityBorder))
        }
    }
}

