package com.nisrulz.example.spacexapi.presentation.features.launchdetail

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.nisrulz.example.spacexapi.domain.usecase.GetLaunchDetail
import com.nisrulz.example.spacexapi.domain.usecase.ToggleBookmarkLaunchInfo
import com.nisrulz.example.spacexapi.presentation.features.launchdetail.LaunchDetailViewModel.LaunchDetailUiEvent.ShowSnackBar
import com.nisrulz.example.spacexapi.presentation.features.launchdetail.LaunchDetailViewModel.LaunchDetailUiState.Loading
import com.nisrulz.example.spacexapi.presentation.features.launchdetail.LaunchDetailViewModel.LaunchDetailUiState.Success
import com.nisrulz.example.spacexapi.presentation.util.TestFactory
import com.nisrulz.example.spacexapi.presentation.util.runUnconfinedTest
import com.nisrulz.example.spacexapi.presentation.util.testDispatcher
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.receiveAsFlow
import org.junit.Before
import org.junit.Test

class LaunchDetailViewModelTest {
    private lateinit var sut: LaunchDetailViewModel
    private lateinit var getLaunchDetail: GetLaunchDetail
    private lateinit var bookmarkLaunchInfo: ToggleBookmarkLaunchInfo

    @Before
    fun setup() {
        getLaunchDetail = mockk()
        bookmarkLaunchInfo = mockk()
        sut =
            LaunchDetailViewModel(
                coroutineDispatcher = testDispatcher,
                getLaunchDetail = getLaunchDetail,
                bookmarkLaunchInfo = bookmarkLaunchInfo
            )
    }

    @Test
    fun `getLaunchInfoDetails() should update uiState with Success`() = runUnconfinedTest {
        // Given
        val launchId = "TestLaunchId"
        val expected = TestFactory.buildLaunchInfo()
        coEvery { getLaunchDetail(launchId) } returns expected

        sut.uiState.test {
            // When
            sut.getLaunchInfoDetails(launchId)

            // Then
            assertThat(awaitItem()).isEqualTo(Loading)
            assertThat(awaitItem()).isEqualTo(Success(expected))
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
}
