package com.nisrulz.example.spacexapi.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.*
import com.nisrulz.example.spacexapi.data.local.entity.LaunchInfoEntity
import com.nisrulz.example.spacexapi.data.util.TestFactory
import com.nisrulz.example.spacexapi.data.util.TestFactory.buildListOfLaunchInfoEntity
import com.nisrulz.example.spacexapi.data.util.runUnconfinedTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class LaunchInfoDaoTest {

    private lateinit var database: SpaceXLaunchesDatabase
    private lateinit var dao: LaunchInfoDao
    private lateinit var launchInfoEntityList: List<LaunchInfoEntity>

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            SpaceXLaunchesDatabase::class.java,
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
    fun insert() = runUnconfinedTest {
        // Insert an items
        val first = launchInfoEntityList.first()
        dao.insert(first)

        val expected = listOf(first)

        dao.getAll().test {
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun insertAll() = runUnconfinedTest {
        // Insert all items
        dao.insertAll(launchInfoEntityList)

        val expected = launchInfoEntityList

        dao.getAll().test {
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }


    @Test
    fun delete() = runUnconfinedTest {
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
    fun deleteAll() = runUnconfinedTest {
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
    fun getById() = runUnconfinedTest {
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
    fun getAll() = runUnconfinedTest {
        // Insert all items
        dao.insertAll(launchInfoEntityList)

        val expected = launchInfoEntityList

        dao.getAll().test {
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }


    @Test
    fun update() = runUnconfinedTest {
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
    fun getAllBookmarked() = runUnconfinedTest {
        // Insert all items
        dao.insertAll(launchInfoEntityList)

        val expected = TestFactory.buildListOfBookmarkedLaunchInoEntity()

        dao.getAllBookmarked().test {
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }
}