package com.nisrulz.example.spacexapi.data.di

import android.app.Application
import androidx.room.Room
import com.nisrulz.example.spacexapi.data.local.SpaceXLaunchesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(application: Application): SpaceXLaunchesDatabase {
        return Room.databaseBuilder(
            application,
            klass = SpaceXLaunchesDatabase::class.java,
            name = SpaceXLaunchesDatabase.DATABASE_NAME,
        ).build()
    }
}
