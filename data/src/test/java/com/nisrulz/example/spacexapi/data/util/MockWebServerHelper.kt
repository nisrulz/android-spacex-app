package com.nisrulz.example.spacexapi.data.util

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


object MockWebServerHelper {
    /**
     * Create an instance of the Retrofit class
     */
    internal inline fun <reified T : Any> generateRetrofit(mockWebServer: MockWebServer): T =
        Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // Dummy url for testing
            .addConverterFactory(GsonConverterFactory.create())
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
        val file = File(uri.path)
        return String(file.readBytes())
    }
}