package com.nisrulz.example.spacexapi.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.nisrulz.example.spacexapi.domain.repository.LaunchesRepository
import com.nisrulz.example.spacexapi.domain.util.TestFactory
import com.nisrulz.example.spacexapi.domain.util.runUnconfinedTest
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class GetLaunchDetailTest {
    private lateinit var repository: LaunchesRepository
    private lateinit var getLaunchDetail: GetLaunchDetail

    @Before
    fun setUp() {
        repository = mockk()
        getLaunchDetail = GetLaunchDetail(repository)
    }

    @Test
    fun `invoke should return a LaunchInfo if item with id is found via repository`() =
        runUnconfinedTest {
            // Given
            val launchInfo = TestFactory.buildLaunchInfo()
            coEvery { repository.getLaunchDetail(launchInfo.id) } returns launchInfo

            // When
            val result = getLaunchDetail(launchInfo.id)

            // Then
            assertThat(result).isEqualTo(launchInfo)
        }
}
