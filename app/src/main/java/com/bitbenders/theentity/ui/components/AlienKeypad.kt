package com.bitbenders.theentity.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bitbenders.theentity.ui.theme.EntityBlack
import com.bitbenders.theentity.ui.theme.EntityBorder
import com.bitbenders.theentity.ui.theme.EntityGreen

@Composable
fun AlienKeypad(
    symbols: List<String>,
    onSymbolClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val paddedSymbols = symbols.take(9).let { list ->
        if (list.size >= 9) list else list + List(9 - list.size) { "" }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 220.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp),
    ) {
        items(paddedSymbols) { symbol ->
            KeypadButton(
                symbol = symbol,
                enabled = symbol.isNotBlank(),
                onClick = { onSymbolClicked(symbol) },
            )
        }
    }
}

@Composable
private fun KeypadButton(
    symbol: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = EntityBlack,
            contentColor = EntityGreen,
            disabledContainerColor = EntityBlack.copy(alpha = 0.4f),
            disabledContentColor = EntityGreen.copy(alpha = 0.25f),
        ),
        border = BorderStroke(1.dp, EntityBorder),
        contentPadding = PaddingValues(vertical = 14.dp, horizontal = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
    ) {
        Text(
            text = symbol.ifBlank { " " },
            style = MaterialTheme.typography.titleLarge,
            color = EntityGreen,
        )
    }
}

