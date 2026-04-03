package com.bitbenders.theentity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.bitbenders.theentity.ui.navigation.EntityNavGraph
import com.bitbenders.theentity.ui.theme.EntityBlack
import com.bitbenders.theentity.ui.theme.TheEntityTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide the system UI or color the status bar black for full immersion
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            TheEntityTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = EntityBlack
                ) {
                    EntityNavGraph()
                }
            }
        }
    }
}

