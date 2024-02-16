package com.nisrulz.example.spacexapi.storage.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nisrulz.example.spacexapi.storage.roomdb.entity.LaunchInfoEntity

@Database(
    entities = [LaunchInfoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SpaceXLaunchesDatabase : RoomDatabase() {
    abstract val dao: LaunchInfoDao

    companion object {
        const val DATABASE_NAME = "spacex_launches_db"
    }
}
