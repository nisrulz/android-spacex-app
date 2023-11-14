package com.nisrulz.example.spacexapi.data.remote

import com.google.common.truth.Truth.assertThat
import com.nisrulz.example.spacexapi.data.remote.dto.LaunchInfoResponse
import com.nisrulz.example.spacexapi.data.util.MockWebServerHelper.generateRetrofit
import com.nisrulz.example.spacexapi.data.util.MockWebServerHelper.setResponse
import com.nisrulz.example.spacexapi.data.util.MockWebServerHelper.throwHttpException
import com.nisrulz.example.spacexapi.data.util.MockWebServerHelper.throwIOException
import com.nisrulz.example.spacexapi.data.util.runUnconfinedTest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import okio.IOException
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

class SpaceXLaunchesApiTest {

    private val api: SpaceXLaunchesApi by lazy { generateRetrofit(mockWebServer) }
    private val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        mockWebServer.start(8080)
    }

    @After
    fun shutdown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getAllLaunches() returns a list of LaunchInfoResponse on success`() = runUnconfinedTest {
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
    fun `getAllLaunches() throws HttpException when service responds with 404 `() = runTest {
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
    fun `getAllLaunches() throws IOException when valid request is made but server terminates the connection`() =
        runTest {
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