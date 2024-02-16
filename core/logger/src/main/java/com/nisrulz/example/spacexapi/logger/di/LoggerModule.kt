package com.nisrulz.example.spacexapi.logger.di

import com.nisrulz.example.spacexapi.logger.Logger
import com.nisrulz.example.spacexapi.logger.SimpleLogger
import com.nisrulz.example.spacexapi.logger.TimberLogger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class LoggerModule {
    @Binds
    @IntoSet
    abstract fun bindTimber(timberLogger: TimberLogger): Logger

    @Binds
    @IntoSet
    abstract fun bindSimple(simpleLogger: SimpleLogger): Logger
}
