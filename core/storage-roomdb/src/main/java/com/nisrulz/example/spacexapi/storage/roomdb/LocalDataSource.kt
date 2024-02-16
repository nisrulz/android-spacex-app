package com.nisrulz.example.spacexapi.storage.roomdb

import com.nisrulz.example.spacexapi.storage.roomdb.entity.LaunchInfoEntity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getAll(): Flow<List<LaunchInfoEntity>>

    fun getAllBookmarked(): Flow<List<LaunchInfoEntity>>

    suspend fun getById(id: String): LaunchInfoEntity?

    suspend fun insert(launchInfoEntity: LaunchInfoEntity)

    suspend fun insertAll(listOfLaunchInfoEntities: List<LaunchInfoEntity>)

    suspend fun update(launchInfoEntity: LaunchInfoEntity)

    suspend fun delete(launchInfoEntity: LaunchInfoEntity)

    suspend fun deleteAll()
}
