package com.nisrulz.example.spacexapi.presentation.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

class Navigator(private val backStack: NavBackStack<NavKey>) {
    fun navigate(key: NavKey) {
        backStack.add(key)
    }

    fun goBack() {
        backStack.removeLastOrNull()
    }
}
