package com.nisrulz.example.spacexapi.data.di

import android.app.Application
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.nisrulz.example.spacexapi.data.remote.SpaceXLaunchesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
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
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        jsonConverter: Converter.Factory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SpaceXLaunchesApi.BASE_URL)
            .addConverterFactory(jsonConverter)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideConvertorFactory(): Converter.Factory {
        val json =
            Json {
                coerceInputValues = true
            }
        return json.asConverterFactory("application/json".toMediaType())
    }

    @Provides
    @Singleton
    fun provideOkhttpClient(
        cacheLoggingInterceptor: Interceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        cache: Cache,
    ) = OkHttpClient
        .Builder()
        .cache(cache)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(cacheLoggingInterceptor)
        .addInterceptor(httpLoggingInterceptor)
        .build()

    @Provides
    @Singleton
    fun provideNetworkCache(application: Application): Cache {
        val cacheDirectory = File(application.cacheDir, "http_cache")
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        return Cache(cacheDirectory, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideCacheLoggingInterceptor(): Interceptor =
        Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            if (response.cacheResponse != null) {
                println(
                    "🧠 Successful Response from MEMORY_CACHE\n" +
                        "\t${request.method} ${request.url}",
                )
            } else if (response.networkResponse != null) {
                println(
                    "☁️ Successful Response from NETWORK\n" +
                        "\t${request.method} ${request.url}",
                )
            }
            return@Interceptor response
        }

    @Provides
    @Singleton
    fun provideLoggingInterceptor() =
        HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
}
