package com.nisrulz.example.spacexapi.presentation.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class NavigatorTest {
    private lateinit var navigator: Navigator
    private lateinit var backStack: NavBackStack<NavKey>

    @Before
    fun setup() {
        backStack = NavBackStack()
        navigator = Navigator(backStack)
    }

    @Test
    fun `navigate adds key to backStack`() {
        navigator.navigate(Home)
        assertEquals(1, backStack.size)
    }

    @Test
    fun `navigate adds multiple keys to backStack`() {
        navigator.navigate(Home)
        navigator.navigate(Bookmarks)
        navigator.navigate(Details("123"))
        assertEquals(3, backStack.size)
    }

    @Test
    fun `goBack removes last key from backStack`() {
        navigator.navigate(Home)
        navigator.navigate(Bookmarks)
        navigator.goBack()
        assertEquals(1, backStack.size)
    }

    @Test
    fun `goBack on empty backStack does not crash`() {
        navigator.goBack()
        assertEquals(0, backStack.size)
    }

    @Test
    fun `navigate and goBack maintains correct order`() {
        navigator.navigate(Home)
        navigator.navigate(Bookmarks)
        navigator.goBack()
        navigator.navigate(Details("456"))
        assertEquals(2, backStack.size)
    }
}
