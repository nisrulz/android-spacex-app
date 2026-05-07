package com.nisrulz.example.spacexapi.domain.repository

import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import kotlinx.coroutines.flow.Flow

interface LaunchesRepository {
    suspend fun getListOfLaunches(): Flow<List<LaunchInfo>>

    suspend fun getAllBookmarked(): Flow<List<LaunchInfo>>

    fun getLaunchDetail(id: String): Flow<LaunchInfo?>

    suspend fun setBookmark(
        id: String,
        value: Boolean,
    )
}
