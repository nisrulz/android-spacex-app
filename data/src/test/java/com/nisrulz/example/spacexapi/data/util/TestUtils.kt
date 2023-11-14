package com.nisrulz.example.spacexapi.data.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
private val testDispatcher = UnconfinedTestDispatcher()
fun runUnconfinedTest(block: suspend (TestScope) -> Unit) = runTest(testDispatcher) { block(this) }