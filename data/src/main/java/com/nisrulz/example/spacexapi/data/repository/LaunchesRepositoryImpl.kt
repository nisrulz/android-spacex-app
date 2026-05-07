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
import kotlinx.coroutines.flow.map

class LaunchesRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val networkUtils: NetworkUtils
) : LaunchesRepository {
    override suspend fun getListOfLaunches(): Flow<List<LaunchInfo>> {
        if (networkUtils.isInternetAvailable()) {
            runCatching {
                val apiResponse = remoteDataSource.getAllLaunches()

                if (apiResponse.isNotEmpty()) {
                    localDataSource.replaceAllPreservingBookmarks(apiResponse.toEntityList())
                }
            }
        }

        return localDataSource.getAll().map { it.mapToDomainModelList() }
    }

    override fun getLaunchDetail(id: String): Flow<LaunchInfo?> =
        localDataSource.observeById(id).map { it?.mapToDomainModel() }

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
