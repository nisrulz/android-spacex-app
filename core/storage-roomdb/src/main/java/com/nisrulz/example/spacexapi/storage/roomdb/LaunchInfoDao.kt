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
interface LaunchInfoDao {
    @Query("SELECT * from LaunchInfoEntity")
    fun getAll(): Flow<List<LaunchInfoEntity>>

    @Query("SELECT * from LaunchInfoEntity where isBookmarked = 1")
    fun getAllBookmarked(): Flow<List<LaunchInfoEntity>>

    @Query("SELECT * from LaunchInfoEntity where id = :id")
    fun observeById(id: String): Flow<LaunchInfoEntity?>

    @Query("SELECT * from LaunchInfoEntity where id = :id")
    suspend fun getById(id: String): LaunchInfoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(listOfLaunchInfoEntities: List<LaunchInfoEntity>)

    @Transaction
    suspend fun replaceAllPreservingBookmarks(listOfLaunchInfoEntities: List<LaunchInfoEntity>) {
        val bookmarkedIds = getBookmarkedIds().toSet()
        deleteAll()
        insertAll(
            listOfLaunchInfoEntities.map { launchInfoEntity ->
                launchInfoEntity.copy(isBookmarked = launchInfoEntity.id in bookmarkedIds)
            },
        )
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(launchInfoEntity: LaunchInfoEntity)

    @Update
    suspend fun update(launchInfoEntity: LaunchInfoEntity)

    @Delete
    suspend fun delete(launchInfoEntity: LaunchInfoEntity)

    @Query("SELECT id from LaunchInfoEntity where isBookmarked = 1")
    suspend fun getBookmarkedIds(): List<String>

    @Query("DELETE FROM LaunchInfoEntity")
    suspend fun deleteAll()
}
