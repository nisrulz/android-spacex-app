package com.nisrulz.example.spacexapi.common.di

import com.nisrulz.example.spacexapi.common.logger.Logger
import com.nisrulz.example.spacexapi.common.logger.SimpleLogger
import com.nisrulz.example.spacexapi.common.logger.TimberLogger
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
