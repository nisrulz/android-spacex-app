package com.nisrulz.example.spacexapi.presentation.features.listoflaunches

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.nisrulz.example.spacexapi.common.analytics.InUseAnalytics
import com.nisrulz.example.spacexapi.common.logger.InUseLoggers
import com.nisrulz.example.spacexapi.domain.usecase.GetAllBookmarkedLaunches
import com.nisrulz.example.spacexapi.domain.usecase.GetAllLaunches
import com.nisrulz.example.spacexapi.domain.usecase.ToggleBookmarkLaunchInfo
import com.nisrulz.example.spacexapi.presentation.features.listoflaunches.ListOfLaunchesViewModel.ListOfLaunchesUiEvent.NavigateToDetails
import com.nisrulz.example.spacexapi.presentation.features.listoflaunches.ListOfLaunchesViewModel.ListOfLaunchesUiEvent.ShowSnackBar
import com.nisrulz.example.spacexapi.presentation.features.listoflaunches.ListOfLaunchesViewModel.ListOfLaunchesUiState.Loading
import com.nisrulz.example.spacexapi.presentation.features.listoflaunches.ListOfLaunchesViewModel.ListOfLaunchesUiState.Success
import com.nisrulz.example.spacexapi.presentation.util.TestFactory
import com.nisrulz.example.spacexapi.presentation.util.runUnconfinedTest
import com.nisrulz.example.spacexapi.presentation.util.testDispatcher
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import org.junit.Before
import org.junit.Test

class ListOfLaunchesViewModelTest {
    private lateinit var sut: ListOfLaunchesViewModel
    private lateinit var getAllLaunches: GetAllLaunches
    private lateinit var bookmarkLaunchInfo: ToggleBookmarkLaunchInfo
    private lateinit var getAllBookmarkedLaunches: GetAllBookmarkedLaunches
    private lateinit var logger: InUseLoggers
    private lateinit var analytics: InUseAnalytics

    @Before
    fun setup() {
        getAllLaunches = mockk()
        bookmarkLaunchInfo = mockk()
        getAllBookmarkedLaunches = mockk()

        logger = mockk {
            every { log(any<String>()) } just runs
        }
        analytics = mockk {
            every { trackEvent(any<String>()) } just runs
        }

        sut = ListOfLaunchesViewModel(
            coroutineDispatcher = testDispatcher,
            getAllLaunches = getAllLaunches,
            bookmarkLaunchInfo = bookmarkLaunchInfo,
            getAllBookmarkedLaunches = getAllBookmarkedLaunches,
            logger = logger,
            analytics = analytics
        )
    }

    @Test
    fun `getListOfLaunches() should update uiState with Success`() = runUnconfinedTest {
        // Given
        val list = TestFactory.buildListOfLaunchInfo()
        coEvery { getAllLaunches() } returns flowOf(list)

        sut.uiState.test {
            // When
            sut.getListOfLaunches()

            // Then
            assertThat(awaitItem()).isEqualTo(Loading)
            assertThat(awaitItem()).isEqualTo(Success(list))
        }
    }

    @Test
    fun `getListOfBookmarkedLaunches() should update uiState with Success`() = runUnconfinedTest {
        // Given
        val list = TestFactory.buildListOfBookmarkedLaunchInfo()
        coEvery { getAllBookmarkedLaunches() } returns flowOf(list)

        sut.uiState.test {
            // When
            sut.getListOfBookmarkedLaunches()

            // Then
            assertThat(awaitItem()).isEqualTo(Loading)
            assertThat(awaitItem()).isEqualTo(Success(list))
        }
    }

    @Test
    fun `bookmark() should call bookmarkLaunchInfo()`() = runUnconfinedTest {
        // Given
        val launchInfo = TestFactory.buildLaunchInfo()
        coEvery { bookmarkLaunchInfo(any()) } returns Unit

        // When
        sut.bookmark(launchInfo)

        // Then
        coVerify {
            bookmarkLaunchInfo(launchInfo)
        }
    }

    @Test
    fun `showError() should update uiEvent with ShowSnackBar`() = runUnconfinedTest {
        // Given
        val message = "Test Error Message"

        // Then
        sut.eventFlow.receiveAsFlow().test {
            // When
            sut.showError(message)

            // Then
            assertThat(awaitItem()).isEqualTo(ShowSnackBar(message))
            verify { logger.log(any<String>()) }
            verify { analytics.trackEvent(any<String>()) }
        }
    }

    @Test
    fun `navigateToDetails() should update uiEvent with NavigateToDetails`() = runUnconfinedTest {
        // Given
        val launchId = "TestLaunchId"

        // Then
        sut.eventFlow.receiveAsFlow().test {
            // When
            sut.navigateToDetails(launchId)

            // Then
            assertThat(awaitItem()).isEqualTo(NavigateToDetails(launchId))
            verify { logger.log(any<String>()) }
            verify { analytics.trackEvent(any<String>()) }
        }
    }
}
