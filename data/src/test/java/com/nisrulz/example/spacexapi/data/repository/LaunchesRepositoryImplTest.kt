package com.nisrulz.example.spacexapi.data.repository

import com.google.common.truth.Truth.assertThat
import com.nisrulz.example.spacexapi.common.contract.utils.NetworkUtils
import com.nisrulz.example.spacexapi.data.mapper.toDomainModelList
import com.nisrulz.example.spacexapi.data.util.TestFactory
import com.nisrulz.example.spacexapi.data.util.runUnconfinedTest
import com.nisrulz.example.spacexapi.domain.repository.LaunchesRepository
import com.nisrulz.example.spacexapi.network.RemoteDataSource
import com.nisrulz.example.spacexapi.storage.LocalDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test

class LaunchesRepositoryImplTest {

    private val localDataSource: LocalDataSource = mockk()
    private val remoteDataSource: RemoteDataSource = mockk()
    private val networkUtils: NetworkUtils = mockk()

    private val sut: LaunchesRepository = LaunchesRepositoryImpl(
        localDataSource = localDataSource,
        remoteDataSource = remoteDataSource,
        networkUtils = networkUtils
    )

    @Before
    fun setUp() {
        every { networkUtils.isInternetAvailable() } returns true
    }

    private fun setupHappyPath() {
        coEvery { remoteDataSource.getAllLaunches() } returns TestFactory.buildListOfLaunchInfoResponse()
        coEvery { localDataSource.replaceAllPreservingBookmarks(any()) } returns Unit
        coEvery { localDataSource.getAll() } returns flowOf(TestFactory.buildListOfLaunchInfo())
    }

    private suspend fun <T> assertFlowEmits(expected: T, block: suspend () -> Flow<T>) {
        block().collect { assertThat(it).isEqualTo(expected) }
    }

    @Test
    fun `getListOfLaunches() returns a list of LaunchInfo on success`() = runUnconfinedTest {
        setupHappyPath()
        assertFlowEmits(TestFactory.buildListOfLaunchInfo()) { sut.getListOfLaunches() }
    }

    @Test
    fun `getListOfLaunches() returns an empty list when repository returns nothing`() =
        runUnconfinedTest {
            coEvery { remoteDataSource.getAllLaunches() } returns TestFactory.buildListOfLaunchInfoResponse()
            coEvery { localDataSource.replaceAllPreservingBookmarks(any()) } returns Unit
            coEvery { localDataSource.getAll() } returns flowOf(emptyList())
            assertFlowEmits(emptyList()) { sut.getListOfLaunches() }
        }

    @Test
    fun `getAllBookmarked() returns a list of bookmarked LaunchInfo on success`() =
        runUnconfinedTest {
            coEvery { localDataSource.getAllBookmarked() } returns flowOf(TestFactory.buildListOfBookmarkedLaunchInfo())
            assertFlowEmits(TestFactory.buildListOfBookmarkedLaunchInfo()) { sut.getAllBookmarked() }
        }

    @Test
    fun `getAllBookmarked() returns an empty list when repository returns nothing`() =
        runUnconfinedTest {
            coEvery { localDataSource.getAllBookmarked() } returns flowOf(emptyList())
            assertFlowEmits(emptyList()) { sut.getAllBookmarked() }
        }

    @Test
    fun `getLaunchDetail() returns a valid LaunchInfo for id on success`() = runUnconfinedTest {
        every { localDataSource.observeById(any()) } returns flowOf(TestFactory.buildLaunchInfo())
        assertFlowEmits(TestFactory.buildLaunchInfo()) { sut.getLaunchDetail("any-id") }
    }

    @Test
    fun `getLaunchDetail() returns null when item with id cannot be found`() = runUnconfinedTest {
        every { localDataSource.observeById(any()) } returns flowOf(null)
        assertFlowEmits(null) { sut.getLaunchDetail("any-id") }
    }

    @Test
    fun `getListOfLaunches() returns a list of LaunchInfo from local storage only when there is no internet available`() =
        runUnconfinedTest {
            every { networkUtils.isInternetAvailable() } returns false
            coEvery { localDataSource.getAll() } returns flowOf(TestFactory.buildListOfLaunchInfo())
            assertFlowEmits(TestFactory.buildListOfLaunchInfo()) { sut.getListOfLaunches() }
            coVerify(exactly = 0) { remoteDataSource.getAllLaunches() }
            coVerify(exactly = 1) { localDataSource.getAll() }
        }

    @Test
    fun `getListOfLaunches() falls back to local storage when refresh fails`() =
        runUnconfinedTest {
            coEvery { remoteDataSource.getAllLaunches() } throws IOException("network down")
            coEvery { localDataSource.getAll() } returns flowOf(TestFactory.buildListOfLaunchInfo())
            assertFlowEmits(TestFactory.buildListOfLaunchInfo()) { sut.getListOfLaunches() }
            coVerify(exactly = 1) { remoteDataSource.getAllLaunches() }
            coVerify(exactly = 1) { localDataSource.getAll() }
            coVerify(exactly = 0) { localDataSource.replaceAllPreservingBookmarks(any()) }
        }

    @Test
    fun `getListOfLaunches() replaces local data while preserving bookmarks atomically`() =
        runUnconfinedTest {
            val apiResponse = TestFactory.buildListOfLaunchInfoResponse()
            coEvery { remoteDataSource.getAllLaunches() } returns apiResponse
            coEvery { localDataSource.replaceAllPreservingBookmarks(any()) } returns Unit
            coEvery { localDataSource.getAll() } returns flowOf(TestFactory.buildListOfLaunchInfo())
            sut.getListOfLaunches().collect { }
            coVerify(exactly = 1) {
                localDataSource.replaceAllPreservingBookmarks(apiResponse.toDomainModelList())
            }
        }
}
