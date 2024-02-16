package com.nisrulz.example.spacexapi.logger

import javax.inject.Inject

class InUseLoggers
@Inject
constructor(
    private val loggers: Set<@JvmSuppressWildcards Logger>
) {
    fun log(message: String) {
        loggers.forEach {
            it.log(message)
        }
    }

    fun log(throwable: Throwable) {
        loggers.forEach {
            it.log(throwable)
        }
    }
}
