package com.nisrulz.example.spacexapi.common.logger

import com.nisrulz.example.spacexapi.common.BuildConfig
import timber.log.Timber
import javax.inject.Inject

class TimberLogger
    @Inject
    constructor() : Logger {
        init {
            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            }
        }

        override fun log(message: String) {
            Timber.d(message)
        }

        override fun log(throwable: Throwable) {
            Timber.d(throwable)
        }
    }
