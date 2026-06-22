package com.nisrulz.example.spacexapi.network

import com.nisrulz.example.spacexapi.network.dto.LaunchInfoResponse

interface RemoteDataSource {
    suspend fun getAllLaunches(): List<LaunchInfoResponse>
}
