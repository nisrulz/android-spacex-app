package com.nisrulz.example.spacexapi.presentation.features.launchdetail

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.nisrulz.example.spacexapi.analytics.InUseAnalytics
import com.nisrulz.example.spacexapi.domain.usecase.GetLaunchDetail
import com.nisrulz.example.spacexapi.domain.usecase.ToggleBookmarkLaunchInfo
import com.nisrulz.example.spacexapi.presentation.common.UiEvent
import com.nisrulz.example.spacexapi.presentation.util.TestFactory
import com.nisrulz.example.spacexapi.presentation.util.runUnconfinedTest
import com.nisrulz.example.spacexapi.presentation.util.testDispatcher
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test

class LaunchDetailViewModelTest {
    private lateinit var sut: LaunchDetailViewModel
    private lateinit var getLaunchDetail: GetLaunchDetail
    private lateinit var bookmarkLaunchInfo: ToggleBookmarkLaunchInfo
    private lateinit var analytics: InUseAnalytics

    @Before
    fun setup() {
        getLaunchDetail = mockk()
        bookmarkLaunchInfo = mockk()
        analytics = mockk(relaxed = true)
        sut = LaunchDetailViewModel(
            coroutineDispatcher = testDispatcher,
            getLaunchDetail = getLaunchDetail,
            bookmarkLaunchInfo = bookmarkLaunchInfo,
            analytics = analytics
        )
    }

    @Test
    fun `getLaunchInfoDetails() should update uiState with launch details`() = runUnconfinedTest {
        val expected = TestFactory.buildLaunchInfo()
        every { getLaunchDetail("test-id") } returns flowOf(expected)
        sut.getLaunchInfoDetails("test-id").join()
        assertThat(sut.uiState.value.isLoading).isFalse()
        assertThat(sut.uiState.value.data).isEqualTo(expected)
    }

    @Test
    fun `bookmark() should call bookmarkLaunchInfo()`() = runUnconfinedTest {
        val launchInfo = TestFactory.buildLaunchInfo()
        coEvery { bookmarkLaunchInfo(any()) } returns Unit
        sut.bookmark(launchInfo)
        coVerify { bookmarkLaunchInfo(launchInfo) }
    }

    @Test
    fun `getLaunchInfoDetails() should reactively update uiState when launch changes`() = runUnconfinedTest {
        val launchFlow = MutableStateFlow(TestFactory.buildLaunchInfo())
        every { getLaunchDetail("test-id") } returns launchFlow
        val job = sut.getLaunchInfoDetails("test-id")
        launchFlow.value = launchFlow.value.copy(isBookmarked = true)
        assertThat(sut.uiState.value.data?.isBookmarked).isTrue()
        job.cancel()
    }

    @Test
    fun `showError() should update uiEvent with ShowSnackBar`() = runUnconfinedTest {
        sut.eventFlow.test {
            sut.getLaunchInfoDetails(null).join()
            assertThat(awaitItem()).isEqualTo(UiEvent.ShowSnackBar("No Data"))
        }
    }
}
