package com.nisrulz.example.spacexapi.network.retrofit

import com.nisrulz.example.spacexapi.network.retrofit.dto.LaunchInfoResponse

interface RemoteDataSource {
    suspend fun getAllLaunches(): List<LaunchInfoResponse>
}
