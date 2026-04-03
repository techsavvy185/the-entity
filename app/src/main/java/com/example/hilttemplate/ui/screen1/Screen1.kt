package com.example.hilttemplate.ui.screen1

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun Screen1(
    // 1. ADD THIS: You must accept the padding from the Scaffold!
    paddingValues: PaddingValues,
    viewModel: Screen1ViewModel = hiltViewModel(),
    onClickButton: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(100) {
                Text(
                    text = "Item $it",
                    modifier = Modifier.padding(16.dp) // Give items some breathing room
                )
            }
        }

        Button(
            onClick = { onClickButton() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                // We apply the padding here so it sits exactly ABOVE the bottom nav bar!
                .padding(paddingValues)
                .padding(bottom = 16.dp) // Add a little extra visual space
        ) {
            Text(text = "Click to go to screen2")
        }
    }
}