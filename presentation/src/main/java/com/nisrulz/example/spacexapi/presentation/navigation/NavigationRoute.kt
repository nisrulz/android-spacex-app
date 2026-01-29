package com.nisrulz.example.spacexapi.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
data object Home : NavKey

@Serializable
data object Bookmarks : NavKey

@Serializable
data class Details(val launchId: String) : NavKey

