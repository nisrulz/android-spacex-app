package com.nisrulz.example.spacexapi.network.retrofit.di

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

internal class CacheLoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.cacheResponse != null) {
            Timber.d("Successful Response from MEMORY_CACHE: ${request.method} ${request.url}")
        } else if (response.networkResponse != null) {
            Timber.d("Successful Response from NETWORK: ${request.method} ${request.url}")
        }
        return response
    }
}
