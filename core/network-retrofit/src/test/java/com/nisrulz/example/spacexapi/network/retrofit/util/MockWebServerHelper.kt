package com.nisrulz.example.spacexapi.network.retrofit.util

import java.io.File
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import okio.Buffer
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object MockWebServerHelper {
    /**
     * Create an instance of the Retrofit class
     */
    internal inline fun <reified T : Any> generateRetrofit(mockWebServer: MockWebServer): T =
        Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // Dummy url for testing
            .addConverterFactory(jsonConvertorFactory())
            .build()
            .create(T::class.java)

    /**
     * Sets which response the [MockWebServer] should return when a request is made
     */
    internal fun MockWebServer.setResponse(fileName: String, responseCode: Int = 200) {
        enqueue(
            MockResponse()
                .setResponseCode(responseCode)
                .setBody(getFileAsString(fileName))
        )
    }

    /**
     * Helper function to simulate service closing connection before completion
     */
    internal fun MockWebServer.throwIOException() {
        enqueue(
            MockResponse()
                .setBody(Buffer().write(ByteArray(4096)))
                .setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY)
        )
    }

    /**
     * Helper function to simulate throwing Http Exception
     */
    internal fun MockWebServer.throwHttpException(fileName: String) {
        setResponse(fileName, 404)
    }

    /**
     * The the file in the [filePath] and return its content as a [String]
     */
    private fun getFileAsString(filePath: String): String {
        val uri = ClassLoader.getSystemResource(filePath)
        if (uri == null) {
            println("File not found! Check if the response json is placed in resources directory")
        }
        val file = File(uri.path)
        return String(file.readBytes())
    }

    /**
     * Kotlinx Serialization JSON Convertor Factory
     */
    private fun jsonConvertorFactory(): Converter.Factory {
        val json =
            Json {
                coerceInputValues = true
            }
        return json.asConverterFactory("application/json".toMediaType())
    }
}
