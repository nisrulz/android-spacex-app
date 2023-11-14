package com.nisrulz.example.spacexapi.utils

import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.nisrulz.example.spacexapi.BuildConfig

interface CoilCache : ImageLoaderFactory {
    fun setupCoilCache(context: Context)
}

class DefaultCoilCache(private val debugBuild: Boolean = BuildConfig.DEBUG) : CoilCache {

    private lateinit var context: Context
    override fun setupCoilCache(context: Context) {
        this.context = context
    }

    override fun newImageLoader(): ImageLoader {
        val builder = ImageLoader(context).newBuilder()
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.1)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .maxSizePercent(0.03)
                    .directory(context.cacheDir)
                    .build()
            }

        if (debugBuild) {
            builder.logger(DebugLogger())
        }

        return builder.build()
    }
}


