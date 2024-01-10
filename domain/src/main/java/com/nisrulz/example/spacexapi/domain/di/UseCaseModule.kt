package com.nisrulz.example.spacexapi.domain.di

import com.nisrulz.example.spacexapi.domain.repository.LaunchesRepository
import com.nisrulz.example.spacexapi.domain.usecase.GetAllBookmarkedLaunches
import com.nisrulz.example.spacexapi.domain.usecase.GetAllLaunches
import com.nisrulz.example.spacexapi.domain.usecase.GetLaunchDetail
import com.nisrulz.example.spacexapi.domain.usecase.ToggleBookmarkLaunchInfo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class UseCaseModule {
    @Provides
    @ViewModelScoped
    fun provideGetAllLaunchesUseCase(repository: LaunchesRepository): GetAllLaunches {
        return GetAllLaunches(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAllBookmarkedLaunchesUseCase(repository: LaunchesRepository): GetAllBookmarkedLaunches {
        return GetAllBookmarkedLaunches(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetLaunchDetailUseCase(repository: LaunchesRepository): GetLaunchDetail {
        return GetLaunchDetail(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideBookmarkLaunchInfoUseCase(repository: LaunchesRepository): ToggleBookmarkLaunchInfo {
        return ToggleBookmarkLaunchInfo(repository)
    }
}
