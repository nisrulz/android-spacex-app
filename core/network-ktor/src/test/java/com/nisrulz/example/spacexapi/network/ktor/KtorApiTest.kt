package com.nisrulz.example.spacexapi.network.ktor

import com.google.common.truth.Truth.assertThat
import com.nisrulz.example.spacexapi.network.dto.LaunchInfoResponse
import com.nisrulz.example.spacexapi.network.dto.Links
import com.nisrulz.example.spacexapi.network.dto.Patch
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Test

class KtorApiTest {

    private fun createMockClient(handler: MockRequestHandler) = HttpClient(MockEngine) {
        install(ContentNegotiation) {
            json(Json {
                coerceInputValues = true
                ignoreUnknownKeys = true
            })
        }
        engine {
            addHandler(handler)
        }
    }

    @Test
    fun `SHOULD return list of launches WHEN request succeeds`() = runTest {
        // Given
        val expected = listOf(
            LaunchInfoResponse(
                id = "1",
                name = "Flight 1",
                flight_number = 1,
                success = true,
                capsules = emptyList(),
                cores = emptyList(),
                crew = emptyList(),
                failures = emptyList(),
                links = Links(patch = Patch(small = "logo.png")),
                payloads = emptyList(),
                ships = emptyList()
            )
        )

        val client = createMockClient {
            respond(
                content = """
                    [{"id":"1","name":"Flight 1","flight_number":1,"success":true,
                    "capsules":[],"cores":[],"crew":[],"failures":[],
                    "links":{"patch":{"small":"logo.png"}},
                    "payloads":[],"ships":[]}]
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType to listOf("application/json"))
            )
        }
        val sut = KtorApi(client)

        // When
        val result = sut.getAllLaunches()

        // Then
        assertThat(result).isNotEmpty()
        assertThat(result.first().id).isEqualTo("1")
        assertThat(result.first().name).isEqualTo("Flight 1")
    }

    @Test
    fun `SHOULD throw exception WHEN request fails`() = runTest {
        // Given
        val client = createMockClient {
            throw RuntimeException("Network error")
        }
        val sut = KtorApi(client)

        // When & Then
        try {
            sut.getAllLaunches()
            throw AssertionError("Expected exception")
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo("Network error")
        }
    }

    @Test
    fun `SHOULD return empty list WHEN response is empty`() = runTest {
        // Given
        val client = createMockClient {
            respond(
                content = "[]",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType to listOf("application/json"))
            )
        }
        val sut = KtorApi(client)

        // When
        val result = sut.getAllLaunches()

        // Then
        assertThat(result).isEmpty()
    }
}
