package com.nisrulz.example.spacexapi.storage.roomdb

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.nisrulz.example.spacexapi.storage.roomdb.entity.LaunchInfoEntity
import com.nisrulz.example.spacexapi.storage.roomdb.util.TestFactory.buildListOfBookmarkedLaunchInoEntity
import com.nisrulz.example.spacexapi.storage.roomdb.util.TestFactory.buildListOfLaunchInfoEntity
import java.io.IOException
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LaunchInfoDaoTest {
    private lateinit var database: SpaceXLaunchesDatabase
    private lateinit var dao: LaunchInfoDao
    private lateinit var launchInfoEntityList: List<LaunchInfoEntity>

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database =
            Room.inMemoryDatabaseBuilder(
                context,
                SpaceXLaunchesDatabase::class.java
            ).build()

        dao = database.dao

        launchInfoEntityList = buildListOfLaunchInfoEntity()
    }

    @After
    @Throws(IOException::class)
    fun teardown() {
        database.close()
    }

    @Test
    fun insert() = com.nisrulz.example.spacexapi.storage.roomdb.util.runUnconfinedTest {
        // Insert an items
        val first = launchInfoEntityList.first()
        dao.insert(first)

        val expected = listOf(first)

        dao.getAll().test {
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun insertAll() = com.nisrulz.example.spacexapi.storage.roomdb.util.runUnconfinedTest {
        // Insert all items
        dao.insertAll(launchInfoEntityList)

        val expected = launchInfoEntityList

        dao.getAll().test {
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun delete() = com.nisrulz.example.spacexapi.storage.roomdb.util.runUnconfinedTest {
        // Insert 2 items
        val first = launchInfoEntityList.first()
        val second = launchInfoEntityList[1]
        dao.apply {
            insert(first)
            insert(second)
            delete(first)
        }
        val expected = listOf(second)

        dao.getAll().test {
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun deleteAll() = com.nisrulz.example.spacexapi.storage.roomdb.util.runUnconfinedTest {
        // Insert 2 items
        val first = launchInfoEntityList.first()
        val second = launchInfoEntityList[1]
        dao.apply {
            insert(first)
            insert(second)
            deleteAll()
        }

        dao.getAll().test {
            assertThat(awaitItem()).isEmpty()
        }
    }

    @Test
    fun getById() = com.nisrulz.example.spacexapi.storage.roomdb.util.runUnconfinedTest {
        // Insert 2 items
        val first = launchInfoEntityList.first()
        val second = launchInfoEntityList[1]
        dao.apply {
            insert(first)
            insert(second)
        }

        val result = dao.getById(second.id)
        assertThat(result).isEqualTo(second)
    }

    @Test
    fun getAll() = com.nisrulz.example.spacexapi.storage.roomdb.util.runUnconfinedTest {
        // Insert all items
        dao.insertAll(launchInfoEntityList)

        val expected = launchInfoEntityList

        dao.getAll().test {
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun update() = com.nisrulz.example.spacexapi.storage.roomdb.util.runUnconfinedTest {
        // Insert all items
        dao.insertAll(launchInfoEntityList)

        val launchInfo = launchInfoEntityList[1]

        // Check if isBookmarked is set to false initially
        assertThat(launchInfo.isBookmarked).isFalse()

        // Set isBookmarked to true
        val updatedLaunchInfoEntity = launchInfo.copy(isBookmarked = true)
        dao.update(updatedLaunchInfoEntity)

        // Check if isBookmarked is updated to true
        val result = dao.getById(updatedLaunchInfoEntity.id)
        assertThat(result?.isBookmarked).isTrue()
    }

    @Test
    fun getAllBookmarked() = com.nisrulz.example.spacexapi.storage.roomdb.util.runUnconfinedTest {
        // Insert all items
        dao.insertAll(launchInfoEntityList)

        val expected = buildListOfBookmarkedLaunchInoEntity()

        dao.getAllBookmarked().test {
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }
}
