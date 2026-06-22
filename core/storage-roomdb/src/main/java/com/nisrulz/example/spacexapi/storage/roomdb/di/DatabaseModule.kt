package com.nisrulz.example.spacexapi.storage.roomdb.di

import android.app.Application
import androidx.room.Room
import com.nisrulz.example.spacexapi.storage.LocalDataSource
import com.nisrulz.example.spacexapi.storage.roomdb.RoomLocalDataSource
import com.nisrulz.example.spacexapi.storage.roomdb.SpaceXLaunchesDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindLocalDataSource(impl: RoomLocalDataSource): LocalDataSource

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(application: Application): SpaceXLaunchesDatabase {
            return Room.databaseBuilder(
                application,
                klass = SpaceXLaunchesDatabase::class.java,
                name = SpaceXLaunchesDatabase.DATABASE_NAME
            ).fallbackToDestructiveMigration().build()
        }

        @Provides
        @Singleton
        fun provideLaunchInfoDao(database: SpaceXLaunchesDatabase) = database.dao
    }
}
