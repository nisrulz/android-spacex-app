package com.nisrulz.example.spacexapi.common.logger

interface Logger {
    fun log(message: String)

    fun log(throwable: Throwable)
}
