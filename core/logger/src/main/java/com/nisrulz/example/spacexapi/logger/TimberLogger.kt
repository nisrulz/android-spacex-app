package com.nisrulz.example.spacexapi.logger

import javax.inject.Inject
import timber.log.Timber

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
