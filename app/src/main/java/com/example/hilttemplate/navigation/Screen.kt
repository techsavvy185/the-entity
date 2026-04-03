package com.example.hilttemplate.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Screen1 : Screen("screen_1", "Home", Icons.Default.Home)
    data object Screen2 : Screen("screen_2", "Search", Icons.Default.Search)
    data object Screen3 : Screen("screen_3", "Profile", Icons.Default.Person)
}