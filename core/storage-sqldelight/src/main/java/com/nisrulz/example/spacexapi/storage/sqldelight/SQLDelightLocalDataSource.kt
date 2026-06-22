package com.nisrulz.example.spacexapi.storage.sqldelight

import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.storage.LocalDataSource
import com.nisrulz.example.spacexapi.storage.sqldelight.mapper.toDomainModel
import com.nisrulz.example.spacexapi.storage.sqldelight.mapper.toDomainModelList
import com.nisrulz.example.spacexapi.storage.sqldelight.mapper.toEntity
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SQLDelightLocalDataSource(
    private val database: SpaceXDatabase
) : LocalDataSource {

    private val queries = database.launchInfoQueries

    override fun getAll(): Flow<List<LaunchInfo>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { it.toDomainModelList() }
    }

    override fun getAllBookmarked(): Flow<List<LaunchInfo>> {
        return queries.selectBookmarked()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { it.toDomainModelList() }
    }

    override fun observeById(id: String): Flow<LaunchInfo?> {
        return queries.selectById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomainModel() }
    }

    override suspend fun getById(id: String): LaunchInfo? {
        return queries.selectById(id).executeAsOneOrNull()?.toDomainModel()
    }

    override suspend fun insert(launchInfo: LaunchInfo) {
        val entity = launchInfo.toEntity()
        queries.insertOrReplace(
            id = entity.id,
            date_local = entity.date_local,
            details = entity.details,
            flight_number = entity.flight_number,
            logo = entity.logo,
            name = entity.name,
            success = entity.success,
            isBookmarked = entity.isBookmarked
        )
    }

    override suspend fun insertAll(launchInfoList: List<LaunchInfo>) {
        database.transaction {
            launchInfoList.forEach { launchInfo ->
                val entity = launchInfo.toEntity()
                queries.insertOrReplace(
                    id = entity.id,
                    date_local = entity.date_local,
                    details = entity.details,
                    flight_number = entity.flight_number,
                    logo = entity.logo,
                    name = entity.name,
                    success = entity.success,
                    isBookmarked = entity.isBookmarked
                )
            }
        }
    }

    override suspend fun replaceAllPreservingBookmarks(launchInfoList: List<LaunchInfo>) {
        database.transaction {
            val bookmarkedIds = queries.selectBookmarkedIds().executeAsList().toSet()
            queries.deleteAll()
            launchInfoList.forEach { launchInfo ->
                val entity = launchInfo.toEntity().let {
                    it.copy(isBookmarked = if (it.id in bookmarkedIds) 1L else 0L)
                }
                queries.insertOrReplace(
                    id = entity.id,
                    date_local = entity.date_local,
                    details = entity.details,
                    flight_number = entity.flight_number,
                    logo = entity.logo,
                    name = entity.name,
                    success = entity.success,
                    isBookmarked = entity.isBookmarked
                )
            }
        }
    }

    override suspend fun update(launchInfo: LaunchInfo) {
        val entity = launchInfo.toEntity()
        queries.updateLaunch(
            date_local = entity.date_local,
            details = entity.details,
            flight_number = entity.flight_number,
            logo = entity.logo,
            name = entity.name,
            success = entity.success,
            isBookmarked = entity.isBookmarked,
            id = entity.id
        )
    }

    override suspend fun delete(launchInfo: LaunchInfo) {
        queries.deleteLaunch(launchInfo.id)
    }

    override suspend fun deleteAll() {
        queries.deleteAll()
    }
}
