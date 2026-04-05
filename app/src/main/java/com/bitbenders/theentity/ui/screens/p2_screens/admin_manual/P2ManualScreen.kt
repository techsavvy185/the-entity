package com.bitbenders.theentity.ui.screens.p2_screens.admin_manual

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bitbenders.theentity.ui.theme.EntityBlack
import com.bitbenders.theentity.ui.theme.EntityBorder
import com.bitbenders.theentity.ui.theme.EntityGreen

@Composable
fun P2ManualScreen(
    viewModel: P2ManualViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EntityBlack)
            .padding(16.dp)
    ) {
        Text(
            text = "CONTAINMENT PROTOCOL MANUAL",
            color = EntityGreen,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Text("TABLE 1: PERSONA OVERRIDES", color = EntityGreen, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(uiState.personaOverrides) { rule ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EntityBorder)
                        .padding(12.dp)
                ) {
                    Text(text = "SUBJECT ID: ${rule.title}", color = EntityGreen, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = rule.details, color = EntityGreen, style = MaterialTheme.typography.bodyMedium)
                }
            }        }  }}

