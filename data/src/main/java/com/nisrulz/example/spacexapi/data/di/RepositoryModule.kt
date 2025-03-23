package com.nisrulz.example.spacexapi.data.di

import android.content.Context
import com.nisrulz.example.spacexapi.common.contract.utils.NetworkUtils
import com.nisrulz.example.spacexapi.data.repository.LaunchesRepositoryImpl
import com.nisrulz.example.spacexapi.domain.repository.LaunchesRepository
import com.nisrulz.example.spacexapi.network.retrofit.SpaceXLaunchesApi
import com.nisrulz.example.spacexapi.storage.roomdb.SpaceXLaunchesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class RepositoryModule {
    @Provides
    @ViewModelScoped
    fun provideRepository(
        database: SpaceXLaunchesDatabase,
        api: SpaceXLaunchesApi,
        networkUtils: NetworkUtils
    ): LaunchesRepository {
        return LaunchesRepositoryImpl(
            localDataSource = database.dao,
            remoteDataSource = api,
            networkUtils = networkUtils
        )
    }

    @Provides
    @ViewModelScoped
    fun provideNetworkUtils(
        @ApplicationContext context: Context
    ) = NetworkUtils(context.applicationContext)
}
