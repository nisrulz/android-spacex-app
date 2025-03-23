package com.nisrulz.example.spacexapi.data.repository

import com.google.common.truth.Truth.assertThat
import com.nisrulz.example.spacexapi.common.contract.utils.NetworkUtils
import com.nisrulz.example.spacexapi.data.util.TestFactory
import com.nisrulz.example.spacexapi.data.util.runUnconfinedTest
import com.nisrulz.example.spacexapi.domain.repository.LaunchesRepository
import com.nisrulz.example.spacexapi.network.retrofit.SpaceXLaunchesApi
import com.nisrulz.example.spacexapi.storage.roomdb.LaunchInfoDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test

class LaunchesRepositoryImplTest {

    private val dao: LaunchInfoDao = mockk()
    private val api: SpaceXLaunchesApi = mockk()
    private val networkUtils: NetworkUtils = mockk()

    private val sut: LaunchesRepository = LaunchesRepositoryImpl(
        localDataSource = dao,
        remoteDataSource = api,
        networkUtils = networkUtils
    )

    @Before
    fun setUp() {
        every { networkUtils.isInternetAvailable() } returns true
    }

    @Test
    fun `getListOfLaunches() returns a list of LaunchInfo on success`() = runUnconfinedTest {
        // Given
        coEvery { api.getAllLaunches() } returns TestFactory.buildListOfLaunchInfoResponse()
        coEvery { dao.deleteAll() } returns Unit
        coEvery { dao.getAllBookmarked() } returns flowOf(emptyList())
        coEvery { dao.insertAll(any()) } returns Unit
        coEvery { dao.getAll() } returns flowOf(TestFactory.buildListOfLaunchInfoEntity())
        val expected = TestFactory.buildListOfLaunchInfo()

        // When
        val resultFlow = sut.getListOfLaunches()

        // Then
        resultFlow.collect {
            assertThat(it).isEqualTo(expected)
        }
    }

    @Test
    fun `getListOfLaunches() returns an empty list when repository returns nothing`() =
        runUnconfinedTest {
            // Given
            coEvery { api.getAllLaunches() } returns TestFactory.buildListOfLaunchInfoResponse()
            coEvery { dao.deleteAll() } returns Unit
            coEvery { dao.getAllBookmarked() } returns flowOf(emptyList())
            coEvery { dao.insertAll(any()) } returns Unit
            coEvery { dao.getAll() } returns flowOf(emptyList())

            // When
            val resultFlow = sut.getListOfLaunches()

            // Then
            resultFlow.collect {
                assertThat(it).isEmpty()
            }
        }

    @Test
    fun `getAllBookmarked() returns a list of bookmarked LaunchInfo on success`() =
        runUnconfinedTest {
            // Given
            coEvery {
                dao.getAllBookmarked()
            } returns flowOf(TestFactory.buildListOfBookmarkedLaunchInfoEntity())
            val expected = TestFactory.buildListOfBookmarkedLaunchInfo()

            // When
            val resultFlow = sut.getAllBookmarked()

            // Then
            resultFlow.collect {
                assertThat(it).isEqualTo(expected)
            }
        }

    @Test
    fun `getAllBookmarked() returns an empty list when repository returns nothing`() =
        runUnconfinedTest {
            // Given
            coEvery { dao.getAllBookmarked() } returns flowOf(emptyList())

            // When
            val resultFlow = sut.getAllBookmarked()

            // Then
            resultFlow.collect {
                assertThat(it).isEmpty()
            }
        }

    @Test
    fun `getLaunchDetail() returns a valid LaunchInfo for id on success`() = runUnconfinedTest {
        // Given
        val id = TestFactory.buildLaunchInfoEntity().id
        coEvery { dao.getById(any()) } returns TestFactory.buildLaunchInfoEntity()
        val expected = TestFactory.buildLaunchInfo()

        // When
        val result = sut.getLaunchDetail(id)

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `getLaunchDetail() returns null when item with id cannot be found`() = runUnconfinedTest {
        // Given
        val id = TestFactory.buildLaunchInfoEntity().id
        coEvery { dao.getById(any()) } returns null

        // When
        val result = sut.getLaunchDetail(id)

        // Then
        assertThat(result).isEqualTo(null)
    }

    @Test
    fun `getListOfLaunches() returns a list of LaunchInfo from local storage only when there is no internet available`() =
        runUnconfinedTest {
            // Given
            every { networkUtils.isInternetAvailable() } returns false
            coEvery { dao.getAll() } returns flowOf(TestFactory.buildListOfLaunchInfoEntity())
            val expected = TestFactory.buildListOfLaunchInfo()

            // When
            val resultFlow = sut.getListOfLaunches()

            // Then
            resultFlow.collect {
                assertThat(it).isEqualTo(expected)
            }

            coVerify(exactly = 0) { api.getAllLaunches() }
            coVerify(exactly = 1) { dao.getAll() }
        }
}
