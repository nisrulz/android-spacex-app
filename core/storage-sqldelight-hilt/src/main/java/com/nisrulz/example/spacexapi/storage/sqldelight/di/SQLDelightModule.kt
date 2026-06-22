package com.nisrulz.example.spacexapi.storage.sqldelight.di

import android.app.Application
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.nisrulz.example.spacexapi.storage.LocalDataSource
import com.nisrulz.example.spacexapi.storage.sqldelight.SQLDelightLocalDataSource
import com.nisrulz.example.spacexapi.storage.sqldelight.SpaceXDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SQLDelightModule {

    @Provides
    @Singleton
    fun provideLocalDataSource(database: SpaceXDatabase): LocalDataSource {
        return SQLDelightLocalDataSource(database)
    }

    @Provides
    @Singleton
    fun provideDatabase(application: Application): SpaceXDatabase {
        val driver = AndroidSqliteDriver(
            schema = SpaceXDatabase.Schema,
            context = application,
            name = "spacex_launches.db"
        )
        return SpaceXDatabase(driver)
    }
}
