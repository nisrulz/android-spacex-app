package com.nisrulz.example.spacexapi.storage.roomdb

import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.storage.LocalDataSource
import com.nisrulz.example.spacexapi.storage.roomdb.mapper.toDomainModel
import com.nisrulz.example.spacexapi.storage.roomdb.mapper.toDomainModelList
import com.nisrulz.example.spacexapi.storage.roomdb.mapper.toEntity
import com.nisrulz.example.spacexapi.storage.roomdb.mapper.toEntityList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomLocalDataSource @Inject constructor(
    private val dao: LaunchInfoDao
) : LocalDataSource {

    override fun getAll(): Flow<List<LaunchInfo>> {
        return dao.getAll().map { entities ->
            entities.toDomainModelList()
        }
    }

    override fun getAllBookmarked(): Flow<List<LaunchInfo>> {
        return dao.getAllBookmarked().map { entities ->
            entities.toDomainModelList()
        }
    }

    override fun observeById(id: String): Flow<LaunchInfo?> {
        return dao.observeById(id).map { entity ->
            entity?.toDomainModel()
        }
    }

    override suspend fun getById(id: String): LaunchInfo? {
        return dao.getById(id)?.toDomainModel()
    }

    override suspend fun insert(launchInfo: LaunchInfo) {
        dao.insert(launchInfo.toEntity())
    }

    override suspend fun insertAll(launchInfoList: List<LaunchInfo>) {
        dao.insertAll(launchInfoList.toEntityList())
    }

    override suspend fun replaceAllPreservingBookmarks(launchInfoList: List<LaunchInfo>) {
        dao.replaceAllPreservingBookmarks(launchInfoList.toEntityList())
    }

    override suspend fun update(launchInfo: LaunchInfo) {
        dao.update(launchInfo.toEntity())
    }

    override suspend fun delete(launchInfo: LaunchInfo) {
        dao.delete(launchInfo.toEntity())
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }
}
