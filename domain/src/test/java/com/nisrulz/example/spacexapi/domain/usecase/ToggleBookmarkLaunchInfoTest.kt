package com.nisrulz.example.spacexapi.domain.usecase

import com.nisrulz.example.spacexapi.domain.repository.LaunchesRepository
import com.nisrulz.example.spacexapi.domain.util.TestFactory
import com.nisrulz.example.spacexapi.domain.util.runUnconfinedTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class ToggleBookmarkLaunchInfoTest {

    private lateinit var repository: LaunchesRepository
    private lateinit var bookmarkLaunchInfo: ToggleBookmarkLaunchInfo

    @Before
    fun setUp() {
        repository = mockk()
        bookmarkLaunchInfo = ToggleBookmarkLaunchInfo(repository)
    }

    @Test
    fun `invoke should call true setBookmarkState on repository`() = runUnconfinedTest {
        // Given
        val launchInfo = TestFactory.buildLaunchInfo()
        coEvery { repository.setBookmark(any(), true) } returns Unit

        // When
        bookmarkLaunchInfo(launchInfo)

        // Then
        coVerify { repository.setBookmark(any(), true) }
    }
}