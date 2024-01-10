package com.nisrulz.example.spacexapi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nisrulz.example.spacexapi.data.local.entity.LaunchInfoEntity

@Database(
    entities = [LaunchInfoEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class SpaceXLaunchesDatabase : RoomDatabase() {
    abstract val dao: LaunchInfoDao

    companion object {
        const val DATABASE_NAME = "spacex_launches_db"
    }
}
