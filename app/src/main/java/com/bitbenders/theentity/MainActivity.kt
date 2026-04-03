package com.bitbenders.theentity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

        enableEdgeToEdge()

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

