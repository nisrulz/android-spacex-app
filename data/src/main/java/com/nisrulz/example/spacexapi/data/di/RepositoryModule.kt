package com.nisrulz.example.spacexapi.data.di

import com.nisrulz.example.spacexapi.data.local.SpaceXLaunchesDatabase
import com.nisrulz.example.spacexapi.data.remote.SpaceXLaunchesApi
import com.nisrulz.example.spacexapi.data.repository.LaunchesRepositoryImpl
import com.nisrulz.example.spacexapi.domain.repository.LaunchesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class RepositoryModule {
    @Provides
    @ViewModelScoped
    fun provideRepository(
        database: SpaceXLaunchesDatabase,
        api: SpaceXLaunchesApi,
    ): LaunchesRepository {
        return LaunchesRepositoryImpl(localDataSource = database.dao, remoteDataSource = api)
    }
}
