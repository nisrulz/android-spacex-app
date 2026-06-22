package com.nisrulz.example.spacexapi.network.retrofit.di

import android.app.Application
import android.os.StrictMode
import com.nisrulz.example.spacexapi.network.RemoteDataSource
import com.nisrulz.example.spacexapi.network.retrofit.BuildConfig
import com.nisrulz.example.spacexapi.network.retrofit.SpaceXLaunchesApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Cache
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
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindRemoteDataSource(impl: SpaceXLaunchesApi): RemoteDataSource

    companion object {
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
                ignoreUnknownKeys = true
            }
            return json.asConverterFactory("application/json".toMediaType())
        }

        @Provides
        @Singleton
        internal fun provideOkhttpClient(
            cacheLoggingInterceptor: CacheLoggingInterceptor,
            httpLoggingInterceptor: JSONPrettyPrintHttpLoggingInterceptor,
            cache: Cache
        ): OkHttpClient {
            val okhttpBuilder = OkHttpClient.Builder()

            with(okhttpBuilder) {
                cache(cache)
                val timeOutInSeconds = 10L
                connectTimeout(timeOutInSeconds, TimeUnit.SECONDS)
                readTimeout(timeOutInSeconds, TimeUnit.SECONDS)
                writeTimeout(timeOutInSeconds, TimeUnit.SECONDS)
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
        internal fun provideCacheLoggingInterceptor(): CacheLoggingInterceptor {
            return CacheLoggingInterceptor()
        }

        @Provides
        @Singleton
        internal fun provideLoggingInterceptor(): JSONPrettyPrintHttpLoggingInterceptor {
            return JSONPrettyPrintHttpLoggingInterceptor()
        }
    }
}
