package com.nisrulz.example.spacexapi.common.utils

interface Logger {
    fun log(message: String)

    fun log(throwable: Throwable)
}

class DefaultLogger(
    private val debugBuild: Boolean,
) : Logger by SimpleLogger(debugBuild)

private class SimpleLogger(
    private val debugBuild: Boolean,
) : Logger {
    override fun log(message: String) {
        if (debugBuild) {
            println(message)
        }
    }

    override fun log(throwable: Throwable) {
        if (debugBuild) {
            println(throwable.localizedMessage)
            println("\n-----------------------------------------\n")
            println("\n========StackTrace========\n")
            throwable.printStackTrace()
            println("\n-----------------------------------------\n")
        }
    }
}
