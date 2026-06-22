package com.nisrulz.example.spacexapi.network.ktor

import com.nisrulz.example.spacexapi.network.RemoteDataSource
import com.nisrulz.example.spacexapi.network.dto.LaunchInfoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class KtorApi @Inject constructor(
    private val httpClient: HttpClient
) : RemoteDataSource {

    override suspend fun getAllLaunches(): List<LaunchInfoResponse> {
        return httpClient.get("v5/launches").body()
    }
}
