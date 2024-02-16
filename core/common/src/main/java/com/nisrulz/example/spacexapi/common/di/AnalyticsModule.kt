package com.nisrulz.example.spacexapi.common.di

import com.nisrulz.example.spacexapi.common.analytics.AnalyticsService
import com.nisrulz.example.spacexapi.common.analytics.FirebaseAnalyticsService
import com.nisrulz.example.spacexapi.common.analytics.GoogleAnalyticsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet

@Module
@InstallIn(SingletonComponent::class)
class AnalyticsModule {
    @Provides
    @ElementsIntoSet
    fun provideAnalyticsServices(): Set<AnalyticsService> =
        setOf(
            GoogleAnalyticsService(),
            FirebaseAnalyticsService(),
            // Add more services here
        )
}
