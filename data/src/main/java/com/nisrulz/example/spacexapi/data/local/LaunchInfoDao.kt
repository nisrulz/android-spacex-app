package com.nisrulz.example.spacexapi.data.local

import androidx.room.*
import com.nisrulz.example.spacexapi.data.local.entity.LaunchInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LaunchInfoDao : LocalDataSource {

    @Query("SELECT * from LaunchInfoEntity")
    override fun getAll(): Flow<List<LaunchInfoEntity>>

    @Query("SELECT * from LaunchInfoEntity where isBookmarked = 1")
    override fun getAllBookmarked(): Flow<List<LaunchInfoEntity>>

    @Query("SELECT * from LaunchInfoEntity where id = :id")
    override suspend fun getById(id: String): LaunchInfoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertAll(listOfLaunchInfoEntities: List<LaunchInfoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(launchInfoEntity: LaunchInfoEntity)

    @Update
    override suspend fun update(launchInfoEntity: LaunchInfoEntity)

    @Delete
    override suspend fun delete(launchInfoEntity: LaunchInfoEntity)

    @Query("DELETE FROM LaunchInfoEntity")
    override suspend fun deleteAll()


}