package com.nisrulz.example.spacexapi.logger

interface Logger {
    fun log(message: String)

    fun log(throwable: Throwable)
}
