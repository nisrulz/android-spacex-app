package com.nisrulz.example.spacexapi.network.retrofit.di

import android.app.Application
import android.os.StrictMode
import com.nisrulz.example.spacexapi.network.retrofit.BuildConfig
import com.nisrulz.example.spacexapi.network.retrofit.SpaceXLaunchesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): SpaceXLaunchesApi {
        return retrofit.create(SpaceXLaunchesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, jsonConverter: Converter.Factory): Retrofit {
        return Retrofit.Builder().baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(jsonConverter).client(okHttpClient).build()
    }

    @Provides
    @Singleton
    fun provideConvertorFactory(): Converter.Factory {
        val json = Json {
            coerceInputValues = true
        }
        return json.asConverterFactory("application/json".toMediaType())
    }

    @Provides
    @Singleton
    fun provideOkhttpClient(
        cacheLoggingInterceptor: Interceptor,
        httpLoggingInterceptor: Interceptor,
        cache: Cache
    ): OkHttpClient {
        val okhttpBuilder = OkHttpClient.Builder()

        with(okhttpBuilder) {
            // Cache
            cache(cache)

            // Timeout
            val timeOutInSeconds = 10L
            connectTimeout(timeOutInSeconds, TimeUnit.SECONDS)
            readTimeout(timeOutInSeconds, TimeUnit.SECONDS)
            writeTimeout(timeOutInSeconds, TimeUnit.SECONDS)

            // Interceptors
            addInterceptor(cacheLoggingInterceptor)
            addInterceptor(httpLoggingInterceptor)
        }

        val policy = StrictMode.getThreadPolicy()
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        try {
            return okhttpBuilder.build()
        } finally {
            StrictMode.setThreadPolicy(policy)
        }
    }

    @Provides
    @Singleton
    fun provideNetworkCache(application: Application): Cache {
        val policy = StrictMode.getThreadPolicy()
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        try {
            val cacheDirectory = File(application.cacheDir, "http_cache")
            cacheDirectory.mkdirs()
            return Cache(cacheDirectory, 10L * 1024 * 1024)
        } finally {
            StrictMode.setThreadPolicy(policy)
        }
    }

    @Provides
    @Singleton
    fun provideCacheLoggingInterceptor(): Interceptor = Interceptor { chain ->
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.cacheResponse != null) {
            println(
                "🧠 Successful Response from MEMORY_CACHE\n" + "\t${request.method} ${request.url}"
            )
        } else if (response.networkResponse != null) {
            println(
                "☁️ Successful Response from NETWORK\n" + "\t${request.method} ${request.url}"
            )
        }
        return@Interceptor response
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): Interceptor {
        return JSONPrettyPrintHttpLoggingInterceptor()
    }
}
