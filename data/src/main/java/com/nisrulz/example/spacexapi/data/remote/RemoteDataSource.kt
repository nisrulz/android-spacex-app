package com.nisrulz.example.spacexapi.data.remote

import com.nisrulz.example.spacexapi.data.remote.dto.LaunchInfoResponse

interface RemoteDataSource {

    suspend fun getAllLaunches(): List<LaunchInfoResponse>
}