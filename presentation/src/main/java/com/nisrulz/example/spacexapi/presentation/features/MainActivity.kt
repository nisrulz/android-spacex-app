package com.nisrulz.example.spacexapi.presentation.features

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.nisrulz.example.spacexapi.presentation.navigation.AppNavigation
import com.nisrulz.example.spacexapi.presentation.theme.SpacexAPITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppEntryPoint()
        }
    }
}

@Composable
private fun AppEntryPoint() {
    SpacexAPITheme {
        AppNavigation()
    }
}

