package com.nisrulz.example.spacexapi.utils

import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.nisrulz.example.spacexapi.BuildConfig

interface AnrDetector {
    fun setupAnrDetector()
}

class DefaultAnrDetector(
    private val debugBuild: Boolean = BuildConfig.DEBUG
) : AnrDetector {
    override fun setupAnrDetector() {
        if (debugBuild) {
            setupThreadPolicy()
            setupVmPolicy()
        }
    }

    private fun setupThreadPolicy() {
        val policy = StrictMode.ThreadPolicy.Builder()
            // Detect disk operations
            .detectDiskReads().detectDiskWrites()

            // Detect network operations on the main thread
            .detectNetwork()

            // Detect custom slow calls
            .detectCustomSlowCalls()

            // Print logs
            .penaltyLog()

            // Flash the screen when a violation occurs
            .penaltyFlashScreen().build()

        // Set the policy
        StrictMode.setThreadPolicy(policy)
    }

    private fun setupVmPolicy() {
        val policy = VmPolicy.Builder()
            // Detect leaked SQLite objects
            .detectLeakedSqlLiteObjects()

            // Detect leaked Closeable objects
            .detectLeakedClosableObjects()

            // Print logs
            .penaltyLog().build()

        // Set the policy
        StrictMode.setVmPolicy(policy)
    }
}