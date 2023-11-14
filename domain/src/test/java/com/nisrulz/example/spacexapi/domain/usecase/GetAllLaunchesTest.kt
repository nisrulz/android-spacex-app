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

class GetAllLaunchesTest {
    private lateinit var repository: LaunchesRepository
    private lateinit var getAllLaunches: GetAllLaunches

    @Before
    fun setUp() {
        repository = mockk()
        getAllLaunches = GetAllLaunches(repository)
    }

    @Test
    fun `invoke should return a Flow of Resource containing a list of all LaunchInfos`() =
        runUnconfinedTest {
            // Given
            val launchInfoList = TestFactory.buildListOfLaunchInfo()
            coEvery { repository.getListOfLaunches() } returns flowOf(launchInfoList)

            // When
            val resultFlow = getAllLaunches()

            // Then
            resultFlow.collect {
                Truth.assertThat(it).isEqualTo(launchInfoList)
            }
        }
}