package com.nisrulz.example.spacexapi.data.remote

import com.nisrulz.example.spacexapi.data.remote.dto.LaunchInfoResponse
import retrofit2.http.GET

interface SpaceXLaunchesApi : RemoteDataSource {
    @GET("v5/launches")
    override suspend fun getAllLaunches(): List<LaunchInfoResponse>

    companion object {
        const val BASE_URL = "https://api.spacexdata.com/"
    }
}
