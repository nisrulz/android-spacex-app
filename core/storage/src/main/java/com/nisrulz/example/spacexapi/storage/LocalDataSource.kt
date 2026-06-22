package com.nisrulz.example.spacexapi.storage

import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getAll(): Flow<List<LaunchInfo>>

    fun getAllBookmarked(): Flow<List<LaunchInfo>>

    fun observeById(id: String): Flow<LaunchInfo?>

    suspend fun getById(id: String): LaunchInfo?

    suspend fun insert(launchInfo: LaunchInfo)

    suspend fun insertAll(launchInfoList: List<LaunchInfo>)

    suspend fun replaceAllPreservingBookmarks(launchInfoList: List<LaunchInfo>)

    suspend fun update(launchInfo: LaunchInfo)

    suspend fun delete(launchInfo: LaunchInfo)

    suspend fun deleteAll()
}
