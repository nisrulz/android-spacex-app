package com.nisrulz.example.spacexapi.presentation.features.bookmarkedlaunches

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.nisrulz.example.spacexapi.domain.usecase.GetAllBookmarkedLaunches
import com.nisrulz.example.spacexapi.domain.usecase.ToggleBookmarkLaunchInfo
import com.nisrulz.example.spacexapi.presentation.common.UiEvent
import com.nisrulz.example.spacexapi.presentation.util.TestFactory
import com.nisrulz.example.spacexapi.presentation.util.runUnconfinedTest
import com.nisrulz.example.spacexapi.presentation.util.testDispatcher
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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
        coEvery { getAllBookmarkedLaunches() } returns flowOf(emptyList())

        sut = BookmarkedLaunchesViewModel(
            coroutineDispatcher = testDispatcher,
            bookmarkLaunchInfo = bookmarkLaunchInfo,
            getAllBookmarkedLaunches = getAllBookmarkedLaunches
        )
    }

    @Test
    fun `getListOfBookmarkedLaunches() should update uiState with list of bookmarked launches`() =
        runUnconfinedTest {
            val list = TestFactory.buildListOfBookmarkedLaunchInfo()
            coEvery { getAllBookmarkedLaunches() } returns flowOf(list)
            sut.getListOfBookmarkedLaunches().join()
            assertThat(sut.uiState.value.isLoading).isFalse()
            assertThat(sut.uiState.value.data).isEqualTo(list)
        }

    @Test
    fun `bookmark() should call bookmarkLaunchInfo()`() = runUnconfinedTest {
        val launchInfo = TestFactory.buildLaunchInfo()
        coEvery { bookmarkLaunchInfo(any()) } returns Unit
        sut.bookmark(launchInfo)
        coVerify { bookmarkLaunchInfo(launchInfo) }
    }

    @Test
    fun `showError() should update uiEvent with ShowSnackBar`() = runUnconfinedTest {
        val message = "Test Error"
        coEvery { getAllBookmarkedLaunches() } returns flow { throw Exception(message) }
        sut.eventFlow.test {
            sut.getListOfBookmarkedLaunches().join()
            assertThat(awaitItem()).isEqualTo(UiEvent.ShowSnackBar(message))
        }
    }
}
