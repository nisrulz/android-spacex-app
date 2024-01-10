package com.nisrulz.example.spacexapi.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DispatcherModule {
    @Provides
    @Singleton
    fun providesDispatcher() = Dispatchers.IO
}
