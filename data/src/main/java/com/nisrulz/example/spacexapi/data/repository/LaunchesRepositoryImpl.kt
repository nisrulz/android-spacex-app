package com.nisrulz.example.spacexapi.data.repository

import com.nisrulz.example.spacexapi.common.contract.utils.NetworkUtils
import com.nisrulz.example.spacexapi.data.mapper.mapToDomainModel
import com.nisrulz.example.spacexapi.data.mapper.mapToDomainModelList
import com.nisrulz.example.spacexapi.data.mapper.toEntityList
import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.domain.repository.LaunchesRepository
import com.nisrulz.example.spacexapi.network.retrofit.RemoteDataSource
import com.nisrulz.example.spacexapi.storage.roomdb.LocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LaunchesRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val networkUtils: NetworkUtils
) : LaunchesRepository {
    override suspend fun getListOfLaunches(): Flow<List<LaunchInfo>> {
        // 1. Get list of all bookmarked launches
        val localBookmarkedLaunches = getAllBookmarked().first()

        if (networkUtils.isInternetAvailable()) {
            // 2. Fetch list of launches from remote API
            val apiResponse = remoteDataSource.getAllLaunches()

            // 3. Update local db with updated remote response
            if (apiResponse.isNotEmpty()) {
                localDataSource.deleteAll()
                localDataSource.insertAll(apiResponse.toEntityList())

                // Update bookmarked state
                localBookmarkedLaunches.forEach { bookmarked ->
                    setBookmark(bookmarked.id, bookmarked.isBookmarked)
                }
            }
        }

        // 4. Fetch the list of launches from local db
        return localDataSource.getAll().map { it.mapToDomainModelList() }
    }

    override suspend fun getLaunchDetail(id: String): LaunchInfo? {
        return localDataSource.getById(id)?.mapToDomainModel()
    }

    override suspend fun getAllBookmarked(): Flow<List<LaunchInfo>> {
        return localDataSource.getAllBookmarked().map { it.mapToDomainModelList() }
    }

    override suspend fun setBookmark(id: String, value: Boolean) {
        val launch = localDataSource.getById(id)
        launch?.copy(isBookmarked = value)?.let {
            localDataSource.update(it)
        }
    }
}
