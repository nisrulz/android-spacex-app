package com.nisrulz.example.spacexapi.storage.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.nisrulz.example.spacexapi.storage.roomdb.entity.LaunchInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LaunchInfoDao : LocalDataSource {
    @Query("SELECT * from LaunchInfoEntity")
    override fun getAll(): Flow<List<LaunchInfoEntity>>

    @Query("SELECT * from LaunchInfoEntity where isBookmarked = 1")
    override fun getAllBookmarked(): Flow<List<LaunchInfoEntity>>

    @Query("SELECT * from LaunchInfoEntity where id = :id")
    override fun observeById(id: String): Flow<LaunchInfoEntity?>

    @Query("SELECT * from LaunchInfoEntity where id = :id")
    override suspend fun getById(id: String): LaunchInfoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertAll(listOfLaunchInfoEntities: List<LaunchInfoEntity>)

    @Transaction
    override suspend fun replaceAllPreservingBookmarks(listOfLaunchInfoEntities: List<LaunchInfoEntity>) {
        val bookmarkedIds = getBookmarkedIds().toSet()
        deleteAll()
        insertAll(
            listOfLaunchInfoEntities.map { launchInfoEntity ->
                launchInfoEntity.copy(isBookmarked = launchInfoEntity.id in bookmarkedIds)
            },
        )
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(launchInfoEntity: LaunchInfoEntity)

    @Update
    override suspend fun update(launchInfoEntity: LaunchInfoEntity)

    @Delete
    override suspend fun delete(launchInfoEntity: LaunchInfoEntity)

    @Query("SELECT id from LaunchInfoEntity where isBookmarked = 1")
    suspend fun getBookmarkedIds(): List<String>

    @Query("DELETE FROM LaunchInfoEntity")
    override suspend fun deleteAll()
}
