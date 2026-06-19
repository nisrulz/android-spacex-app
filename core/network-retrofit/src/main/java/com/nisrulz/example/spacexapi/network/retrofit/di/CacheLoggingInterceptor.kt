package com.nisrulz.example.spacexapi.network.retrofit.di

import okhttp3.Interceptor
import okhttp3.Response

internal class CacheLoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.cacheResponse != null) {
            println("🧠 Successful Response from MEMORY_CACHE\n" + "\t${request.method} ${request.url}")
        } else if (response.networkResponse != null) {
            println("☁️ Successful Response from NETWORK\n" + "\t${request.method} ${request.url}")
        }
        return response
    }
}
