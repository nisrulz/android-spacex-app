package com.nisrulz.example.spacexapi.domain.usecase

import com.google.common.truth.Truth
import com.nisrulz.example.spacexapi.domain.repository.LaunchesRepository
import com.nisrulz.example.spacexapi.domain.util.TestFactory
import com.nisrulz.example.spacexapi.domain.util.runUnconfinedTest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test

class GetAllBookmarkedLaunchesTest {
    private lateinit var repository: LaunchesRepository
    private lateinit var getAllBookmarkedLaunches: GetAllBookmarkedLaunches

    @Before
    fun setUp() {
        repository = mockk()
        getAllBookmarkedLaunches = GetAllBookmarkedLaunches(repository)
    }

    @Test
    fun `invoke should return a Flow of Resource containing a list of bookmarked LaunchInfos`() =
        runUnconfinedTest {
            // Given
            val launchInfoList = TestFactory.buildListOfBookmarkedLaunchInfo()
            coEvery { repository.getAllBookmarked() } returns flowOf(launchInfoList)

            // When
            val resultFlow = getAllBookmarkedLaunches()

            // Then
            resultFlow.collect {
                Truth.assertThat(it).isEqualTo(launchInfoList)
            }
        }
}