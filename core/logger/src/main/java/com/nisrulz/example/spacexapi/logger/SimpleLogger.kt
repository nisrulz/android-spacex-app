package com.nisrulz.example.spacexapi.logger

import android.util.Log
import javax.inject.Inject

class SimpleLogger
@Inject constructor() : Logger {
    override fun log(message: String) {
        if (BuildConfig.DEBUG) {
            simpleLog(message)
        }
    }

    override fun log(throwable: Throwable) {
        if (BuildConfig.DEBUG) {
            throwable.localizedMessage?.let { simpleLog(it) }
            simpleLog("\n-----------------------------------------\n")
            simpleLog("\n========StackTrace========\n")
            throwable.printStackTrace()
            simpleLog("\n-----------------------------------------\n")
        }
    }

    private fun simpleLog(message: String) {
        Log.i("SimpleLogger", message)
    }
}
