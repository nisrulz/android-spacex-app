package com.nisrulz.example.spacexapi.network.retrofit.di

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONArray
import org.json.JSONObject

internal class JSONPrettyPrintHttpLoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val contentType = response.header("Content-Type") ?: ""
        val rawBody = response.body
        val bodyString = if (rawBody != null && contentType.startsWith("application/json")) {
            rawBody.string()
        } else {
            null
        }

        Log.d(LOG_TAG, "--> ${request.method} ${request.url}")
        Log.d(LOG_TAG, "--> END ${request.method}")

        Log.d(LOG_TAG, "<-- ${response.code} ${response.message} ${response.request.url}")
        if (bodyString != null) {
            val formatted = try {
                when {
                    bodyString.trimStart().startsWith("{") -> JSONObject(bodyString).toString(2)
                    bodyString.trimStart().startsWith("[") -> JSONArray(bodyString).toString(2)
                    else -> bodyString
                }
            } catch (_: Exception) {
                bodyString
            }
            Log.d(LOG_TAG, formatted)
        }
        Log.d(LOG_TAG, "<-- END HTTP")

        return response.newBuilder().body(
            if (bodyString != null) bodyString.toResponseBody(rawBody?.contentType()) else rawBody
        ).build()
    }

    companion object {
        private const val LOG_TAG = "OkHttp"
    }
}
