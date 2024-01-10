package com.nisrulz.example.spacexapi.domain.model

import com.google.common.truth.Truth.assertThat
import com.nisrulz.example.spacexapi.domain.util.TestFactory
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LaunchInfoTest {
    private lateinit var launchInfo: LaunchInfo

    @Before
    fun setup() {
        launchInfo = TestFactory.buildLaunchInfo()
    }

    @Test
    fun `getFormattedDate on correct parsing of date string`() {
        // Given
        val dateLocal = "2008-08-03T15:34:00+12:00"
        launchInfo = launchInfo.copy(date_local = dateLocal)
        val expected = "03-08-2008"

        // When
        val result = launchInfo.getFormattedDate()

        // Then
        assertEquals(expected, result)
    }

    @Test
    fun `getFormattedDate on incorrect parsing of date string returns empty string`() {
        // Given
        val dateLocal = "InvalidDateString"
        launchInfo = launchInfo.copy(date_local = dateLocal)
        val expected = ""

        // When
        val result = launchInfo.getFormattedDate()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `getDetailsString when null returns -`() {
        // Given
        launchInfo = launchInfo.copy(details = null)
        val expected = "-"

        // When
        val result = launchInfo.getDetailsString()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `getDetailsString when not null returns correct string`() {
        // Given
        launchInfo = launchInfo.copy(details = "Details")
        val expected = "Details"

        // When
        val result = launchInfo.getDetailsString()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `wasSuccessfulString when success is true returns Yes`() {
        // Given
        launchInfo = launchInfo.copy(success = true)
        val expected = "Yes"

        // When
        val result = launchInfo.wasSuccessfulString()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `wasSuccessfulString when success is false returns No`() {
        // Given
        launchInfo = launchInfo.copy(success = false)
        val expected = "No"

        // When
        val result = launchInfo.wasSuccessfulString()

        // Then
        assertThat(result).isEqualTo(expected)
    }
}
