package com.nisrulz.example.spacexapi.analytics.di

import com.nisrulz.example.spacexapi.analytics.contract.AnalyticsService
import com.nisrulz.example.spacexapi.analytics.services.FirebaseAnalyticsService
import com.nisrulz.example.spacexapi.analytics.services.GoogleAnalyticsService
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
    fun provideAnalyticsServices(): Set<AnalyticsService> = setOf(
        GoogleAnalyticsService(),
        FirebaseAnalyticsService()
        // Add more services here
    )
}
