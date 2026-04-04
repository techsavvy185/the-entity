package com.bitbenders.theentity.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bitbenders.theentity.ui.theme.EntityBlack
import com.bitbenders.theentity.ui.theme.EntityBorder
import com.bitbenders.theentity.ui.theme.EntityGreen

/**
 * Retro on-screen keyboard for the P1 terminal.
 *
 * Always visible during rounds 1–3 so we never show the Android IME.
 */
@Composable
fun RetroTerminalKeyboard(
    onKeyPressed: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isNumeric by remember { mutableStateOf(false) }

    // QWERTY-ish alpha layout and numeric/symbol layout
    val alphaRows = listOf(
        listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
        listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
        listOf("Z", "X", "C", "V", "B", "N", "M"),
        listOf("NUM", "SPACE", "BACK", "CLR", "SEND"),
    )

    val numRows = listOf(
        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
        listOf("-", "_", "/", "?", ":", ";", "(", ")"),
        listOf(".", ",", "@", "#", "&"),
        listOf("ABC", "SPACE", "BACK", "CLR", "SEND"),
    )

    val rows = if (isNumeric) numRows else alphaRows

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 220.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                row.forEach { rawLabel ->
                    val label = rawLabel
                    val weight = when (label) {
                        "SPACE" -> 3f
                        "NUM", "ABC" -> 1.5f
                        else -> 1f
                    }
                    RetroKeyButton(
                        label = label,
                        modifier = Modifier.weight(weight),
                        onClick = {
                            when (label) {
                                "NUM" -> isNumeric = true
                                "ABC" -> isNumeric = false
                                else -> onKeyPressed(label)
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun RetroKeyButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = EntityBlack,
            contentColor = EntityGreen,
        ),
        border = BorderStroke(1.dp, EntityBorder),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp),
    ) {
        val display = when (label) {
            "SPACE" -> "␣"
            "BACK" -> "⌫"
            "CLR" -> "CLR"
            "SEND" -> "⏎"
            "NUM" -> "123"
            "ABC" -> "ABC"
            else -> label
        }
        Text(
            text = display,
            style = MaterialTheme.typography.labelLarge,
            color = EntityGreen,
        )
    }
}
