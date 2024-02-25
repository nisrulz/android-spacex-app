package com.nisrulz.example.spacexapi.presentation.features.bookmarkedlaunches

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.nisrulz.example.spacexapi.domain.usecase.GetAllBookmarkedLaunches
import com.nisrulz.example.spacexapi.domain.usecase.ToggleBookmarkLaunchInfo
import com.nisrulz.example.spacexapi.presentation.features.bookmarkedlaunches.BookmarkedLaunchesViewModel.UiEvent.NavigateBack
import com.nisrulz.example.spacexapi.presentation.features.bookmarkedlaunches.BookmarkedLaunchesViewModel.UiEvent.NavigateToDetails
import com.nisrulz.example.spacexapi.presentation.features.bookmarkedlaunches.BookmarkedLaunchesViewModel.UiEvent.ShowSnackBar
import com.nisrulz.example.spacexapi.presentation.util.TestFactory
import com.nisrulz.example.spacexapi.presentation.util.runUnconfinedTest
import com.nisrulz.example.spacexapi.presentation.util.testDispatcher
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import org.junit.Before
import org.junit.Test

class BookmarkedLaunchesViewModelTest {
    private lateinit var sut: BookmarkedLaunchesViewModel
    private lateinit var bookmarkLaunchInfo: ToggleBookmarkLaunchInfo
    private lateinit var getAllBookmarkedLaunches: GetAllBookmarkedLaunches

    @Before
    fun setup() {
        bookmarkLaunchInfo = mockk()
        getAllBookmarkedLaunches = mockk()

        sut = BookmarkedLaunchesViewModel(
            coroutineDispatcher = testDispatcher,
            bookmarkLaunchInfo = bookmarkLaunchInfo,
            getAllBookmarkedLaunches = getAllBookmarkedLaunches
        )
    }

    @Test
    fun `getListOfBookmarkedLaunches() should update uiState with list of bookmarked launches`() =
        runUnconfinedTest {
            // Given
            val list = TestFactory.buildListOfBookmarkedLaunchInfo()
            coEvery { getAllBookmarkedLaunches() } returns flowOf(list)

            sut.uiState.test {
                // When
                sut.getListOfBookmarkedLaunches()

                // Then
                assertThat(awaitItem().isLoading).isTrue()
                assertThat(awaitItem().data).isEqualTo(list)
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
        }
    }

    @Test
    fun `navigateBack() should update uiEvent with NavigateBack`() = runUnconfinedTest {
        // Then
        sut.eventFlow.receiveAsFlow().test {
            // When
            sut.navigateBack()

            // Then
            assertThat(awaitItem()).isEqualTo(NavigateBack)
        }
    }
}
