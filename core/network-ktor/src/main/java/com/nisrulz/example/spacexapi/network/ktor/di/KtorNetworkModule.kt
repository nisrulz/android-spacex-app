package com.nisrulz.example.spacexapi.network.ktor.di

import com.nisrulz.example.spacexapi.network.RemoteDataSource
import com.nisrulz.example.spacexapi.network.ktor.BuildConfig
import com.nisrulz.example.spacexapi.network.ktor.KtorApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.takeFrom
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class KtorNetworkModule {

    @Binds
    @Singleton
    abstract fun bindRemoteDataSource(impl: KtorApi): RemoteDataSource

    companion object {
        @Provides
        @Singleton
        fun provideHttpClient(): HttpClient {
            return HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(Json {
                        coerceInputValues = true
                        ignoreUnknownKeys = true
                    })
                }

                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            Timber.d(message)
                        }
                    }
                    level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
                }

                defaultRequest {
                    url.takeFrom(BuildConfig.API_BASE_URL)
                }

                engine {
                    config {
                        connectTimeout(10, TimeUnit.SECONDS)
                        readTimeout(10, TimeUnit.SECONDS)
                        writeTimeout(10, TimeUnit.SECONDS)
                    }
                }
            }
        }
    }
}
