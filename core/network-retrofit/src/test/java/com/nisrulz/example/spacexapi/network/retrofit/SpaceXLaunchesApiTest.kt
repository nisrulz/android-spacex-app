package com.nisrulz.example.spacexapi.network.retrofit

import com.google.common.truth.Truth.assertThat
import com.nisrulz.example.spacexapi.network.retrofit.dto.LaunchInfoResponse
import com.nisrulz.example.spacexapi.network.retrofit.util.MockWebServerHelper.generateRetrofit
import com.nisrulz.example.spacexapi.network.retrofit.util.MockWebServerHelper.setResponse
import com.nisrulz.example.spacexapi.network.retrofit.util.MockWebServerHelper.throwHttpException
import com.nisrulz.example.spacexapi.network.retrofit.util.MockWebServerHelper.throwIOException
import com.nisrulz.example.spacexapi.network.retrofit.util.runUnconfinedTest
import okhttp3.mockwebserver.MockWebServer
import okio.IOException
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

class SpaceXLaunchesApiTest {
    private val api: SpaceXLaunchesApi by lazy {
        generateRetrofit(
            mockWebServer
        )
    }
    private val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        mockWebServer.start()
    }

    @After
    fun shutdown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `SHOULD return a list of LaunchInfoResponse WHEN request succeeds`() = runUnconfinedTest {
        // Given
        setResponseForList()

        // When
        val response = api.getAllLaunches()

        // Then
        assertThat(response).apply {
            isInstanceOf(List::class.java)
            isNotEmpty()
        }
        response.forEach {
            assertThat(it).isInstanceOf(LaunchInfoResponse::class.java)
        }
    }

    @Test
    fun `SHOULD throw HttpException WHEN service responds with 404`() = runUnconfinedTest {
        // Given
        throwHttpExceptionForList()

        try {
            // When
            api.getAllLaunches()
            assert(false)
        } catch (e: Exception) {
            // Then
            assertThat(e).isInstanceOf(HttpException::class.java)
            assert(true)
        }
    }

    @Test
    fun `SHOULD throw IOException WHEN request is made but server terminates connection`() =
        runUnconfinedTest {
            // Given
            throwIOExceptionForList()

            try {
                // When
                api.getAllLaunches()
                assert(false)
            } catch (e: Exception) {
                // Then
                assertThat(e).isInstanceOf(IOException::class.java)
                assert(true)
            }
        }

    @Test
    fun `test BASE_URL is correct`() {
        // Given
        val baseUrl = SpaceXLaunchesApi.BASE_URL
        // Then
        assertThat(baseUrl).isEqualTo("https://api.spacexdata.com/")
    }

    //region Utils
    private val jsonForList = "response_items_list.json"

    private fun setResponseForList() = apply {
        mockWebServer.setResponse(jsonForList)
    }

    private fun throwHttpExceptionForList() = apply {
        mockWebServer.throwHttpException(jsonForList)
    }

    private fun throwIOExceptionForList() = apply {
        mockWebServer.throwIOException()
    }
    //endregion
}
